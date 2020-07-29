package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.Region;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RedRover extends Event {

    public int traveltime = 10;
    public int peacetime = 20;
    private BukkitTask taskGame = null;
    public int schedulerSeconds = 0;

    public RedRover(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.blue");
        requiredFields.add("arena.red");
        requiredFields.add("arena.pvp");
        requiredFields.add("arena.dead");
        requiredFields.add("config.traveltime");
        requiredFields.add("config.peacetime");
    }

    public void checkForWinner() {
        List<Player> partaking = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (!eliminated.contains(p)) {
                partaking.add(p);
            }
        }
        if (partaking.size() == 0) {
            running = false;
            setStatus(EventStatus.STOPPED);
            schedulerSeconds = 0;
            announceMessage(MessageUtils.translateMessage(Language.Redrover_nowinner, new HashMap<>()));
            eliminated.clear();
            clearPlayers();
        } else if (partaking.size() == 1) {
            running = false;
            setStatus(EventStatus.STOPPED);
            schedulerSeconds = 0;
            HashMap<String, String> hm = new HashMap<>();
            hm.put("%player%", partaking.get(0).getName());
            announceMessage(MessageUtils.translateMessage(Language.Redrover_winner, hm));
            eliminated.clear();
            clearPlayers();
        }
    }

    public void joinPlayer(Player p) {
        getPlayers().add(p);
        p.teleport(getArena().getRegionByname("red").getRandomLoc());

        for (ItemStack it : getEventStartGear()) {
            p.getInventory().addItem(it);
        }
    }

    public void announceMessage(String message) {
        if (Config.redrover_announce_to_all_online) {
            Bukkit.broadcastMessage(message);
        }
        if (Config.redrover_announce_to_all_partaking) {
            sendMessageToPartaking(message);
        }
    }

    boolean peace = false; //is it currently peace?
    public boolean red = true; //current area
    boolean announced = false; //has the message been announced?

    public void start() {
        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {
            if (running) {
                setStatus(EventStatus.RUNNING);
                schedulerSeconds++;
                checkForWinner(); //Check if someone has won and end the event.
                if (peace) {
                    announced = false;
                    if (schedulerSeconds >= peacetime) {
                        peace = false;
                        schedulerSeconds = 0;
                        announceMessage(MessageUtils.translateMessage(Language.Redrover_peace_over,new HashMap<>()));
                    }
                } else {
                    if (red) {
                        //goto blue
                        if (!announced) {
                            announceMessage(MessageUtils.translateMessage(Language.Redrover_run_blue, new HashMap<>()));
                            announced = true;
                        }
                        if (schedulerSeconds >= traveltime) {
                            red = false;
                            List<Player> blue = new ArrayList<>();
                            Region r = getArena().getRegionByname("blue");
                            for (Player p : getPlayers()) {
                                if (getAllPlayersInRegionXZ(r).contains(p)) {
                                    blue.add(p);
                                }
                            }
                            for (Player p : getPlayers()) {
                                if(!blue.contains(p)){
                                    p.teleport(getArena().getRegionByname("dead").getRandomLoc());
                                    eliminated.add(p);
                                    HashMap<String, String> hm = new HashMap<>();
                                    hm.put("%killed%", p.getName());
                                    announceMessage(MessageUtils.translateMessage(Language.Redrover_eliminated, hm));
                                }
                            }


                            peace = true;
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("%time%", String.valueOf(peacetime));
                            announceMessage(MessageUtils.translateMessage(Language.Redrover_peace, hm));
                            schedulerSeconds = 0;
                        }

                    } else {
                        //gotored
                        if (!announced) {
                            announceMessage(MessageUtils.translateMessage(Language.Redrover_run_red, new HashMap<>()));
                            announced = true;
                        }
                        if (schedulerSeconds >= traveltime) {
                            red = true;
                            List<Player> red = new ArrayList<>();
                            Region r = getArena().getRegionByname("red");
                            for (Player p : getPlayers()) {
                                if (getAllPlayersInRegionXZ(r).contains(p)) {
                                    red.add(p);
                                }
                            }
                            for (Player p : getPlayers()) {
                                if(!red.contains(p)){
                                    p.teleport(getArena().getRegionByname("dead").getRandomLoc());
                                    eliminated.add(p);
                                    HashMap<String,String> hm = new HashMap<>();
                                    hm.put("%killed%",p.getName());
                                    announceMessage(MessageUtils.translateMessage(Language.Redrover_eliminated,hm));
                                }
                            }
                            peace = true;
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("%time%", String.valueOf(peacetime));
                            announceMessage(MessageUtils.translateMessage(Language.Redrover_peace, hm));
                            schedulerSeconds = 0;
                        }
                    }
                }


            }

        }, 0, 20);
    }
}
