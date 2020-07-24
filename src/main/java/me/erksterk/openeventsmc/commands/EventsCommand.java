package me.erksterk.openeventsmc.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.*;
import me.erksterk.openeventsmc.events.Waterdrop;
import me.erksterk.openeventsmc.misc.*;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class EventsCommand implements CommandExecutor {

    public static HashMap<Player, Event> setupMode = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("events")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aRunning OEMC by &6ErkSterk - &cv."+Main.version));
            } else {
                switch (args[0]) {
                    case "create": {
                        if (sender.hasPermission("oemc.events.create")) {
                            if (args.length == 1) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_missing_name,new HashMap<>()));
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
                                    }
                                    if (e != null) {
                                        if (EventManager.isEventNameAvailable(eventname)) {
                                            EventManager.createEvent(e);
                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_created,new HashMap<>()));
                                        } else {
                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_already_created,new HashMap<>()));
                                        }
                                    } else {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_invalid_eventtype,new HashMap<>()));
                                    }
                                } else {
                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_create_missing_eventtype,new HashMap<>()));
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
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_done,new HashMap<>()));
                                        setupMode.remove(p);
                                    } else {
                                        sender.sendMessage(ChatColor.RED+"Missing Fields:");
                                        for (String s : missing) {
                                            sender.sendMessage(ChatColor.RED+s);
                                        }
                                    }
                                } else {
                                    switch (args[1]) {
                                        case "set": {
                                            if (args.length == 2) {
                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_missing_field,new HashMap<>()));
                                            } else {
                                                String field = args[2].toLowerCase();
                                                if (e.requiredFields.contains(field)) {
                                                    if (field.contains("arena.")) {
                                                        Selection sel = Main.worldedit.getSelection(p);
                                                        if (field.equalsIgnoreCase("arena.main")) {
                                                            Region r = new Region(sel.getMinimumPoint(), sel.getMaximumPoint(), "main");
                                                            Arena a = new Arena(r);
                                                            e.setArena(a);
                                                            e.setFields.add("arena.main");
                                                            setupMode.put(p, e);
                                                            EventManager.setEvent(e);
                                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_success,new HashMap<>()));
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
                                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_success,new HashMap<>()));
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
                                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_success,new HashMap<>()));
                                                            } catch (NoSuchFieldException ex) {
                                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_invalid,new HashMap<>()));
                                                            } catch (IllegalAccessException ex) {
                                                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_illegal,new HashMap<>()));
                                                            }
                                                        } else {
                                                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_missing_value,new HashMap<>()));
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
                                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_success,new HashMap<>()));
                                                    }
                                                } else {
                                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_set_field_not_needed,new HashMap<>()));
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
                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_missing_eventname,new HashMap<>()));
                                } else {
                                    String eventname = args[1];
                                    Event e = EventManager.getEventFromName(eventname);
                                    if (e != null) {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_entered,new HashMap<>()));
                                        setupMode.put(p, e);
                                    } else {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_setup_missing_eventname,new HashMap<>()));
                                    }
                                }
                            }
                        } else {
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "host": {
                        if(sender.hasPermission("oemc.events.host")) {
                            Player p = (Player) sender;
                            if (args.length == 1) {
                                sender.sendMessage(MessageUtils.translateMessage(Language.Command_host_event_event_missing,new HashMap<>()));
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
                                    } else {
                                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_host_event_already,new HashMap<>()));
                                    }
                                }else{
                                    sender.sendMessage(MessageUtils.translateMessage(Language.Command_event_not_found,new HashMap<>()));
                                }
                            }

                        }else{
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "pause": {
                        if(sender.hasPermission("oemc.events.pause")) {
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
                        }else{
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    case "join": {
                        if(sender.hasPermission("oemc.events.join")) {
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
                        }else{
                            sender.sendMessage(MessageUtils.translateMessage(Language.Command_No_Permission, new HashMap<>()));
                        }
                        break;
                    }
                    default:{
                        sender.sendMessage(MessageUtils.translateMessage(Language.Command_not_found,new HashMap<>()));
                        break;
                    }

                }
            }
            return true;
        }
        return false;
    }
}
