package me.erksterk.openeventsmc.misc;

import me.erksterk.openeventsmc.events.Event;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Region {

    private Location min;
    private Location max;
    private String name;
    public boolean pvp=false;
    public boolean block_break=false;

    //Allow breaking if Key is tool and Value is block
    //Putting Air as material acts as a wildcard
    public HashMap<Material, List<Material>> breakIf = new HashMap<>();


    public Region(Location l1, Location l2, String name, Event e) {
        this.name = name;
        World world = l1.getWorld();
        int xmin;
        int xmax;
        int ymin;
        int ymax;
        int zmin;
        int zmax;
        if (l1.getBlockX() > l2.getBlockX()) {
            xmin = l2.getBlockX();
            xmax = l1.getBlockX();
        } else {
            xmin = l1.getBlockX();
            xmax = l2.getBlockX();
        }
        if (l1.getBlockY() > l2.getBlockY()) {
            ymin = l2.getBlockY();
            ymax = l1.getBlockY();
        } else {
            ymin = l1.getBlockY();
            ymax = l2.getBlockY();
        }
        if (l1.getBlockZ() > l2.getBlockZ()) {
            zmin = l2.getBlockZ();
            zmax = l1.getBlockZ();
        } else {
            zmin = l1.getBlockZ();
            zmax = l2.getBlockZ();
        }
        this.min = new Location(world, xmin, ymin, zmin);
        this.max = new Location(world, xmax, ymax, zmax);
        if(e.forcedMaps.containsKey("region."+name+".pvp")){
            pvp= (boolean) e.forcedMaps.get("region."+name+".pvp");
        }
        if(e.forcedMaps.containsKey("region."+name+".block_break")){
           block_break= (boolean) e.forcedMaps.get("region."+name+".block_break");
        }
        if(e.forcedMaps.containsKey("region."+name+".breakIf")){
            breakIf= (HashMap<Material, List<Material>>) e.forcedMaps.get("region."+name+".breakIf");
        }
    }

    public Location getMin() {
        return this.min;
    }

    public Location getMax() {
        return this.max;
    }

    public String getName() {
        return name;
    }

    public boolean isInBounds(Location l) {
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        if ((x >= min.getBlockX() && x <= max.getBlockX()) && (y >= min.getBlockY() && y <= max.getBlockY()) && (z >= min.getBlockZ() && z <= max.getBlockZ())) {
            return true;
        }
        return false;
    }

    public Location getRandomLoc() {
        int x;
        if (getMin().getBlockX() == getMax().getBlockX()) {
            x = getMax().getBlockX();
        } else {
            x = ThreadLocalRandom.current().nextInt(getMin().getBlockX(), getMax().getBlockX());
        }
        int y;
        if (getMin().getBlockY() == getMax().getBlockY()) {
            y = getMin().getBlockY();
        } else {
            y = ThreadLocalRandom.current().nextInt(getMin().getBlockY(), getMax().getBlockY());
        }

        int z;
        if (getMin().getBlockX() == getMax().getBlockX()) {
            z = getMax().getBlockZ();
        } else {
            z = ThreadLocalRandom.current().nextInt(getMin().getBlockZ(), getMax().getBlockZ());
        }
        Location l = new Location(getMin().getWorld(), x, y, z);
        return l;
    }

    public boolean isInBoundsXZ(Location l) {
        int x = l.getBlockX();

        int z = l.getBlockZ();
        if ((x >= min.getBlockX() && x <= max.getBlockX()) && (z >= min.getBlockZ() && z <= max.getBlockZ())) {
            return true;
        }
        return false;
    }
}
