package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.Region;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LastManStanding extends Event {
    public LastManStanding(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.pvp");
        requiredFields.add("arena.wait");
        requiredFields.add("arena.dead");
        requiredFields.add("inventory.start_gear");
    }

    private BukkitTask taskGame = null;
    public int schedulerSeconds = 0;


    public void joinPlayer(Player p) {
        getPlayers().add(p);
        p.teleport(getArena().getRegionByname("wait").getRandomLoc());

        //TODO: save and give back the players inventory after the game!
        p.getInventory().clear();

        for (ItemStack it : getEventStartGear()) {
            p.getInventory().addItem(it);
        }
    }

    public void announceMessage(String message) {
        if (Config.lms_announce_to_all_online) {
            Bukkit.broadcastMessage(message);
        }
        if (Config.lms_announce_to_all_partaking) {
            sendMessageToPartaking(message);
        }
    }

    public void start() {
        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {
            Region r = getArena().getRegionByname("wait");
            if (running) {
                for (Player p : getAllPlayersInRegionXZ(r)) {
                    p.teleport(getArena().getRegionByname("pvp").getRandomLoc());
                }
                setStatus(EventStatus.RUNNING);
                schedulerSeconds++;
                checkForWinner();
            }
        }, 0, 20);
    }

    private void checkForWinner() {
        List<Player> alive = new ArrayList<>();
        for(Player p : getPlayers()){
            if(!eliminated.contains(p)) alive.add(p);
        }
        if(alive.size()==0){
            announceMessage(MessageUtils.translateMessage(Language.LastManStanding_nowinner,new HashMap<>()));
            running=false;
            setStatus(EventStatus.STOPPED);
            clear();
        }else if(alive.size()==1){
            HashMap<String,String> hm = new HashMap<>();
            hm.put("%player%",alive.get(0).getName());
            announceMessage(MessageUtils.translateMessage(Language.LastManStanding_winner,hm));
            running=false;
            setStatus(EventStatus.STOPPED);
            clear();
        }
    }

    private void clear() {
        getPlayers().clear();
        eliminated.clear();
        schedulerSeconds=0;
        setHoster(null);
        taskGame.cancel();
    }
}
