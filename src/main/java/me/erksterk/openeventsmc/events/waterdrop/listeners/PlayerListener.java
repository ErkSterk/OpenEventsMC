package me.erksterk.openeventsmc.events.waterdrop.listeners;

import me.erksterk.openeventsmc.EventListener;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.waterdrop.Waterdrop;
import me.erksterk.openeventsmc.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class PlayerListener extends EventListener {
    public PlayerListener(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (Waterdrop.isPlayerInWaterdrop(p)&& Waterdrop.isPlayerParticipating(p)) {
            //The player died inside the waterdrop, this means he should be removed from the game.
            Waterdrop.waterdrop_dead_players.add(p);
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (Waterdrop.isPlayerInWaterdrop(p)&& Waterdrop.isPlayerParticipating(p)) {
            if (Waterdrop.waterdrop_dead_players.contains(p)) {
                e.setRespawnLocation(Waterdrop.waterdrop_death);
            }
        }
    }

    @EventHandler
    public void hitwater(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (Waterdrop.isPlayerInWaterdrop(p) && Waterdrop.isPlayerParticipating(p)) {
            if (e.getTo().getBlock().getType() == Material.WATER || e.getTo().getBlock().getType() == Material.STATIONARY_WATER) {
                //The player managed to get into the water.
                //Doing a hack check too see if the player has clipped down and joined the event again
                if (Waterdrop.waterdrop_dead_players.contains(p)) {
                    //What? The player is registered dead but made it down.
                    p.teleport(Waterdrop.waterdrop_death);
                    HashMap<String,String> placeholders = new HashMap<>();
                    placeholders.put("%player%",p.getName());
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        //Look for staff members and tell them about the player
                        if (pl.hasPermission("openeventsmc.staff")) {
                            pl.sendMessage(MessageUtils.translateMessage(Language.WATERDROP_PLAYERSUPPOSEDTOBEDEAD,placeholders));
                        }
                    }
                } else {
                    //Its all gucci, the player is not supposed to be dead
                    p.teleport(Waterdrop.waterdrop_wait);
                }
            }
        }

    }
}
