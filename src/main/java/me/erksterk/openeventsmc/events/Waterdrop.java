package me.erksterk.openeventsmc.events;


import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.Region;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Waterdrop extends Event {
    //TODO: Currently we have multiple variables to check and do the same stuff, this should be reduced to a single on to prevent confusion.
    private BukkitTask taskGame = null;
    private BukkitTask taskMove = null;
    private boolean dropped = false;
    private int schedulerSeconds = 0;
    private int round = 0;

    public Waterdrop(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.inwater");
        requiredFields.add("arena.player");
        requiredFields.add("arena.wait");
        requiredFields.add("arena.dead");

        announce_to_all_online=Config.waterdrop_announce_to_all_online;
        announce_to_all_partaking=Config.waterdrop_announce_to_all_partaking;
    }


    //randomize the water region
    private void randomize() {

        int waterblocks = 0;

        Region re = getArena().getRegionByname("inwater");
        int percentCover = round * round; //TODO: maybe make the difficulty system better and implement it as a config.
        if (percentCover >= 100) {
            percentCover = 99;
        }

        if (re != null) {
            for (int x = re.getMin().getBlockX(); x <= re.getMax().getBlockX(); x = x + 1) {
                for (int y = re.getMin().getBlockY(); y <= re.getMax().getBlockY(); y = y + 1) {
                    for (int z = re.getMin().getBlockZ(); z <= re.getMax().getBlockZ(); z = z + 1) {
                        Location tmpblock = new Location(re.getMin().getWorld(), x, y, z);
                        tmpblock.getBlock().setType(Material.WATER);
                        waterblocks++;
                            int rand = ThreadLocalRandom.current().nextInt(0, 100);
                            if (rand < percentCover) {
                                tmpblock.getBlock().setType(Material.REDSTONE_BLOCK);
                                waterblocks--;
                            }


                    }
                }
            }
            if (waterblocks == 0) {
                //We need to add a random waterblock
                int randx = ThreadLocalRandom.current().nextInt(re.getMin().getBlockX(), re.getMax().getBlockX());
                int randz = ThreadLocalRandom.current().nextInt(re.getMin().getBlockZ(), re.getMax().getBlockZ());
                Location l = new Location(re.getMin().getWorld(), randx, re.getMax().getY(), randz);
                l.getBlock().setType(Material.WATER);
                if (l.getBlock().getType() == Material.REDSTONE_BLOCK) {
                    l.getBlock().setType(Material.WATER);
                    waterblocks++;
                }
            }
        }

    }

    private void cleanupWater() {
        Region re = getArena().getRegionByname("inwater");

        if (re != null) {
            for (int x = re.getMin().getBlockX(); x <= re.getMax().getBlockX(); x = x + 1) {
                for (int y = re.getMin().getBlockY(); y <= re.getMax().getBlockY(); y = y + 1) {
                    for (int z = re.getMin().getBlockZ(); z <= re.getMax().getBlockZ(); z = z + 1) {
                        Location tmpblock = new Location(re.getMin().getWorld(), x, y, z);
                        tmpblock.getBlock().setType(Material.WATER);
                    }
                }
            }
        }
    }

    //Checks for winners and if there is one ends the event
    private void checkForWinner() {
        List<Player> alive = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (!eliminated.contains(p)) alive.add(p);
        }
        if (alive.size() == 0) {
            String message = Language.Waterdrop_nowinner;
            message = MessageUtils.translateMessage(message,new HashMap<>());
            announceMessage(message);
            running = false;

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
                cleanupWater();
            }, 0);
            setStatus(EventStatus.STOPPED);
            eliminated.clear();
            clearPlayers();
            setHoster(null);
            taskGame.cancel();
            taskMove.cancel();

        } else if (alive.size() == 1) {
            String message = Language.Waterdrop_winner;
            HashMap<String,String> args = new HashMap<>();
            args.put("%player%",alive.get(0).getName());
            message = MessageUtils.translateMessage(message,args);
            announceMessage(message);
            running = false;

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
                cleanupWater();
            }, 0);
            setStatus(EventStatus.STOPPED);
            eliminated.clear();
            clearPlayers();
            setHoster(null);
            taskGame.cancel();
            taskMove.cancel();

        }
    }


    public void start() {
        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {
            if (running) {
                setStatus(EventStatus.RUNNING);
                schedulerSeconds++;
                if (dropped) { //A drop has started, search for players that didnt complete
                    if (schedulerSeconds > Config.waterdrop_elimination_after_drop) {
                        //The players left that didnt finish should be eliminated at this point!
                        List<Player> l = getAllPlayersInRegionXZ(getArena().getRegionByname("wait"));
                        for (Player p : getPlayers()) {
                            if (!eliminated.contains(p)) {
                                if (!l.contains(p)) {
                                    //The player is not dead but didnt complete either, eliminate him
                                    eliminated.add(p);
                                    p.teleport(getArena().getRegionByname("dead").getRandomLoc());
                                    String message = Language.Waterdrop_eliminated;
                                    HashMap<String,String> args = new HashMap<>();
                                    args.put("%player%",p.getName());
                                    message = MessageUtils.translateMessage(message,args);
                                    announceMessage(message);

                                }
                            }
                        }
                        dropped = false; // telling loop to drop players
                        schedulerSeconds = 0; //resetting the timer
                    } else {
                        HashMap<String,String> args = new HashMap<>();
                        args.put("%time%",(String.valueOf(Config.waterdrop_elimination_after_drop - schedulerSeconds)));
                        String message = MessageUtils.translateMessage(Language.Waterdrop_eliminate_countdown,args);
                        announceMessage(message);

                    }
                } else {
                    if (schedulerSeconds > Config.waterdrop_warn_before_drop_amount) {
                        //We should drop all alive players
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
                            randomize(); //randomize the dropcover
                        }, 0);

                        String message = MessageUtils.translateMessage(Language.Waterdrop_dropping,new HashMap<>());
                        announceMessage(message);
                        Region r = getArena().getRegionByname("wait");
                        List<Player> l = getAllPlayersInRegionXZ(r);
                        for (Player p : l) {
                            p.teleport(getArena().getRegionByname("player").getRandomLoc());
                        }
                        dropped = true; //telling the loop to check for people who didnt finish next round
                        schedulerSeconds = 0; //resetting the timer
                        round++;
                    } else {
                        HashMap<String,String> args = new HashMap<>();
                        args.put("%time%",(String.valueOf((Config.waterdrop_warn_before_drop_amount - schedulerSeconds))));
                        String message = MessageUtils.translateMessage(Language.Waterdrop_drop_countdown,args);
                        announceMessage(message);
                    }
                }

                checkForWinner(); //Check if someone has won and end the event.


            }

        }, 0, 20);
        taskMove = Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            if (running) {
                //Player is partaking in an event
                for (Player p : getAllAlivePlayers()) {
                    if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                        if (getArena().getRegionByname("inwater").isInBoundsXZ(p.getLocation())) {
                            p.teleport(getArena().getRegionByname("wait").getRandomLoc());
                            p.sendMessage(MessageUtils.translateMessage(Language.Waterdrop_dropped_success,new HashMap<>()));
                        }
                    }
                }
            }

        }, 0, 1);
    }

    private List<Player> getAllAlivePlayers() {
        List<Player> alive = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (!eliminated.contains(p)) {
                alive.add(p);
            }
        }
        return alive;
    }


    @Override
    public void joinPlayer(Player p) {
        if (!getPlayers().contains(p)) {
            getPlayers().add(p);
            p.teleport(getArena().getRegionByname("wait").getRandomLoc());
        }
    }


}
