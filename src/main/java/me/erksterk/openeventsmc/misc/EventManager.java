package me.erksterk.openeventsmc.misc;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.ConfigManager;
import me.erksterk.openeventsmc.events.Event;
import me.erksterk.openeventsmc.events.Waterdrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
            }
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
