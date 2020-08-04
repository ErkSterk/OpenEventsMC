package me.erksterk.openeventsmc.events.OITC;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.Event;
import me.erksterk.openeventsmc.libraries.scoreboard.SBoard;
import me.erksterk.openeventsmc.libraries.scoreboard.SBoardHandler;
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

        forcedMaps.put("region.player.pvp", true);

        announce_to_all_online = Config.oitc_announce_to_all_online;
        announce_to_all_partaking = Config.oitc_announce_to_all_partaking;

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

    public static HashMap<Player, Integer> sortByValue(HashMap<Player, Integer> hm) {
        List<Map.Entry<Player, Integer>> list =
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
        int i = 0;
        Player r = null;
        for (Player p : newkill.keySet()) {
            i++;
            if (i == pos) {
                r = p;
                break;
            }
        }
        return r;

    }

    public void scoreboardUpdate() {
        for (Player p : getPlayers()) {

            List<String> li = Config.oitc_scoreboard;

            HashMap<String, String> pholder = new HashMap<>();

            for(int i = 1; i<getPlayers().size()+1;i++){
                Player ps = getPlayerOnScoreboardPosition(i);
                if(ps!=null) {
                    pholder.put("%player" + (i) + "%", ps.getName());
                    String ki = "0";
                    if (kills.containsKey(ps)) {
                        ki = String.valueOf(kills.get(ps));
                    }
                    pholder.put("%p" + (i) + "stat%", ki);
                }else{
                    pholder.put("%player" + (i) + "%", null);
                    pholder.put("%p" + (i) + "stat%", null);
                }
            }

            if (kills.containsKey(p)) {
                pholder.put("%pstats%", kills.get(p).toString());
            } else {
                pholder.put("%pstats%", "0");
            }
            SBoard sb = new OITCSboard(li,pholder);
            SBoardHandler.sendSBoard(sb,p);
        }
    }

    public void joinPlayer(Player p) {
        getPlayers().add(p);
        p.teleport(getArena().getRegionByname("wait").getRandomLoc());

        //TODO: save and give back the players inventory after the game!
        p.getInventory().clear();

        for (ItemStack it : getEventStartGear()) {
            p.getInventory().addItem(it);
        }
    }

    public void start() {
        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {
            Region r = getArena().getRegionByname("wait");
            if (running) {
                for (Player p : getAllPlayersInRegionXZ(r)) {
                    p.teleport(getArena().getRegionByname("player").getRandomLoc());
                }
                setStatus(EventStatus.RUNNING);
                schedulerSeconds++;
                if (schedulerSeconds > gamelength) {
                    //We should end the event and announce the player with the most points
                    if (getLeadingPlayer() != null) {
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("%player%", getLeadingPlayer().getName());
                        announceMessage(MessageUtils.translateMessage(Language.Oitc_Time_Out, hm));
                        running = false;
                        setStatus(EventStatus.STOPPED);
                        cleanup();
                    }

                } else {
                    if (Config.oitc_scoreboard_enabled) {
                        Bukkit.getScheduler().runTask(Main.plugin, this::scoreboardUpdate);
                    }
                    if (getLeadingPlayer() != null) {
                        if (kills.containsKey(getLeadingPlayer())) {
                            if (kills.get(getLeadingPlayer()) >= winscore) {
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("%player%", getLeadingPlayer().getName());
                                announceMessage(MessageUtils.translateMessage(Language.Oitc_Kill_Win, hm));
                                running = false;
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
        schedulerSeconds = 0;
        setHoster(null);
        taskGame.cancel();
    }

    private void clearScoreboard() {
        for (Player p : getPlayers()) {
            Scoreboard sidebar = Bukkit.getScoreboardManager().getNewScoreboard(); // sidebar is the name of the scoreboard
            p.setScoreboard(sidebar);
        }
    }
}
