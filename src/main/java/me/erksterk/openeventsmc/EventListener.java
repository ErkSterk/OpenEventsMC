package me.erksterk.openeventsmc;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EventListener implements Listener {
    public EventListener(Plugin plugin){
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

    }
}
