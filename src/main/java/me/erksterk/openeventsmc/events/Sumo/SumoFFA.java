package me.erksterk.openeventsmc.events.Sumo;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.Event;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SumoFFA extends Event {

    BukkitTask taskGame;

    public SumoFFA(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.wait");
        requiredFields.add("arena.dead");
        requiredFields.add("arena.pvp");
        requiredFields.add("arena.out");
        requiredFields.add("inventory.start_gear");

        forcedMaps.put("region.pvp.pvp", true);
    }

    public void joinPlayer(Player p) {
        getPlayers().add(p);
        p.teleport(getArena().getRegionByname("wait").getRandomLoc());
        //TODO: save and give back the players inventory after the game!
        p.getInventory().clear();

        for (ItemStack it : getEventStartGear()) {
            p.getInventory().addItem(it);
        }
    }

    public void start() {
        taskGame = Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            if (running) {
                setStatus(EventStatus.RUNNING);
                checkForWinner();
                for (Player p : getAllAlivePlayers()) {
                    if (getArena().getRegionByname("wait").isInBoundsXZ(p.getLocation())) {
                        Location l = getArena().getRegionByname("pvp").getRandomLoc();
                        p.teleport(l);
                    } else if (getArena().getRegionByname("out").isInBounds(p.getLocation())) {
                        p.teleport(getArena().getRegionByname("dead").getRandomLoc());
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("%player%", p.getName());
                        eliminated.add(p);
                        announceMessage(MessageUtils.translateMessage(Language.Sumo_eliminated, hm));
                    }
                }
            }

        }, 0, 1);
    }

    private void checkForWinner() {
        List<Player> a = getAllAlivePlayers();
        if (a.size() == 0) {
            announceMessage(MessageUtils.translateMessage(Language.Sumo_no_winner, new HashMap<>()));
            running = false;
            clearPlayers();
            eliminated.clear();
            setStatus(EventStatus.STOPPED);
            taskGame.cancel();
        } else if (a.size() == 1) {
            HashMap<String, String> hm = new HashMap<>();
            Player p = a.get(0);
            hm.put("%player%", p.getName());
            announceMessage(MessageUtils.translateMessage(Language.Sumo_winner, hm));
            running = false;
            clearPlayers();
            eliminated.clear();
            setStatus(EventStatus.STOPPED);
            taskGame.cancel();
        }
    }

    private List<Player> getAllAlivePlayers() {
        List<Player> alive = new ArrayList<>();
        for (Player p : getPlayers()) {
            if (!eliminated.contains(p)) {
                alive.add(p);
            }
        }
        return alive;
    }
}
