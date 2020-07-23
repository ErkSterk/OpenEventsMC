package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.Region;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;


public class OneInTheChamber extends Event {

    private BukkitTask taskGame = null;
    private int schedulerSeconds = 0;

    public int gamelength = 10;
    public int winscore = 0;
    public int respawn_item_give_delay = 5;

    public HashMap<Player, Integer> kills = new HashMap<>();


    public OneInTheChamber(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.player");
        requiredFields.add("arena.wait");
        requiredFields.add("config.gamelength");
        requiredFields.add("config.winscore");
        requiredFields.add("config.respawn_item_give_delay");
        requiredFields.add("inventory.start_gear");
        requiredFields.add("inventory.respawn_gear");
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
    public static HashMap<Player, Integer> sortByValue(HashMap<Player, Integer> hm)
    {
        List<Map.Entry<Player, Integer> > list =
                new LinkedList<>(hm.entrySet());

        Collections.sort(list, Comparator.comparing(Map.Entry::getValue));

        HashMap<Player, Integer> temp = new HashMap<>();
        for (Map.Entry<Player, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
    public Player getPlayerOnScoreboardPosition(int pos) {
        HashMap<Player, Integer> newkill = sortByValue(kills);
        int i=0;
        Player r = null;
        for(Player p : newkill.keySet()){
           i++;
           if(i==pos){
               r=p;
               break;
            }
        }
        return r;

    }

    //TODO: make a proper scoreboard manager
    public void scoreboardUpdate() {
        for (Player p : getPlayers()) {
            Scoreboard sidebar = Bukkit.getScoreboardManager().getNewScoreboard(); // sidebar is the name of the scoreboard


            Objective objective = sidebar.registerNewObjective("test", "dummy"); // Just supposed to be, doesn't actually add anything
            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', MessageUtils.translateMessage(Config.oitc_scoreboard_title,new HashMap<>()))); // The name that shows up on the top of the sidebar
            objective.setDisplaySlot(DisplaySlot.SIDEBAR); // setting it as sidebar


            int pos = 0;
            List<String> li = Config.oitc_scoreboard;
            for (String s : li) {
                try {

                    String score = s;
                    if (score != null) {
                        //TODO: this code is fucking retarded, find a better way to do this.
                        //I will make a manager for this for Alpha2 I wont make it in time for Alpha1 I think.
                        //There is more problems than just the code for this.
                        Player p0 = getLeadingPlayer();
                        Player p1 = getPlayerOnScoreboardPosition(1);
                        Player p2 = getPlayerOnScoreboardPosition(2);
                        Player p3 = getPlayerOnScoreboardPosition(3);
                        Player p4 = getPlayerOnScoreboardPosition(4);
                        if(score.contains("%player1%")) {
                            if (p0 != null) {
                                score = score.replace("%player1%", p0.getName());
                                score = score.replace("%p1stat%", kills.get(p0).toString());
                            } else {
                                score = "";
                            }
                        }else if(score.contains("%player2%")) {
                            if (p1 != null) {
                                score = score.replace("%player2%", p1.getName());
                                score = score.replace("%p2stat%", kills.get(p1).toString());
                            } else {
                                score = "";
                            }
                        }else if(score.contains("%player3%")) {
                            if (p2 != null) {
                                score = score.replace("%player3%", p2.getName());
                                score = score.replace("%p3stat%", kills.get(p2).toString());
                            } else {
                                score = "";
                            }
                        }else if(score.contains("%player4%")) {
                            if (p3 != null) {
                                score = score.replace("%player4%", p3.getName());
                                score = score.replace("%p4stat%", kills.get(p3).toString());
                            } else {
                                score = "";
                            }
                        }else if(score.contains("%player5%")) {
                            if (p4 != null) {
                                score = score.replace("%player5%", p4.getName());
                                score = score.replace("%p5stat%", kills.get(p4).toString());
                            } else {
                                score = "";
                            }
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
                    ex.printStackTrace();
                    break;
                }
            }

            p.setScoreboard(sidebar);
        }
    }

    public void joinPlayer(Player p) {
        getPlayers().add(p);
        p.teleport(getArena().getRegionByname("wait").getRandomLoc());

        //TODO: save and give back the players inventory after the game!
        p.getInventory().clear();

        for(ItemStack it : getEventStartGear()){
            p.getInventory().addItem(it);
        }
    }
    public void announceMessage(String message) {
        if(Config.oitc_announce_to_all_online) {
            Bukkit.broadcastMessage(message);
        }
        if(Config.oitc_announce_to_all_partaking){
            sendMessageToPartaking(message);
        }
    }

    public void start() {
        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {
            Region r = getArena().getRegionByname("wait");
            if (running) {
                for(Player p : getAllPlayersInRegionXZ(r)){
                    p.teleport(getArena().getRegionByname("player").getRandomLoc());
                }
                setStatus(EventStatus.RUNNING);
                schedulerSeconds++;
                if (schedulerSeconds > gamelength) {
                    //We should end the event and announce the player with the most points
                    if (getLeadingPlayer()!=null) {
                        HashMap<String,String> hm = new HashMap<>();
                        hm.put("%player%",getLeadingPlayer().getName());
                        announceMessage(MessageUtils.translateMessage(Language.Oitc_Time_Out,hm));
                        running=false;
                        setStatus(EventStatus.STOPPED);
                        cleanup();
                    }

                } else {
                    if(Config.oitc_scoreboard_enabled) {
                        Bukkit.getScheduler().runTask(Main.plugin, this::scoreboardUpdate);
                    }
                    if(getLeadingPlayer()!=null) {
                        if(kills.containsKey(getLeadingPlayer())) {
                            if (kills.get(getLeadingPlayer()) >= winscore) {
                                HashMap<String,String> hm = new HashMap<>();
                                hm.put("%player%",getLeadingPlayer().getName());
                                announceMessage(MessageUtils.translateMessage(Language.Oitc_Kill_Win,hm));
                                running=false;
                                setStatus(EventStatus.STOPPED);
                                cleanup();
                            }
                        }
                    }
                }
            }
        }, 0, 20);

    }

    private void cleanup() {
        kills.clear();
        clearPlayers();
        clearScoreboard();
        schedulerSeconds=0;
    }

    private void clearScoreboard() {
        for (Player p : getPlayers()) {
            Scoreboard sidebar = Bukkit.getScoreboardManager().getNewScoreboard(); // sidebar is the name of the scoreboard
            p.setScoreboard(sidebar);
        }
    }
}
