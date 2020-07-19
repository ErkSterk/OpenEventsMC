package me.erksterk.openeventsmc.misc;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.ConfigManager;
import me.erksterk.openeventsmc.events.Event;
import me.erksterk.openeventsmc.events.OneInTheChamber;
import me.erksterk.openeventsmc.events.Waterdrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static List<Event> events = new ArrayList<>();
    private static ConfigManager conf = ConfigManager.getInstance();

    //TODO: Config
    public static void loadEventsFromConfig() {
        Main.writeToConsole("&cStarting to load events from config");
        events.clear();
        for (String eventname : conf.getEvent().getKeys(false)) {
            String type = conf.getEvent().getString(eventname + ".type");
            Event e = null;

            switch (type) {
                case "WATERDROP": {
                    e = new Waterdrop(eventname);
                    e.setType(EventType.WATERDROP);
                    break;
                }
                case "ONEINTHECHAMBER":{
                    e = new OneInTheChamber(eventname);
                    e.setType(EventType.ONEINTHECHAMBER);
                }
            }
            if(conf.getEvent().isConfigurationSection(eventname+".arena")) {
                for (String arena : conf.getEvent().getConfigurationSection(eventname + ".arena").getKeys(false)) {
                    String loc1 = conf.getEvent().getString(eventname + ".arena." + arena + ".loc1");
                    String[] l1 = loc1.split("_");

                    String loc2 = conf.getEvent().getString(eventname + ".arena." + arena + ".loc2");
                    String[] l2 = loc2.split("_");
                    Location m1 = new Location(Bukkit.getWorld(l1[0]), Integer.parseInt(l1[1]), Integer.parseInt(l1[2]), Integer.parseInt(l1[3]));
                    Location m2 = new Location(Bukkit.getWorld(l2[0]), Integer.parseInt(l2[1]), Integer.parseInt(l2[2]), Integer.parseInt(l2[3]));
                    Region main = new Region(m1, m2, arena);

                    Arena a;
                    if (arena.equalsIgnoreCase("main")) {
                        a = new Arena(main);
                    } else {
                        a = e.getArena();
                    }
                    a.addRegion(main);
                    e.setArena(a);
                    e.setFields.add("arena." + arena);
                }
            }
            if(conf.getEvent().isConfigurationSection(eventname+".config")) {
                for (String c : conf.getEvent().getConfigurationSection(eventname + ".config").getKeys(false)) {
                    String v = conf.getEvent().getString(eventname + ".config." + c);
                    try {
                        Field f1 = e.getClass().getField(c);
                        if (f1.getType().equals(int.class)) {
                            f1.set(e, Integer.parseInt(v));
                        } else {
                            f1.set(e, v);
                        }
                        e.setFields.add("config." + c);
                    } catch (NoSuchFieldException ex) {

                    } catch (IllegalAccessException ex) {

                    }
                    e.setFields.add("config." + c);
                }
            }

            Main.writeToConsole("&aLoaded Event with name: " + e.getName());
            events.add(e);
        }
        Main.writeToConsole("&aFinished loading Events from config!");

    }

    public static void saveEventsToConfig() {

        for (Event e : events) {
            conf.getEvent().set(e.getName() + ".name", e.getName());
            conf.getEvent().set(e.getName() + ".type", e.getType().toString());
            Arena a = e.getArena();
            if (a != null) {
                Region m = a.getArenaBoundRegion();
                if (m != null) {
                    conf.getEvent().set(e.getName() + ".arena.main.loc1", m.getMin().getWorld().getName() + "_" + m.getMin().getBlockX() + "_" + m.getMin().getBlockY() + "_" + m.getMin().getBlockZ());
                    conf.getEvent().set(e.getName() + ".arena.main.loc2", m.getMax().getWorld().getName() + "_" + m.getMax().getBlockX() + "_" + m.getMax().getBlockY() + "_" + m.getMax().getBlockZ());
                }
                for (Region r : a.getAllRegions()) {
                    conf.getEvent().set(e.getName() + ".arena." + r.getName() + ".loc1", r.getMin().getWorld().getName() + "_" + r.getMin().getBlockX() + "_" + r.getMin().getBlockY() + "_" + r.getMin().getBlockZ());
                    conf.getEvent().set(e.getName() + ".arena." + r.getName() + ".loc2", r.getMax().getWorld().getName() + "_" + r.getMax().getBlockX() + "_" + r.getMax().getBlockY() + "_" + r.getMax().getBlockZ());
                }
            }
            for (String c : e.requiredFields) {
                if(c.contains("config.")) {
                    String f = c.split("\\.")[1];
                    String v = null;
                    try {
                        Field f1 = e.getClass().getField(f);
                        v = String.valueOf(f1.get(e));
                    } catch (NoSuchFieldException ex) {

                    } catch (IllegalAccessException ex) {

                    }
                    conf.getEvent().set(e.getName()+"."+c,v);
                }
            }
            conf.saveEvent();
        }
    }

    public static void createEvent(Event e) {
        events.add(e);
        saveEventsToConfig();
    }

    public static void deleteEvent(Event e) {
        events.remove(e);
        saveEventsToConfig();
    }

    public static boolean isEventNameAvailable(String eventname) {
        for (Event e : events) {
            if (e.getName().equalsIgnoreCase(eventname)) return false;
        }
        return true;
    }

    public static Event getEventFromName(String eventname) {
        for (Event e : events) {
            if (e.getName().equalsIgnoreCase(eventname)) return e;
        }
        return null;
    }

    public static void setEvent(Event e) {
        Event r = getEventFromName(e.getName());
        if (r != null) deleteEvent(r);
        createEvent(e);
    }

    public static Event getEventPlayerPartaking(Player p) {
        for (Event e : events) {
            if (e.isPlayerPartaking(p)) return e;
        }
        return null;
    }

}
