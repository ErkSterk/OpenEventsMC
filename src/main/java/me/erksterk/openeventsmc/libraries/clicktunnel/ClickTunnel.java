package me.erksterk.openeventsmc.libraries.clicktunnel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ClickTunnel {

    public static void register(Plugin plugin){
        Bukkit.getServer().getPluginManager().registerEvents(new GuiListener(),plugin);
    }
}
