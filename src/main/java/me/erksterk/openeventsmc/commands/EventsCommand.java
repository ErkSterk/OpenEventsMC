package me.erksterk.openeventsmc.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.*;
import me.erksterk.openeventsmc.events.Waterdrop;
import me.erksterk.openeventsmc.libraries.clicktunnel.Gui;
import me.erksterk.openeventsmc.libraries.clicktunnel.GuiAction;
import me.erksterk.openeventsmc.libraries.clicktunnel.GuiManager;
import me.erksterk.openeventsmc.misc.*;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventsCommand implements CommandExecutor {

    public static HashMap<Player, Event> setupMode = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("events")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aRunning OEMC by &6ErkSterk - &cv." + Main.version));
            } else {
                switch (args[0]) {
                    case "create": {
                        if (sender.hasPermission("oemc.events.create")) {
                            if (args.length == 1) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_missing_name, new HashMap<>()));
                            } else {
                                if (args.length == 3) {
                                    String eventname = args[1];
                                    String eventtype = args[2].toLowerCase();
                                    Event e = null;
                                    switch (eventtype) {
                                        case "waterdrop": {
                                            e = new Waterdrop(eventname);
                                            e.setType(EventType.WATERDROP);
                                            break;
                                        }
                                        case "oitc": {
                                            e = new OneInTheChamber(eventname);
                                            e.setType(EventType.ONEINTHECHAMBER);
                                            break;
                                        }
                                        case "redrover": {
                                            e = new RedRover(eventname);
                                            e.setType(EventType.REDROVER);
                                            break;
                                        }
                                        case "lastmanstanding": {
                                            e = new LastManStanding(eventname);
                                            e.setType(EventType.LASTMANSTANDING);
                                            break;
                                        }
                                        case "spleef": {
                                            e = new Spleef(eventname);
                                            e.setType(EventType.SPLEEF);
                                            break;
                                        }
                                        case "woolshuffle": {
                                            e = new WoolShuffle(eventname);
                                            e.setType(EventType.WOOLSHUFFLE);
                                            break;
                                        }
                                    }
                                    if (e != null) {
                                        if (EventManager.isEventNameAvailable(eventname)) {
                                            EventManager.createEvent(e);
                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_created, new HashMap<>()));
                                        } else {
                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_already_created, new HashMap<>()));
                                        }
                                    } else {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_invalid_eventtype, new HashMap<>()));
                                    }
                                } else {
                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_missing_eventtype, new HashMap<>()));
                                }
                            }

                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "setup": {
                        if (sender.hasPermission("oemc.events.setup")) {
                            Player p = (Player) sender;
                            if (setupMode.containsKey(p)) {
                                Event e = setupMode.get(p);
                                if (args.length == 1) {
                                    List<String> missing = e.getMissingFields();
                                    if (missing.size() == 0) {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_done, new HashMap<>()));
                                        setupMode.remove(p);
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "Missing Fields:");
                                        for (String s : missing) {
                                            sender.sendMessage(ChatColor.RED + s);
                                        }
                                    }
                                } else {
                                    switch (args[1]) {
                                        case "set": {
                                            if (args.length == 2) {
                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_missing_field, new HashMap<>()));
                                            } else {
                                                String field = args[2].toLowerCase();
                                                if (e.requiredFields.contains(field)) {
                                                    if (field.contains("arena.")) {
                                                        Selection sel = Main.worldedit.getSelection(p);
                                                        if (sel != null) {
                                                            if (field.equalsIgnoreCase("arena.main")) {
                                                                Region r = new Region(sel.getMinimumPoint(), sel.getMaximumPoint(), "main");
                                                                Arena a = new Arena(r);
                                                                e.setArena(a);
                                                                e.setFields.add("arena.main");
                                                                setupMode.put(p, e);
                                                                EventManager.setEvent(e);
                                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_success, new HashMap<>()));
                                                            } else {
                                                                String[] spli = field.split("\\.");
                                                                String rname = spli[1];
                                                                Region r = new Region(sel.getMinimumPoint(), sel.getMaximumPoint(), rname);
                                                                Arena a = e.getArena();
                                                                a.addRegion(r);
                                                                e.setArena(a);
                                                                e.setFields.add(field);
                                                                setupMode.put(p, e);
                                                                EventManager.setEvent(e);
                                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_success, new HashMap<>()));
                                                            }
                                                        } else {
                                                            sender.sendMessage(MessageUtils.translateMessage(Language.Commands_setup_set_missing_selection, new HashMap<>()));
                                                        }
                                                    } else if (field.contains("config.")) {
                                                        if (args.length == 4) {
                                                            String fieldname = field.split("\\.")[1];
                                                            try {
                                                                Field f1 = e.getClass().getField(fieldname);
                                                                if (f1.getType().equals(int.class)) {
                                                                    f1.set(e, Integer.parseInt(args[3]));
                                                                } else {
                                                                    f1.set(e, args[3]);
                                                                }
                                                                e.setFields.add(field);
                                                                EventManager.setEvent(e);
                                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_success, new HashMap<>()));
                                                            } catch (NoSuchFieldException ex) {
                                                                ex.printStackTrace();
                                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_invalid, new HashMap<>()));
                                                            } catch (IllegalAccessException ex) {
                                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_illegal, new HashMap<>()));
                                                            }
                                                        } else {
                                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_missing_value, new HashMap<>()));
                                                        }
                                                    } else if (field.contains("inventory.")) {
                                                        String fieldname = field.split("\\.")[1];
                                                        if (fieldname.equalsIgnoreCase("start_gear")) {
                                                            e.setEventStartGear(p.getInventory()); //Sets the events starter gear to the players inventory!
                                                            e.setFields.add(field);
                                                        } else if (fieldname.equalsIgnoreCase("respawn_gear")) {
                                                            e.setEventRespawnGear(p.getInventory()); //Sets the events starter gear to the players inventory!
                                                            e.setFields.add(field);
                                                        }
                                                        EventManager.setEvent(e);
                                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_success, new HashMap<>()));
                                                    }
                                                } else {
                                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_field_not_needed, new HashMap<>()));
                                                }

                                            }
                                            break;
                                        }
                                        default: {
                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_not_found, new HashMap<>()));
                                            break;
                                        }
                                    }
                                }
                            } else {
                                if (args.length == 1) {
                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_missing_eventname, new HashMap<>()));
                                } else {
                                    String eventname = args[1];
                                    Event e = EventManager.getEventFromName(eventname);
                                    if (e != null) {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_entered, new HashMap<>()));
                                        setupMode.put(p, e);
                                    } else {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_missing_eventname, new HashMap<>()));
                                    }
                                }
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "host": {
                        if (sender.hasPermission("oemc.events.host")) {
                            Player p = (Player) sender;
                            if (args.length == 1) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_host_event_event_missing, new HashMap<>()));
                            } else {
                                Event e = EventManager.getEventFromName(args[1]);
                                if (e != null) {
                                    if (e.getStatus() == EventStatus.STOPPED) {
                                        HashMap<String, String> ar = new HashMap<>();
                                        ar.put("%type%", e.getType().toString());
                                        ar.put("%name%", e.getName());
                                        ar.put("%player%", p.getName());
                                        for (String s : Language.Event_host_announce) {
                                            Bukkit.broadcastMessage(MessageUtils.translateMessage(s, ar));
                                        }
                                        e.setStatus(EventStatus.STARTED);
                                        e.setHoster(p);
                                        e.start();
                                        EventManager.loadChangesForMenu();
                                    } else {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_host_event_already, new HashMap<>()));
                                    }
                                } else {
                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_event_not_found, new HashMap<>()));
                                }
                            }

                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "pause": {
                        if (sender.hasPermission("oemc.events.pause")) {
                            Player p = (Player) sender;
                            if (args.length == 1) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_pause_name_missing, new HashMap<>()));
                            } else {
                                Event e = EventManager.getEventFromName(args[1]);
                                if (e != null) {
                                    if (e.running) {
                                        p.sendMessage(MessageUtils.translateMessage(Language.Command_pause_paused, new HashMap<>()));
                                        e.running = false;
                                    } else {
                                        p.sendMessage(MessageUtils.translateMessage(Language.Command_pause_started, new HashMap<>()));
                                        e.running = true;
                                    }
                                }
                            }
                            EventManager.loadChangesForMenu();
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "join": {
                        if (sender.hasPermission("oemc.events.join")) {
                            if (args.length == 1) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_join_missing_event, new HashMap<>()));
                            } else {
                                Event e = EventManager.getEventFromName(args[1]);
                                if (e != null) {
                                    if (e.getStatus() == EventStatus.STARTED) {
                                        Player p = (Player) sender;
                                        e.joinPlayer(p);
                                        HashMap<String, String> ar = new HashMap<>();
                                        ar.put("%player%", p.getName());
                                        p.sendMessage(MessageUtils.translateMessage(Language.Event_join, ar));
                                    } else {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_join_not_running, new HashMap<>()));
                                    }
                                } else {
                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_join_event_not_exists, new HashMap<>()));
                                }
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "revive": {
                        if (sender.hasPermission("oemc.events.revive")) {
                            if (args.length == 1) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_revive_missing_player, new HashMap<>()));
                            } else {
                                String n = args[1];
                                Player p = Bukkit.getPlayer(n);
                                if (p != null) {
                                    Event e = EventManager.getEventPlayerPartaking(p);
                                    if (!e.isPlayerAlive(p)) {
                                        switch (e.getType()) {
                                            case WATERDROP: {
                                                e.revivePlayer(p, e.getArena().getRegionByname("wait"));
                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_revive_revived, new HashMap<>()));
                                                break;
                                            }
                                            case REDROVER: {
                                                RedRover r = (RedRover) e;
                                                Region re;
                                                if (r.red) {
                                                    re = e.getArena().getRegionByname("red");
                                                } else {
                                                    re = e.getArena().getRegionByname("blue");
                                                }
                                                e.revivePlayer(p, re);
                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_revive_revived, new HashMap<>()));
                                                break;
                                            }
                                            case LASTMANSTANDING: {
                                                e.revivePlayer(p, e.getArena().getRegionByname("pvp"));
                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_revive_revived, new HashMap<>()));
                                                break;
                                            }
                                            default: {
                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_revive_event_norevive, new HashMap<>()));
                                            }
                                        }
                                    } else {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_revive_player_alive, new HashMap<>()));
                                    }
                                } else {
                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_revive_player_noexist, new HashMap<>()));
                                }
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "menu": {
                        if (sender.hasPermission("oemc.events.menu")) {
                            Gui g = GuiManager.getGuiFromId("EventsMain");
                            Player p = (Player) sender;
                            p.openInventory(g.guiInv);
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "manage": {
                        if (sender.hasPermission("oemc.events.manage")) {
                            Player p = (Player) sender;
                            Event e = EventManager.getEventByHoster(p);
                            if (e != null) {
                                GuiManager.createGui(e.getName(), 9, e.getName());
                                Gui g = GuiManager.getGuiFromId(e.getName());

                                ItemStack pause = new ItemStack(Material.WOOL, 1);
                                ItemMeta pauseim = pause.getItemMeta();
                                pauseim.setDisplayName("Toggle pause");
                                List<String> lore = new ArrayList<>();
                                if (e.running) {
                                    lore.add("Status: RUNNING");
                                } else {
                                    lore.add("Status: PAUSED");
                                }
                                pauseim.setLore(lore);
                                pause.setItemMeta(pauseim);

                                ItemStack revive = new ItemStack(Material.POTION, 1);
                                ItemMeta reviveim = pause.getItemMeta();
                                reviveim.setDisplayName("Revival");
                                revive.setItemMeta(reviveim);

                                GuiAction pauseAction = new GuiAction(true);
                                pauseAction.commandsPlayer.add("events pause " + e.getName());

                                GuiAction reviveAction = new GuiAction(true);
                                reviveAction.commandsPlayer.add("events revivalmenu " + e.getName());

                                g.setItem(pause, 0, pauseAction);
                                g.setItem(revive, 1, reviveAction);

                                p.openInventory(g.guiInv);
                            } else {
                                sender.sendMessage(MessageUtils.translateMessage(Language.Event_host_none,new HashMap<>()));
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "revivalmenu": {
                        if (sender.hasPermission("oemc.events.revivalmenu")) {
                            Player p = (Player) sender;
                            Event e = EventManager.getEventByHoster(p);
                            if (e != null) {
                                GuiManager.createGui(e.getName()+"_revive", 54, e.getName()+"_revive");
                                Gui g = GuiManager.getGuiFromId(e.getName()+"_revive");

                                int slot = 0;
                                for(Player el : e.eliminated) {
                                    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
                                    SkullMeta m = (SkullMeta) skull.getItemMeta();
                                    m.setOwner(el.getName());
                                    m.setDisplayName(el.getName());
                                    skull.setItemMeta(m);

                                    GuiAction pauseAction = new GuiAction(true);
                                    pauseAction.commandsPlayer.add("events revive " + el.getName());
                                    pauseAction.closeGui=true;
                                    g.setItem(skull, slot, pauseAction);
                                    slot++;
                                }

                                p.openInventory(g.guiInv);
                            }else{
                                sender.sendMessage(MessageUtils.translateMessage(Language.Event_host_none,new HashMap<>()));
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    default: {
                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_not_found, new HashMap<>()));
                        break;
                    }

                }
            }
            return true;
        }
        return false;
    }
}
