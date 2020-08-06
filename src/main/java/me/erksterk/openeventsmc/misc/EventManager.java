package me.erksterk.openeventsmc.misc;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.ConfigManager;
import me.erksterk.openeventsmc.events.*;
import me.erksterk.openeventsmc.events.LastManStanding;
import me.erksterk.openeventsmc.events.OITC.OneInTheChamber;
import me.erksterk.openeventsmc.events.Sumo.SumoFFA;
import me.erksterk.openeventsmc.libraries.clicktunnel.Gui;
import me.erksterk.openeventsmc.libraries.clicktunnel.GuiAction;
import me.erksterk.openeventsmc.libraries.clicktunnel.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                case "ONEINTHECHAMBER": {
                    e = new OneInTheChamber(eventname);
                    e.setType(EventType.ONEINTHECHAMBER);
                    break;
                }
                case "REDROVER": {
                    e = new RedRover(eventname);
                    e.setType(EventType.REDROVER);
                    break;
                }
                case "LASTMANSTANDING": {
                    e = new LastManStanding(eventname);
                    e.setType(EventType.LASTMANSTANDING);
                    break;
                }
                case "SPLEEF": {
                    e = new Spleef(eventname);
                    e.setType(EventType.SPLEEF);
                    break;
                }
                case "WOOLSHUFFLE": {
                    e = new WoolShuffle(eventname);
                    e.setType(EventType.WOOLSHUFFLE);
                    break;
                }
                case "FIRSTTOLOC":{
                    e = new FirstToLocation(eventname);
                    e.setType(EventType.FIRSTTOLOC);
                    break;
                }
                case "SUMO_FFA":{
                    e = new SumoFFA(eventname);
                    e.setType(EventType.SUMO_FFA);
                    break;
                }

            }
            if (conf.getEvent().isConfigurationSection(eventname + ".arena")) {
                for (String arena : conf.getEvent().getConfigurationSection(eventname + ".arena").getKeys(false)) {
                    String loc1 = conf.getEvent().getString(eventname + ".arena." + arena + ".loc1");
                    String[] l1 = loc1.split("_");

                    String loc2 = conf.getEvent().getString(eventname + ".arena." + arena + ".loc2");
                    String[] l2 = loc2.split("_");
                    Location m1 = new Location(Bukkit.getWorld(l1[0]), Integer.parseInt(l1[1]), Integer.parseInt(l1[2]), Integer.parseInt(l1[3]));
                    Location m2 = new Location(Bukkit.getWorld(l2[0]), Integer.parseInt(l2[1]), Integer.parseInt(l2[2]), Integer.parseInt(l2[3]));
                    Region main = new Region(m1, m2, arena,e);

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
            if (conf.getEvent().isConfigurationSection(eventname + ".config")) {
                for (String c : conf.getEvent().getConfigurationSection(eventname + ".config").getKeys(false)) {
                    String v = conf.getEvent().getString(eventname + ".config." + c);
                    try {
                        Field f1 = e.getClass().getField(c);
                        switch(f1.getType().toString()){
                            case "int":{
                                f1.set(e, Integer.parseInt(v));
                                break;
                            }
                            case "boolean":{
                                if(v.equalsIgnoreCase("false")){
                                    f1.set(e, false);
                                }else if(v.equalsIgnoreCase("true")){
                                    f1.set(e, true);
                                }else{
                                    Main.writeToConsole(ChatColor.RED+"Configuration problem for "+eventname+" for boolean "+v);
                                    Main.writeToConsole(ChatColor.RED+"File an issue on github containing your events.yml file aswell ass the above message!");
                                }
                                break;
                            }
                            default:{
                                f1.set(e, v);
                                break;
                            }
                        }
                        e.setFields.add("config." + c);
                    } catch (NoSuchFieldException ex) {

                    } catch (IllegalAccessException ex) {

                    }
                    e.setFields.add("config." + c);
                }
            }
            if (conf.getEvent().isConfigurationSection(eventname + ".inventory")) {
                for (String c : conf.getEvent().getConfigurationSection(eventname + ".inventory").getKeys(false)) {
                    List<ItemStack> items = new ArrayList<>();
                    for (String s : conf.getEvent().getConfigurationSection(eventname + ".inventory." + c).getKeys(false)) {
                        ItemStack it = conf.getEvent().getItemStack(eventname + ".inventory." + c + "." + s);
                        items.add(it);
                    }
                    if (c.contains("start_gear")) {
                        e.start_gear = items;
                    } else if (c.contains("respawn_gear")) {
                        e.respawn_gear = items;
                    }
                    e.setFields.add("inventory." + c);
                }
            }

            Main.writeToConsole("&aLoaded Event with name: " + e.getName());
            events.add(e);
        }
        Main.writeToConsole("&aFinished loading Events from config!");
        loadChangesForMenu();
    }

    public static Material getMaterialForMenu(EventType type) {
        switch (type) {
            case WATERDROP:
                return Material.WATER_BUCKET;
            case SPLEEF:
                return Material.IRON_SPADE;
            case LASTMANSTANDING:
                return Material.GOLDEN_APPLE;
            case REDROVER:
                return Material.REDSTONE_BLOCK;
            case ONEINTHECHAMBER:
                return Material.BOW;
            case FIRSTTOLOC:
                return Material.LEATHER_BOOTS;
            case WOOLSHUFFLE:
                return Material.WOOL;
            case SUMO_FFA:
                return Material.INK_SACK;
            default:
                return Material.DIRT;
        }
    }


    public static Event getEventByHoster(Player p){
        for(Event e : events){
            if(e.getHoster()!=null) {
                if (e.getHoster().getName().equalsIgnoreCase(p.getName())) {
                    return e;
                }
            }
        }
        return null;
    }

    public static void loadChangesForMenu() {
        for (EventType t : EventType.values()) {
            Gui gui = GuiManager.getGuiFromId("event_"+t.toString());
            int slot = 0;
            gui.guiInv.clear();
            for (Event e : EventManager.getAllEventsOfType(t)) {
                ItemStack it = new ItemStack(getMaterialForMenu(t), 1);
                ItemMeta im = it.getItemMeta();
                im.setDisplayName(e.getName());
                List<String> lore = new ArrayList<>();
                lore.add("Status: " + e.getStatus().name());
                Player h = e.getHoster();
                String n = "None";
                if (h != null) {
                    n = h.getName();
                }
                lore.add("Hoster: "+n);
                im.setLore(lore);
                it.setItemMeta(im);

                GuiAction action = new GuiAction(true);
                action.closeGui=true;
                action.commandsPlayer.add("events host "+e.getName());
                gui.setItem(it, slot, action);
                slot++;
            }
        }
    }

    public static void saveEventsToConfig() {

        for (Event e : events) {
            conf.getEvent().set(e.getName() + ".name", e.getName());
            conf.getEvent().set(e.getName() + ".type", e.getType().toString());
            Arena a = e.getArena();
            try {
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
            } catch (NullPointerException ex) {
                System.out.println("No arena found!");
            }
            for (String c : e.requiredFields) {
                if (c.contains("config.")) {
                    String f = c.split("\\.")[1];
                    String v = null;
                    try {
                        Field f1 = e.getClass().getField(f);
                        v = String.valueOf(f1.get(e));
                    } catch (NoSuchFieldException ex) {

                    } catch (IllegalAccessException ex) {

                    }
                    conf.getEvent().set(e.getName() + "." + c, v);
                } else if (c.contains("inventory.")) {
                    if (c.equalsIgnoreCase("inventory.start_gear")) {
                        List<ItemStack> li = e.getEventStartGear();
                        int i = 0;
                        for (ItemStack it : li) {
                            conf.getEvent().set(e.getName() + "." + c + "." + i, it);
                            i++;
                        }
                    } else if (c.equalsIgnoreCase("inventory.respawn_gear")) {
                        List<ItemStack> li = e.respawn_gear;
                        int i = 0;
                        for (ItemStack it : li) {
                            conf.getEvent().set(e.getName() + "." + c + "." + i, it);
                            i++;
                        }
                    }
                }
            }
            conf.saveEvent();
        }
        loadChangesForMenu();
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

    public static List<Event> getAllEventsOfType(EventType eventtype) {
        List<Event> ev = new ArrayList<>();
        for (Event e : events) {
            if (e.getType() == eventtype) {
                ev.add(e);
            }
        }
        return ev;
    }
}
