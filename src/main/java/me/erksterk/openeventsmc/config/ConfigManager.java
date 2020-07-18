package me.erksterk.openeventsmc.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    static ConfigManager instance = new ConfigManager();

    public static ConfigManager getInstance() {
        return instance;
    }

    Plugin p;



    FileConfiguration event;
    File eventfile;
    public void setup(Plugin p) {

       eventfile= new File(p.getDataFolder(), "events.yml");

        if (!eventfile.exists()) {
            try {
                eventfile.createNewFile();
            }
            catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create events.yml!");
            }
        }

        event = YamlConfiguration.loadConfiguration(eventfile);

    }

    public FileConfiguration getEvent() {
        return event;
    }
    public void saveEvent() {
        try {
            event.save(eventfile);
        }
        catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save events.yml!");
        }
    }

    public void reloadData() {
        event = YamlConfiguration.loadConfiguration(eventfile);
    }

}
