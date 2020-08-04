package me.erksterk.openeventsmc.events;

import me.erksterk.openeventsmc.Main;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.misc.EventStatus;
import me.erksterk.openeventsmc.misc.Region;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirstToLocation extends Event{
    //This Event can be used as a Maze event, deathrun event and basically all events that has the winning requirement of reaching a location.
    //You can also utilize WorldGuard for now to setup pvp zones etc.

    public boolean respawn = false;
    public int how_many_winners = 1;
    private List<Player> winners = new ArrayList<>();
    private BukkitTask taskGame;

    public FirstToLocation(String name) {
        super(name);
        requiredFields.add("arena.main");
        requiredFields.add("arena.spawn");
        requiredFields.add("arena.wait");
        requiredFields.add("arena.end");
        requiredFields.add("arena.dead");

        requiredFields.add("config.respawn");
        requiredFields.add("config.how_many_winners");
    }

    public void checkForWinners(){
        Region end = getArena().getRegionByname("end");
        for (Player p : getAllPlayersInRegionXZ(end)) {
            if(!winners.contains(p)) {
                winners.add(p);
                HashMap<String, String> hm = new HashMap<>();
                hm.put("%player%", p.getName());
                announceMessage(MessageUtils.translateMessage(Language.FTL_winner, hm));
            }
        }
        if(winners.size()>=how_many_winners){
            HashMap<String,String> win = new HashMap<>();
            String mes="";
            for(Player p : winners){
                if(mes.equalsIgnoreCase("")){
                    mes=p.getName();
                }else{
                    mes=mes+", "+p.getName();
                }
            }
            win.put("%winners%",mes);
            announceMessage(MessageUtils.translateMessage(Language.FTL_all_winner,win));
            running=false;
            setStatus(EventStatus.STOPPED);
            winners.clear();
            getPlayers().clear();
            eliminated.clear();
            setHoster(null);
            taskGame.cancel();
        }
        if(winners.size()>=getAllAlivePlayers().size()){
            HashMap<String,String> win = new HashMap<>();
            String mes="";
            for(Player p : winners){
                if(mes.equalsIgnoreCase("")){
                    mes=p.getName();
                }else{
                    mes=mes+", "+p.getName();
                }
            }
            win.put("%winners%",mes);
            announceMessage(MessageUtils.translateMessage(Language.FTL_all_winner,win));
            running=false;
            setStatus(EventStatus.STOPPED);
            winners.clear();
            getPlayers().clear();
            eliminated.clear();
            setHoster(null);
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
    public void joinPlayer(Player p) {
        getPlayers().add(p);
        p.teleport(getArena().getRegionByname("wait").getRandomLoc());

        for (ItemStack it : getEventStartGear()) {
            p.getInventory().addItem(it);
        }
    }
    public void start() {
        Region r = getArena().getRegionByname("wait");

        taskGame = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, () -> {

            if (running) {
                setStatus(EventStatus.RUNNING);
                checkForWinners();
                for (Player p : getAllPlayersInRegionXZ(r)) {
                    Location l = getArena().getRegionByname("spawn").getRandomLoc();
                    l.setY(getArena().getRegionByname("spawn").getMax().getBlockY());
                    p.teleport(l);
                }


            }
        }, 0, 1);
    }



}
