package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.misc.Arena;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.EventType;
import me.erksterk.openeventsmc.misc.Region;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event {


    public boolean running=false;
    private List<Player> players = new ArrayList<>();
    private Arena arena;
    private EventStatus status;
    private String name;
    private EventType type;
    public List<ItemStack> start_gear = new ArrayList<>();
    public List<ItemStack> respawn_gear = new ArrayList<>();


    public List<String> requiredFields = new ArrayList<>();
    public List<String> setFields = new ArrayList<>();
    private Player host;

    public Event(String name) {
        this.status = EventStatus.STOPPED;
        this.name = name;
    }

    public EventStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public boolean isPlayerPartaking(Player p) {
        if (players.contains(p)) return true;
        return false;
    }

    public List<Player> getPlayers(){
        return players;
    }

    //TODO: messages
    public void joinPlayer(Player p) {
        players.add(p);
    }

    public void leavePlayer(Player p) {
        players.remove(p);
    }

    public void clearPlayers(){
        players.clear();
    }

    public Arena getArena() {
        return arena;
    }


    public void setType(EventType eventtype) {
        this.type = eventtype;
    }

    public List<String> getMissingFields() {
        List<String> req = new ArrayList<>();
        for (String s : requiredFields) {
            if (!setFields.contains(s.toLowerCase())) req.add(s);
        }
        return req;
    }

    public void setArena(Arena a) {
        this.arena = a;
    }
    public List<ItemStack> getEventStartGear(){
        return start_gear;
    }
    public void setEventStartGear(Inventory inv){
        start_gear.clear();
        for(ItemStack it : inv.getContents()) {
            if(it!=null) start_gear.add(it);
        }
    }

    public EventType getType() {
        return type;
    }

    public void setStatus(EventStatus eventstatus) {
        this.status = eventstatus;
    }

    public void setHoster(Player p) {
        this.host = p;
    }

    public void executeWin(Player p) {
    }

    public void start() {
    }

    public void sendMessageToPartaking(String message){
        for(Player p : players){
            p.sendMessage(message);
        }
    }

    public List<Player> getAllPlayersInRegion(Region r) {
        List<Player> ret = new ArrayList<>();
        for (Player p : players) {
            if (r.isInBounds(p.getLocation())) ret.add(p);
        }
        return ret;
    }
    public List<Player> getAllPlayersInRegionXZ(Region r) {
        List<Player> ret = new ArrayList<>();
        for (Player p : players) {
            if (r.isInBoundsXZ(p.getLocation())) ret.add(p);
        }
        return ret;
    }


    public void setEventRespawnGear(Inventory inv) {
        respawn_gear.clear();
        for(ItemStack it : inv.getContents()) {
            if(it!=null) respawn_gear.add(it);
        }
    }

    public static int getFieldInt(Event e,String s) {
        String[] field = s.split("\\.");
        try {
            Field f1 = e.getClass().getField(field[1]);
            return (int) f1.get(e);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
