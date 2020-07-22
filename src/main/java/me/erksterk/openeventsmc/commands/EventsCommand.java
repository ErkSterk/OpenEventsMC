package me.erksterk.openeventsmc.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.*;
import me.erksterk.openeventsmc.events.Waterdrop;
import me.erksterk.openeventsmc.misc.*;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class EventsCommand implements CommandExecutor {

    public static HashMap<Player,Event> setupMode = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("events")) {
            if (args.length == 0) {
                sender.sendMessage("Running OEMC v0.1");
            } else {
                switch (args[0]) {
                    case "create": {
                        if (args.length == 1) {
                            sender.sendMessage("You need to specify the eventname and type");
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
                                }
                                if (e != null) {
                                    if (EventManager.isEventNameAvailable(eventname)) {
                                        EventManager.createEvent(e);
                                        sender.sendMessage("Created event");
                                    } else {
                                        sender.sendMessage("EventName is already taken!");
                                    }
                                } else {
                                    sender.sendMessage("Invalid eventtype!");
                                }
                            } else {
                                sender.sendMessage("You need to specify the eventname and type");
                            }
                        }
                        break;
                    }
                    case "setup": {
                        Player p = (Player) sender;
                        if(setupMode.containsKey(p)){
                            Event e = setupMode.get(p);
                            if(args.length==1){
                                List<String> missing = e.getMissingFields();
                                if(missing.size()==0){
                                    sender.sendMessage("Event is fully setup!");
                                    setupMode.remove(p);
                                }else{
                                    for(String s : missing){
                                        sender.sendMessage(s);
                                    }
                                }
                            }else{
                                switch(args[1]){
                                    case "set":
                                    {
                                        if(args.length==2){
                                            sender.sendMessage("select a field as a param");
                                        }else{
                                            String field = args[2].toLowerCase();
                                            if(e.requiredFields.contains(field)){
                                                if(field.contains("arena.")){
                                                    Selection sel = Main.worldedit.getSelection(p);
                                                    if(field.equalsIgnoreCase("arena.main")){
                                                        Region r = new Region(sel.getMinimumPoint(),sel.getMaximumPoint(),"main");
                                                        Arena a = new Arena(r);
                                                        e.setArena(a);
                                                        e.setFields.add("arena.main");
                                                        setupMode.put(p,e);
                                                        EventManager.setEvent(e);
                                                    }else{
                                                        String[] spli = field.split("\\.");
                                                        String rname = spli[1];
                                                        Region r = new Region(sel.getMinimumPoint(),sel.getMaximumPoint(),rname);
                                                        Arena a = e.getArena();
                                                        a.addRegion(r);
                                                        e.setArena(a);
                                                        e.setFields.add(field);
                                                        setupMode.put(p,e);
                                                        EventManager.setEvent(e);
                                                    }
                                                }else if(field.contains("config.")){
                                                    if(args.length==4){
                                                        String fieldname = field.split("\\.")[1];
                                                        try {
                                                            Field f1 = e.getClass().getField(fieldname);
                                                            if(f1.getType().equals(int.class)) {
                                                                f1.set(e, Integer.parseInt(args[3]));
                                                            }else{
                                                                f1.set(e,args[3]);
                                                            }
                                                            e.setFields.add(field);
                                                            EventManager.setEvent(e);
                                                        } catch (NoSuchFieldException ex) {
                                                            sender.sendMessage("Invalid configfield");
                                                        } catch (IllegalAccessException ex) {
                                                            sender.sendMessage("Not allowed!");
                                                        }
                                                    }else{
                                                        sender.sendMessage("You need to specify a value!");
                                                    }
                                                }else if(field.contains("inventory.")){
                                                    String fieldname = field.split("\\.")[1];
                                                    if(fieldname.equalsIgnoreCase("start_gear")){
                                                        e.setEventStartGear(p.getInventory()); //Sets the events starter gear to the players inventory!
                                                        e.setFields.add(field);
                                                    }else if(fieldname.equalsIgnoreCase("respawn_gear")){
                                                        e.setEventRespawnGear(p.getInventory()); //Sets the events starter gear to the players inventory!
                                                        e.setFields.add(field);
                                                    }
                                                    EventManager.setEvent(e);
                                                }
                                            }else{
                                                sender.sendMessage("The Event of this type does not require this field!");
                                            }
                                        }
                                    }
                                }
                            }
                        }else {
                            if (args.length == 1) {
                                sender.sendMessage("Specify an eventname");
                            } else {
                                String eventname = args[1];
                                Event e = EventManager.getEventFromName(eventname);
                                if (e != null) {
                                    sender.sendMessage("Entered setup mode for this event");

                                    setupMode.put(p, e);
                                } else {
                                    sender.sendMessage("Could not find event by this name!");
                                }
                            }
                        }
                        break;
                    }
                    case "host":
                    {
                        Player p = (Player) sender;
                        if(args.length==1){
                            sender.sendMessage("type the name of the event u wish to host!");
                        }else{
                            Event e = EventManager.getEventFromName(args[1]);
                            if(e!=null) {
                                if(e.getStatus()== EventStatus.STOPPED) {
                                    HashMap<String,String> ar = new HashMap<>();
                                    ar.put("%type%",e.getType().toString());
                                    ar.put("%name%",e.getName());
                                    ar.put("%player%",p.getName());
                                    for(String s : Language.Event_host_announce){
                                        Bukkit.broadcastMessage(MessageUtils.translateMessage(s,ar));
                                    }
                                    e.setStatus(EventStatus.STARTED);
                                    e.setHoster(p);
                                    e.start();
                                }else{
                                    sender.sendMessage("Someone is running the event rn!");
                                }
                            }
                        }
                        break;
                    }
                    case "pause":
                    {
                        Player p = (Player) sender;
                        if(args.length==1){
                            sender.sendMessage("type the name of the event u wish to host!");
                        }else{
                            Event e = EventManager.getEventFromName(args[1]);
                            if(e!=null) {
                                if(e.running){
                                    p.sendMessage("Paused event!");
                                    e.running=false;
                                }else{
                                    p.sendMessage("Started event!");
                                    e.running=true;
                                }
                            }
                        }
                        break;
                    }
                    case "join":
                    {
                        if(args.length==1){
                            sender.sendMessage("specify the eventnname!");
                        }else {
                            Event e = EventManager.getEventFromName(args[1]);
                            if(e!=null){
                                if(e.getStatus()==EventStatus.STARTED){
                                    Player p = (Player) sender;
                                    e.joinPlayer(p);
                                    HashMap<String,String> ar = new HashMap<>();
                                    ar.put("%player%",p.getName());
                                    p.sendMessage(MessageUtils.translateMessage(Language.Event_join,ar));
                                }else{
                                    sender.sendMessage("This event is currently not running!");
                                }
                            }else{
                                sender.sendMessage("event does not exist");
                            }
                        }
                    }

                }
            }
            return true;
        }
        return false;
    }
}
