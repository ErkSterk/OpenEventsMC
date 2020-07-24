package me.erksterk.openeventsmc.listeners;

import me.erksterk.openeventsmc.EventListener;
import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.Event;
import me.erksterk.openeventsmc.events.OneInTheChamber;
import me.erksterk.openeventsmc.events.RedRover;
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
                    if(ev.running) {
                        if (killer != null) {
                            if(killer.getItemInHand().getType()!=Material.BOW) {
                                int kills = 0;
                                if (c.kills.containsKey(killer)) {
                                    kills = c.kills.get(killer);
                                }
                                kills++;
                                c.kills.put(killer, kills);
                                if (!killer.getInventory().contains(Material.ARROW)) {
                                    killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                                    killer.sendMessage(MessageUtils.translateMessage(Language.Oitc_Add_Arrow,new HashMap<>()));
                                }
                                HashMap<String,String> hm = new HashMap<>();
                                hm.put("%killed%",p.getName());
                                hm.put("%killer%",killer.getName());
                                ev.sendMessageToPartaking(MessageUtils.translateMessage(Language.Oitc_Kill_Player,hm));
                            }
                        }
                        e.getDrops().clear();
                    }
                    break;
                }
                case REDROVER:{
                    RedRover c = (RedRover) ev;
                    if(ev.running){
                        Player killer = e.getEntity().getKiller();
                        Player killed = e.getEntity();
                        c.eliminated.add(killed);
                        HashMap<String,String> hm = new HashMap<>();
                        if(killer!=null){
                            hm.put("%killer%",killer.getName());
                            hm.put("%killed%",killed.getName());
                            ((RedRover) ev).announceMessage(MessageUtils.translateMessage(Language.Redrover_killed,hm));
                        }else{
                            hm.put("%killed%",killed.getName());
                            ((RedRover) ev).announceMessage(MessageUtils.translateMessage(Language.Redrover_eliminated,hm));
                        }
                    }
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
                    for(ItemStack it : ev.respawn_gear){
                        p.getInventory().addItem(it);
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
                        p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                        p.sendMessage(MessageUtils.translateMessage(Language.Oitc_Add_Arrow,new HashMap<>()));
                    }, 20 * Event.getFieldInt(ev,"config.respawn_item_give_delay"));
                    Location l = ev.getArena().getRegionByname("player").getRandomLoc();
                    e.setRespawnLocation(l);
                    p.teleport(l);
                    break;
                }
                case REDROVER:{
                    RedRover rr = (RedRover) ev;
                    if(rr.eliminated.contains(p)) {
                        e.setRespawnLocation(ev.getArena().getRegionByname("dead").getRandomLoc());
                    }
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
                                    d.sendMessage(MessageUtils.translateMessage(Language.Oitc_Add_Arrow,new HashMap<>()));
                                }
                                HashMap<String,String> hm = new HashMap<>();
                                hm.put("%killed%",k.getName());
                                hm.put("%killer%",d.getName());
                                ev.sendMessageToPartaking(MessageUtils.translateMessage(Language.Oitc_Kill_Player,hm));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

}
