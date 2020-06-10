package me.erksterk.openeventsmc.utils;

import me.erksterk.openeventsmc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ConfigUtils {

    public static Location parseLocationFromString(String loc){
        Location location = null;
        String[] split = loc.split("_");
        if(split!=null) {
            if(split.length>=4) {
                location = new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            }else{
                Main.writeToConsole("&cError, ConfigUtil couldnt parse location from string. missing variables");
            }
        }
        return location;
    }
    public static String parseStringFromLocation(Location loc){
        String r = "world_0_0_0";
        if(loc!=null) {
            String world = loc.getWorld().getName();
            String x = String.valueOf(loc.getBlockX());
            String y = String.valueOf(loc.getBlockY());
            String z = String.valueOf(loc.getBlockZ());
            r = world + "_" + x + "_" + y + "_" + z;
        }
        return r;
    }
}
