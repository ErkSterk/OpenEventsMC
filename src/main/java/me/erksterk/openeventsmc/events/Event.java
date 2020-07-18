package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.misc.Arena;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.EventType;
import me.erksterk.openeventsmc.misc.Region;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Event {


    public boolean running=false;
    private List<Player> players = new ArrayList<>();
    private Arena arena;
    private EventStatus status;
    private String name;
    private EventType type;


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

    public List<Player> getAllPlayers() {
        return players;
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



}
