package me.erksterk.openeventsmc.listeners;

import me.erksterk.openeventsmc.EventListener;
import me.erksterk.openeventsmc.events.Event;
import me.erksterk.openeventsmc.misc.EventManager;
import me.erksterk.openeventsmc.misc.EventType;
import me.erksterk.openeventsmc.events.Waterdrop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public class PlayerListener extends EventListener {
    public PlayerListener(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void move(PlayerMoveEvent e){
        Event ev = EventManager.getEventPlayerPartaking(e.getPlayer());
        if(ev!=null){
            //Player is partaking in an event
            Player p = e.getPlayer();
            if(ev.getType()== EventType.WATERDROP){
                Waterdrop wd = (Waterdrop) ev;
                if(!wd.eliminated.contains(p)){
                   if(p.getLocation().getBlock().getType()==Material.WATER || p.getLocation().getBlock().getType()==Material.STATIONARY_WATER){
                       if(wd.getArena().getRegionByname("inwater").isInBoundsXZ(p.getLocation())){
                           p.teleport(wd.getArena().getRegionByname("wait").getRandomLoc());
                           p.sendMessage("Successfully dropped!");
                       }
                   }
                }
            }
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if(ev!=null) {
            if(ev.getType()== EventType.WATERDROP){
                Waterdrop wd = (Waterdrop) ev;
                wd.eliminated.add(p);
                Bukkit.broadcastMessage(p.getName()+" Was eliminated");
            }
        }
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if(ev!=null) {
            if(ev.getType()== EventType.WATERDROP){
                Waterdrop wd = (Waterdrop) ev;
                if(wd.eliminated.contains(p)){
                    e.setRespawnLocation(wd.getArena().getRegionByname("dead").getRandomLoc());
                }
            }
        }
    }
}
