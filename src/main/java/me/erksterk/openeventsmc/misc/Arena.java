package me.erksterk.openeventsmc.misc;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    private Region arenaRegion=null;
    private List<Region> arenaregions = new ArrayList<>();

    public Arena(Region arena){
        this.arenaRegion=arena;
    }

    public Region[] getAllRegions(){
        return arenaregions.toArray(new Region[0]);
    }

    public Region getArenaBoundRegion(){
        return arenaRegion;
    }

    public Region getRegionByname(String name){
        for(Region r : arenaregions){
            if(r.getName().equalsIgnoreCase(name)){
                return r;
            }
        }
        return null;
    }
    public Region[] getAllRegionsAtLocation(Location l){
        List<Region> regs = new ArrayList<>();
        for(Region r : arenaregions){
            if(r.isInBounds(l)) regs.add(r);
        }
        regs.add(arenaRegion);
        return regs.toArray(new Region[0]);
    }

    public void addRegion(Region r) {
        arenaregions.add(r);
    }
}
