package me.erksterk.openeventsmc.listeners;

import me.erksterk.openeventsmc.EventListener;
import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.Event;
import me.erksterk.openeventsmc.events.OneInTheChamber;
import me.erksterk.openeventsmc.misc.EventManager;
import me.erksterk.openeventsmc.misc.EventType;
import me.erksterk.openeventsmc.events.Waterdrop;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;

public class PlayerListener extends EventListener {
    public PlayerListener(Plugin plugin) {
        super(plugin);
    }


    //TODO: implement the listeners into the event instead of having one shared class
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if (ev != null) {
            switch (ev.getType()) {
                case WATERDROP: {
                    Waterdrop wd = (Waterdrop) ev;
                    wd.eliminated.add(p);
                    String message = Language.Waterdrop_eliminated;
                    HashMap<String, String> args = new HashMap<>();
                    args.put("%player%", p.getName());
                    message = MessageUtils.translateMessage(message, args);
                    wd.announceMessage(message);
                    break;
                }
                case ONEINTHECHAMBER: {
                    //handle death
                    break;
                }

            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if (ev != null) {
            switch (ev.getType()) {
                case WATERDROP: {
                    Waterdrop wd = (Waterdrop) ev;
                    if (wd.eliminated.contains(p)) {
                        e.setRespawnLocation(wd.getArena().getRegionByname("dead").getRandomLoc());
                        p.teleport(wd.getArena().getRegionByname("dead").getRandomLoc());
                    }
                    break;
                }
                case ONEINTHECHAMBER: {
                    p.getInventory().addItem(new ItemStack(Material.BOW, 1));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
                        p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                    }, 20 * 5);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Entity damager = e.getDamager();
            Entity damaged = e.getEntity();
            if(damager.getType()==EntityType.ARROW){
                Arrow a = (Arrow) damager;
                ProjectileSource source = a.getShooter();
                if (damaged.getType()==EntityType.PLAYER) {
                    Player d = (Player) source;
                    Player k = (Player) damaged;
                    Event ev = EventManager.getEventPlayerPartaking(k);
                    if (ev != null) {
                        switch (ev.getType()) {
                            case ONEINTHECHAMBER: {
                                OneInTheChamber c = (OneInTheChamber) ev;
                                k.setHealth(0);
                                Bukkit.broadcastMessage(k.getName()+"was killed by "+d.getName());
                                int kills = 0;
                                if(c.kills.containsKey(d)){
                                    kills = c.kills.get(d);
                                }
                                kills++;
                                c.kills.put(d,kills);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

}
