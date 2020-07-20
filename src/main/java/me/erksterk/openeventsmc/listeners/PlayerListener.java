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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
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
    @EventHandler(priority = EventPriority.HIGH)
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
                    OneInTheChamber c = (OneInTheChamber) ev;
                    Player killer = e.getEntity().getKiller();
                    if(killer!=null) {
                        int kills = 0;
                        if (c.kills.containsKey(killer)) {
                            kills = c.kills.get(killer);
                        }
                        kills++;
                        c.kills.put(killer, kills);
                        if (!killer.getInventory().contains(Material.ARROW)) {
                            killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                            killer.sendMessage("+1 Arrow!");
                        }
                        Bukkit.broadcastMessage(p.getName() + "was killed by " + killer.getName());
                    }
                    e.getDrops().clear();
                    break;
                }

            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onArrowPickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if (ev != null) {
            switch (ev.getType()){
                case ONEINTHECHAMBER:{
                    if(e.getItem().getItemStack().getType()==Material.ARROW){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
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
                    Location l = ev.getArena().getRegionByname("player").getRandomLoc();
                    e.setRespawnLocation(l);
                    p.teleport(l);
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
            if (damager.getType() == EntityType.ARROW) {
                Arrow a = (Arrow) damager;
                ProjectileSource source = a.getShooter();
                if (damaged.getType() == EntityType.PLAYER) {
                    Player d = (Player) source;
                    Player k = (Player) damaged;
                    Event ev = EventManager.getEventPlayerPartaking(k);
                    if (ev != null) {
                        switch (ev.getType()) {
                            case ONEINTHECHAMBER: {
                                OneInTheChamber c = (OneInTheChamber) ev;
                                k.setHealth(0);
                                int kills = 0;
                                if (c.kills.containsKey(d)) {
                                    kills = c.kills.get(d);
                                }
                                kills++;
                                c.kills.put(d, kills);
                                if(!d.getInventory().contains(Material.ARROW)){
                                    d.getInventory().addItem(new ItemStack(Material.ARROW,1));
                                    d.sendMessage("+1 Arrow!");
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

}
