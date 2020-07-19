package me.erksterk.openeventsmc.listeners;

import me.erksterk.openeventsmc.EventListener;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.Event;
import me.erksterk.openeventsmc.misc.EventManager;
import me.erksterk.openeventsmc.misc.EventType;
import me.erksterk.openeventsmc.events.Waterdrop;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class PlayerListener extends EventListener {
    public PlayerListener(Plugin plugin) {
        super(plugin);
    }


    //TODO: implement the listeners into the event instead of having one shared class
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if(ev!=null) {
            if(ev.getType()== EventType.WATERDROP){
                Waterdrop wd = (Waterdrop) ev;
                wd.eliminated.add(p);
                String message = Language.Waterdrop_eliminated;
                HashMap<String,String> args = new HashMap<>();
                args.put("%player%",p.getName());
                message = MessageUtils.translateMessage(message,args);
                wd.announceMessage(message);

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
