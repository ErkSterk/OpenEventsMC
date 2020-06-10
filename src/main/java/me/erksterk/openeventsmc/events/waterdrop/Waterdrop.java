package me.erksterk.openeventsmc.events.waterdrop;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.waterdrop.listeners.PlayerListener;

import me.erksterk.openeventsmc.utils.ConfigUtils;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;

public class Waterdrop {

    //TODO: make it so we can have multiple waterdrops configured
    public static Location waterdrop_death;
    public static Location waterdrop_drop;
    public static Location waterdrop_wait;

    public static int lapispercentage=20;
    public static int stonepercentage=70;

    public static List<Player> waterdrop_not_partaking = new ArrayList<>();

    public static List<Player> waterdrop_dead_players = new ArrayList<>();

    public static Player winner = null;

    public static boolean running=false;

    int secondselapsed=0;
    boolean dropstarted=false;

//Sets up the required locations and listeners
    public void setup(Main plugin){
        if(Config.waterdrop_enabled){
            waterdrop_death= ConfigUtils.parseLocationFromString(Config.waterdrop_death_location);
            waterdrop_drop= ConfigUtils.parseLocationFromString(Config.waterdrop_drop_location);
            waterdrop_wait= ConfigUtils.parseLocationFromString(Config.waterdrop_wait_location);
        }else{
            Main.writeToConsole("&cWaterdrop disabled!");
        }
        //Register the events
        new PlayerListener(plugin);
        //thread.start();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    if(running){
                        secondselapsed++;
                        if(dropstarted) {
                            if (secondselapsed >= Config.waterdrop_elimination_after_drop) {
                                //its been 10 seconds, lets kill everyone thats not in the waiting
                                Waterdrop.eliminatePlayersNotInWait();
                                if(Waterdrop.checkIfWeHaveWinner()){
                                    running=false;
                                    HashMap<String,String> val = new HashMap<>();
                                    val.put("%player%",winner.getName());
                                    Bukkit.broadcastMessage(MessageUtils.translateMessage(Language.EVENTS_WINNER,val));
                                }
                                secondselapsed=0;
                                dropstarted=false;
                            }else{
                                HashMap<String,String> val = new HashMap<>();
                                val.put("%warn%",(Config.waterdrop_elimination_after_drop-secondselapsed)+"");
                                Bukkit.broadcastMessage(MessageUtils.translateMessage(Language.EVENTS_WATERDROP_ANNOUNCEKILL,val));
                            }
                        }else{
                            if(secondselapsed>=Config.waterdrop_warn_before_drop_amount){
                                secondselapsed=0;
                                Waterdrop.randomizeWater(stonepercentage,lapispercentage);
                                dropstarted=true;

                                //Teleport all alive players to drop
                                Waterdrop.dropAllPlayers();

                            }else{
                                HashMap<String,String> val = new HashMap<>();
                                val.put("%warn%",(Config.waterdrop_warn_before_drop_amount-secondselapsed)+"");
                                Bukkit.broadcastMessage(MessageUtils.translateMessage(Language.EVENTS_WATERDROP_ANNOUNCEDROP,val));
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 20L, 20L);
    }

    private static boolean checkIfWeHaveWinner() {
        List<Player> remaining = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()){
            if(isPlayerInWaterdrop(p) && isPlayerParticipating(p)){
                if(!waterdrop_dead_players.contains(p)){
                    remaining.add(p);
                }
            }
        }
        if(remaining.size()==1){
            winner=remaining.get(0);
            return true;
        }else{
            return false;
        }
    }

    //return true if the player is inside the waterdrop worldguard region
    public static boolean isPlayerInWaterdrop(Player p){
        if(Config.waterdrop_enabled) {
            Map<String, ProtectedRegion> regions = WorldGuardPlugin.inst().getRegionManager(p.getLocation().getWorld()).getRegions();
            for (String regionname : regions.keySet()) {
                if (regionname.equalsIgnoreCase(Config.waterdrop_region)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void reset() {
        winner=null;
        waterdrop_dead_players.clear();
        waterdrop_not_partaking.clear();
        for(Player p : Bukkit.getOnlinePlayers()){
            if(isPlayerInWaterdrop(p)){
                p.teleport(waterdrop_wait);
            }
        }
    }

    //Checks if the player has been removed from event etc if its an admin, returns true if the player is supposed to be playing
    public static boolean isPlayerParticipating(Player p) {
        if(waterdrop_not_partaking.contains(p)){
            return false;
        }
        return true;
    }

    public static void randomizeWater(int difficulty, int lapisspawnrate){
        Map<String, ProtectedRegion> regions = WorldGuardPlugin.inst().getRegionManager(waterdrop_drop.getWorld()).getRegions();
        CuboidRegion region = null;
        World worldeditworld = null;
        for(World w : WorldEdit.getInstance().getServer().getWorlds()){
            if(w.getName().equalsIgnoreCase(waterdrop_drop.getWorld().getName())){
                worldeditworld=w;
            }
        }
        if(worldeditworld==null){
            Main.writeToConsole("&cInternal error!");
        }else {
            int waterblocks=0;
            int lapis = 0;
            int stone = 0;
            for (String regionname : regions.keySet()) {
                if (regionname.equalsIgnoreCase(Config.waterdrop_water_region)) {
                    ProtectedRegion re = regions.get(regionname);
                    region = new CuboidRegion(worldeditworld, new BlockVector(re.getMinimumPoint().getX(), re.getMinimumPoint().getY(), re.getMinimumPoint().getZ()), new BlockVector(re.getMaximumPoint().getX(), re.getMaximumPoint().getY(), re.getMaximumPoint().getZ()));
                    for(int x = region.getMinimumPoint().getBlockX();x <= region.getMaximumPoint().getBlockX(); x=x+1){
                        for(int y = region.getMinimumPoint().getBlockY();y <= region.getMaximumPoint().getBlockY(); y=y+1){
                            for(int z = region.getMinimumPoint().getBlockZ();z <= region.getMaximumPoint().getBlockZ(); z=z+1){
                                Location tmpblock = new Location(waterdrop_drop.getWorld(), x, y, z);
                                if(tmpblock.getBlock().getType()==Material.LAPIS_BLOCK || tmpblock.getBlock().getType()==Material.STONE){
                                    tmpblock.getBlock().setType(Material.WATER);
                                    waterblocks++;
                                }else if(tmpblock.getBlock().getType()==Material.WATER || tmpblock.getBlock().getType()==Material.STATIONARY_WATER){
                                    waterblocks++;
                                }
                                int rand = ThreadLocalRandom.current().nextInt(0,100);
                                if(rand<=difficulty){
                                    if(tmpblock.getBlock().getType()==Material.WATER || tmpblock.getBlock().getType()==Material.STATIONARY_WATER) {
                                        waterblocks--;
                                        if (difficulty > 50) {
                                            int rand2 = ThreadLocalRandom.current().nextInt(0, 100);
                                            if (rand2 > (100-lapisspawnrate)) {
                                                tmpblock.getBlock().setType(Material.LAPIS_BLOCK);
                                                lapis++;
                                            } else {
                                                tmpblock.getBlock().setType(Material.STONE);
                                                stone++;
                                            }
                                        } else {
                                            tmpblock.getBlock().setType(Material.STONE);
                                            stone++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(waterblocks==0){
                        //We need to add a random waterblock
                        int randx = ThreadLocalRandom.current().nextInt(re.getMinimumPoint().getBlockX(),re.getMaximumPoint().getBlockX());
                        int randz = ThreadLocalRandom.current().nextInt(re.getMinimumPoint().getBlockZ(),re.getMaximumPoint().getBlockZ());
                        Location l = new Location(waterdrop_drop.getWorld(),randx,re.getMaximumPoint().getY(),randz);
                        l.getBlock().setType(Material.WATER);
                        if(l.getBlock().getType()==Material.LAPIS_BLOCK || l.getBlock().getType()==Material.STONE){
                            l.getBlock().setType(Material.WATER);
                            waterblocks++;
                        }
                    }
                }
            }
        }
    }

    public static void eliminatePlayersNotInWait() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(isPlayerInWaterdrop(p) && isPlayerParticipating(p)){
                if(!waterdrop_dead_players.contains(p)){
                    //check the regions the player is inside
                    boolean kill = true;
                    Map<String, ProtectedRegion> regions = WorldGuardPlugin.inst().getRegionManager(p.getLocation().getWorld()).getRegions();
                    for (String regionname : regions.keySet()) {
                        if (regionname.equalsIgnoreCase(Config.waterdrop_waiting_region)) {
                            ProtectedRegion r = regions.get(regionname);
                            if(r.contains(p.getLocation().getBlockX(),p.getLocation().getBlockY(),p.getLocation().getBlockZ())) {
                                kill = false;
                            }
                        }
                    }
                    if(kill){
                        waterdrop_dead_players.add(p);
                        p.teleport(waterdrop_death);
                    }
                }

            }
        }
    }

    public static void dropAllPlayers() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(isPlayerInWaterdrop(p) && isPlayerParticipating(p)){
                if(!waterdrop_dead_players.contains(p)) {
                    p.teleport(waterdrop_drop);
                }
            }
        }
    }

    public static void pause() {
        if(running==true) {
           running = false;
        }else{
            running=true;
        }
    }
}
