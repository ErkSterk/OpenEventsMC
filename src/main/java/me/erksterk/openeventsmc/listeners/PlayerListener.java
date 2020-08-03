package me.erksterk.openeventsmc.listeners;

import me.erksterk.openeventsmc.EventListener;
import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.*;
import me.erksterk.openeventsmc.misc.EventManager;
import me.erksterk.openeventsmc.misc.EventType;
import me.erksterk.openeventsmc.misc.Region;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
import java.util.List;

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
                    if (ev.running) {
                        if (killer != null) {
                            if (killer.getItemInHand().getType() != Material.BOW) {
                                int kills = 0;
                                if (c.kills.containsKey(killer)) {
                                    kills = c.kills.get(killer);
                                }
                                kills++;
                                c.kills.put(killer, kills);
                                if (!killer.getInventory().contains(Material.ARROW)) {
                                    killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                                    killer.sendMessage(MessageUtils.translateMessage(Language.Oitc_Add_Arrow, new HashMap<>()));
                                }
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("%killed%", p.getName());
                                hm.put("%killer%", killer.getName());
                                ev.sendMessageToPartaking(MessageUtils.translateMessage(Language.Oitc_Kill_Player, hm));
                            }
                        }
                        e.getDrops().clear();
                    }
                    break;
                }
                case REDROVER: {
                    RedRover c = (RedRover) ev;
                    if (ev.running) {
                        Player killer = e.getEntity().getKiller();
                        Player killed = e.getEntity();
                        c.eliminated.add(killed);
                        HashMap<String, String> hm = new HashMap<>();
                        if (killer != null) {
                            hm.put("%killer%", killer.getName());
                            hm.put("%killed%", killed.getName());
                            c.announceMessage(MessageUtils.translateMessage(Language.Redrover_killed, hm));
                        } else {
                            hm.put("%killed%", killed.getName());
                            c.announceMessage(MessageUtils.translateMessage(Language.Redrover_eliminated, hm));
                        }

                    }
                    break;
                }
                case LASTMANSTANDING: {
                    LastManStanding lms = (LastManStanding) ev;
                    if (lms.running) {
                        Player killer = e.getEntity().getKiller();
                        Player killed = e.getEntity();
                        lms.eliminated.add(killed);
                        HashMap<String, String> hm = new HashMap<>();
                        if (killer != null) {
                            hm.put("%killer%", killer.getName());
                            hm.put("%killed%", killed.getName());
                            lms.announceMessage(MessageUtils.translateMessage(Language.LastManStanding_killed, hm));
                        } else {
                            hm.put("%killed%", killed.getName());
                            lms.announceMessage(MessageUtils.translateMessage(Language.LastManStanding_eliminated, hm));
                        }
                        e.getDrops().clear();
                    }
                }
                case SPLEEF: {
                    Spleef sple = (Spleef) ev;
                    if (sple.running) {
                        Player killed = e.getEntity();
                        sple.eliminated.add(killed);
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("%killed%", killed.getName());
                        sple.announceMessage(MessageUtils.translateMessage(Language.Spleef_eliminated, hm));
                        e.getDrops().clear();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if (ev != null) {
            switch (ev.getType()) {
                //Overrides
                //Default
                default: {
                    Block b = e.getBlock();
                    boolean allow = false;
                    for (Region r : ev.getArena().getAllRegionsAtLocation(b.getLocation())) {
                        if (r.block_break) {
                            allow = true;
                            break;
                        }
                    }
                    if (!allow) {
                        e.setCancelled(true);
                    }
                    break;
                }
            }

        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if (ev != null) {
            switch (ev.getType()) {
                //Overrides
                case SPLEEF: {
                    if (e.getBlock().getType() == Material.SNOW_BLOCK) {
                        e.getBlock().setType(Material.AIR);
                        p.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 1));
                        e.setCancelled(true);
                    } else {
                        e.setCancelled(true);
                    }
                    break;
                }
                //Default
                default: {
                    Block b = e.getBlock();
                    boolean allow = false;
                    for (Region r : ev.getArena().getAllRegionsAtLocation(b.getLocation())) {
                        if (r.block_break) {
                            allow = true;
                            break;
                        } else {
                            if (r.breakIf.containsKey(b.getType())) {
                                List<Material> a = r.breakIf.get(b.getType());
                                if (a.contains(Material.AIR)) {
                                    allow = true;
                                    break;
                                } else if (a.contains(b.getType())) {
                                    allow = true;
                                    break;
                                }
                            } else if (r.breakIf.containsKey(Material.AIR)) {
                                //Wildcard Detected
                                List<Material> a = r.breakIf.get(Material.AIR);
                                if (a.contains(Material.AIR)) {
                                    allow = true;
                                    break;
                                } else if (a.contains(b.getType())) {
                                    allow = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!allow) {
                        e.setCancelled(true);
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
            switch (ev.getType()) {
                case ONEINTHECHAMBER: {
                    if (e.getItem().getItemStack().getType() == Material.ARROW) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Event ev = EventManager.getEventPlayerPartaking(p);
        if (ev != null) {
            switch (ev.getType()) {
                case WATERDROP: {
                    Waterdrop wd = (Waterdrop) ev;
                    Location l = ev.getArena().getRegionByname("dead").getRandomLoc();
                    e.setRespawnLocation(l);
                    p.teleport(l);
                    break;
                }
                case ONEINTHECHAMBER: {
                    for (ItemStack it : ev.respawn_gear) {
                        p.getInventory().addItem(it);
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
                        p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                        p.sendMessage(MessageUtils.translateMessage(Language.Oitc_Add_Arrow, new HashMap<>()));
                    }, 20 * Event.getFieldInt(ev, "config.respawn_item_give_delay"));
                    Location l = ev.getArena().getRegionByname("player").getRandomLoc();
                    e.setRespawnLocation(l);
                    p.teleport(l);
                    break;
                }
                case REDROVER: {
                    RedRover rr = (RedRover) ev;
                    e.setRespawnLocation(ev.getArena().getRegionByname("dead").getRandomLoc());
                    break;
                }
                case LASTMANSTANDING: {
                    LastManStanding lms = (LastManStanding) ev;
                    Location l = ev.getArena().getRegionByname("dead").getRandomLoc();
                    e.setRespawnLocation(l);
                    p.teleport(l);
                    break;
                }
                case SPLEEF: {
                    Location l = ev.getArena().getRegionByname("dead").getRandomLoc();
                    e.setRespawnLocation(l);
                    p.teleport(l);
                    break;
                }
            }
        }
    }


    @EventHandler
    public void EntityDamageByEntity(EntityDamageByEntityEvent e) {
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
                                if (!d.getInventory().contains(Material.ARROW)) {
                                    d.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                                    d.sendMessage(MessageUtils.translateMessage(Language.Oitc_Add_Arrow, new HashMap<>()));
                                }
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("%killed%", k.getName());
                                hm.put("%killer%", d.getName());
                                ev.sendMessageToPartaking(MessageUtils.translateMessage(Language.Oitc_Kill_Player, hm));
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            Entity damager = e.getDamager();
            Entity damaged = e.getEntity();
            if (damager.getType() == EntityType.PLAYER && damaged.getType() == EntityType.PLAYER) {
                Player k = (Player) damager;
                Player d = (Player) damaged;

                Event ev1 = EventManager.getEventPlayerPartaking(k);
                Event ev2 = EventManager.getEventPlayerPartaking(d);
                if (ev1 != null && ev2 != null) {
                    if (ev1.getName().equalsIgnoreCase(ev2.getName())) {
                        Region r = null;
                        for (Region n : ev1.getArena().getAllRegions()) {
                            if (n.isInBoundsXZ(k.getLocation())) {
                                r = n;
                                break;
                            }
                        }
                        if (r != null) {
                            if (!r.pvp) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

}
