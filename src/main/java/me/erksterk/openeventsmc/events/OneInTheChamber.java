package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.misc.EventStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;


public class OneInTheChamber extends Event {

    private BukkitTask taskGame = null;
    private int schedulerSeconds = 0;

    public int gamelength = 10;
    public int winscore = 0;

    public HashMap<Player, Integer> kills = new HashMap<>();


    public OneInTheChamber(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.player");
        requiredFields.add("config.gamelength");
        requiredFields.add("config.winscore");
    }


    public Player getLeadingPlayer() {
        Player l = null;
        for (Player p : kills.keySet()) {
            if (l == null) {
                l = p;
            } else {
                int s = kills.get(l);
                int n = kills.get(p);
                if (n > s) {
                    l = p;
                }
            }

        }
        return l;
    }

    public Player getPlayerOnScoreboardPosition(int pos) {
        HashMap<Player, Integer> newkill = (HashMap<Player, Integer>) kills.clone();
        for (int i = 0; i > pos; i++) {
            Player l = null;
            for (Player p : newkill.keySet()) {
                if (l == null) {
                    l = p;
                } else {
                    int s = newkill.get(l);
                    int n = newkill.get(p);
                    if (n > s) {
                        l = p;
                    }
                }

            }
            newkill.remove(l);
        }
        Player l = null;
        for (Player p : newkill.keySet()) {
            if (l == null) {
                l = p;
            } else {
                int s = newkill.get(l);
                int n = newkill.get(p);
                if (n > s) {
                    l = p;
                }
            }

        }
        return l;

    }

    //TODO: make a proper scoreboard manager
    public void scoreboardUpdate() {
        for (Player p : getPlayers()) {
            Scoreboard sidebar = Bukkit.getScoreboardManager().getNewScoreboard(); // sidebar is the name of the scoreboard


            Objective objective = sidebar.registerNewObjective("test", "dummy"); // Just supposed to be, doesn't actually add anything
            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', "Event")); // The name that shows up on the top of the sidebar
            objective.setDisplaySlot(DisplaySlot.SIDEBAR); // setting it as sidebar


            int pos = 0;
            for (String s : Config.oitc_scoreboard) {
                try {

                    String score = s;
                    if (score != null) {
                        //TODO: this code is fucking retarded, find a better way to do this.
                        Player p0 = getLeadingPlayer();
                        Player p1 = getPlayerOnScoreboardPosition(1);
                        Player p2 = getPlayerOnScoreboardPosition(2);
                        Player p3 = getPlayerOnScoreboardPosition(3);
                        Player p4 = getPlayerOnScoreboardPosition(4);


                        if(p0!=null) {
                            score = score.replace("%player1%", p0.getName());
                            score = score.replace("%p1stat%", kills.get(p0).toString());
                        }else{
                            score = score.replace("%player1%", "null");
                            score = score.replace("%p1stat%", "0");
                        }
                        if(p1!=null) {
                            score = score.replace("%player2%", p1.getName());
                            score = score.replace("%p2stat%", kills.get(p1).toString());
                        }else{
                            score = score.replace("%player2%", "null");
                            score = score.replace("%p2stat%", "0");
                        }
                        if(p2!=null) {
                            score = score.replace("%player3%", p2.getName());
                            score = score.replace("%p3stat%", kills.get(p2).toString());
                        }else{
                            score = score.replace("%player3%", "null");
                            score = score.replace("%p3stat%", "0");
                        }
                        if(p3!=null) {
                            score = score.replace("%player4%", p3.getName());
                            score = score.replace("%p4stat%", kills.get(p3).toString());
                        }else{
                            score = score.replace("%player4%", "null");
                            score = score.replace("%p4stat%", "0");
                        }
                        if(p4!=null) {
                            score = score.replace("%player5%", p4.getName());
                            score = score.replace("%p5stat%", kills.get(p4).toString());
                        }else{
                            score = score.replace("%player5%", "null");
                            score = score.replace("%p5stat%", "0");
                        }



                        if(kills.containsKey(p)) {
                            score = score.replace("%pstats%",kills.get(p).toString());
                        }else{
                            score = score.replace("%pstats%","0");
                        }
                        score = ChatColor.translateAlternateColorCodes('&', score);
                        objective.getScore(score).setScore(pos); //sets the score on the scoreboard to the string.
                        pos++;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Config error for scoreboard!");
                    break;
                }
            }

            p.setScoreboard(sidebar);
        }
    }


    public void start() {
        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {
            if (running) {
                setStatus(EventStatus.RUNNING);
                schedulerSeconds++;
                if (schedulerSeconds > gamelength) {
                    //We should end the event and announce the player with the most points
                    if (kills.get(getLeadingPlayer()) >= winscore) {
                        Bukkit.broadcastMessage(getLeadingPlayer().getName() + " has won!");
                    }
                } else {
                    Bukkit.getScheduler().runTask(Main.plugin, () -> {
                        scoreboardUpdate();
                    });

                    if(getLeadingPlayer()!=null) {
                        if(kills.containsKey(getLeadingPlayer())) {
                            if (kills.get(getLeadingPlayer()) >= winscore) {
                                Bukkit.broadcastMessage(getLeadingPlayer().getName() + " has won!");
                            }
                        }
                    }
                }
            }
        }, 0, 20);

    }
}
