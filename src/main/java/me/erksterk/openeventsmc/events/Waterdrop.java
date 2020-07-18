package me.erksterk.openeventsmc.events;


import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Waterdrop extends Event {
    //TODO: Currently we have multiple variables to check and do the same stuff, this should be reduced to a single on to prevent confusion.
    private BukkitTask task = null;
    private boolean dropped = false;
    private int schedulerSeconds = 0;
    public List<Player> eliminated = new ArrayList<>();
    private int round = 0;

    public Waterdrop(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.inwater");
        requiredFields.add("arena.player");
        requiredFields.add("arena.wait");
        requiredFields.add("arena.dead");
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
                        if (round > 2) {
                            int rand = ThreadLocalRandom.current().nextInt(0, 100);
                            if (rand < percentCover) {
                                tmpblock.getBlock().setType(Material.REDSTONE_BLOCK);
                                waterblocks--;
                            }
                        } else {
                            return;
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

    private void cleanupWater(){
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
    private void checkForWinner(){
        List<Player> alive = new ArrayList<>();
        for(Player p : getAllPlayers()){
            if(!eliminated.contains(p)) alive.add(p);
        }
        if(alive.size()==0){
            Bukkit.broadcastMessage("Nobody survived the event.");
            running=false;
            cleanupWater();
            setStatus(EventStatus.STOPPED);
            task.cancel();
        }else if(alive.size()==1){
            Bukkit.broadcastMessage(alive.get(0).getName()+" has won the event!");
            cleanupWater();
            running=false;
            setStatus(EventStatus.STOPPED);
            task.cancel();
        }
    }


    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            try {
                Thread.sleep(1000);
                if (running) {
                    setStatus(EventStatus.RUNNING);
                    schedulerSeconds++;
                    if (dropped) { //A drop has started, search for players that didnt complete
                        if (schedulerSeconds > Config.waterdrop_elimination_after_drop) {
                            //The players left that didnt finish should be eliminated at this point!
                            List<Player> l = getAllPlayersInRegionXZ(getArena().getRegionByname("wait"));
                            for (Player p : getAllPlayers()) {
                                if (!eliminated.contains(p)) {
                                    if (!l.contains(p)) {
                                        //The player is not dead but didnt complete either, eliminate him
                                        eliminated.add(p);
                                        p.teleport(getArena().getRegionByname("dead").getRandomLoc());
                                        Bukkit.broadcastMessage("Eliminated "+p.getName());
                                    }
                                }
                            }
                            dropped = false; // telling loop to drop players
                            schedulerSeconds = 0; //resetting the timer
                        } else {
                            Bukkit.broadcastMessage("Eliminating in "+(Config.waterdrop_elimination_after_drop-schedulerSeconds)+" Seconds");

                        }
                    } else {
                        if (schedulerSeconds > Config.waterdrop_warn_before_drop_amount) {
                            //We should drop all alive players
                            randomize(); //randomize the dropcover
                            Bukkit.broadcastMessage("Dropping!");
                            Region r = getArena().getRegionByname("wait");
                            List<Player> l = getAllPlayersInRegionXZ(r);
                            System.out.println(l.size());
                            for (Player p : l) {
                                System.out.println(p.getName());
                                p.teleport(getArena().getRegionByname("player").getRandomLoc());
                            }
                            dropped = true; //telling the loop to check for people who didnt finish next round
                            schedulerSeconds = 0; //resetting the timer
                            round++;
                        } else {
                            Bukkit.broadcastMessage("Dropping in "+(Config.waterdrop_warn_before_drop_amount-schedulerSeconds)+" Seconds");
                        }
                    }

                    checkForWinner(); //Check if someone has won and end the event.


                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 20L, 20L);
    }


    @Override
    public void joinPlayer(Player p) {
       if(!getPlayers().contains(p)) {
           getPlayers().add(p);
           p.teleport(getArena().getRegionByname("wait").getRandomLoc());
       }
    }


}
