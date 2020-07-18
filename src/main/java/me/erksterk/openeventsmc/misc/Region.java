package me.erksterk.openeventsmc.misc;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.ThreadLocalRandom;

public class Region {

    private Location min = null;
    private Location max = null;
    private String name;

    public Region(Location l1, Location l2, String name) {
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
        min = new Location(world, xmin, ymin, zmin);
        max = new Location(world, xmax, ymax, zmax);
    }

    public Location getMin() {
        return min;
    }

    public Location getMax() {
        return max;
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
