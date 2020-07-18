package me.erksterk.openeventsmc;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.ConfigManager;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.misc.EventManager;
import me.erksterk.openeventsmc.commands.EventsCommand;
import me.erksterk.openeventsmc.listeners.PlayerListener;
import me.erksterk.openeventsmc.libraries.tinyphoenix.config.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Context;

public class Main extends JavaPlugin {


    public static Context context;
    public static String version;
    public static Plugin plugin;
    private static ConsoleCommandSender console;
    public static boolean PAPI=false;

    ConfigManager configManager = ConfigManager.getInstance();

    public static void writeToConsole(String message){
        console.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    public static WorldEditPlugin worldedit = null;
    @Override
    public void onEnable(){
        plugin=this;
        console= Bukkit.getConsoleSender();
        writeToConsole("&cStarting loading of OpenEventsMC");
        worldedit=(WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("worldedit");
        version=getDescription().getVersion();
        configManager.setup(this);
        //Initialize config files
        StorageHandler.loadConfig(getDataFolder().toString(),"config",new Config());
        StorageHandler.loadConfig(getDataFolder().toString(),"language",new Language());
        writeToConsole("&aConfigs Loaded!");
        if(checkForMissingDependency("WorldEdit")){
            writeToConsole("&cERROR, Missing WorldEdit. WorldEdit is required for OpenEventsMC to function properly");
            getPluginLoader().disablePlugin(this);
        }
        if(checkForMissingDependency("WorldGuard")){
            writeToConsole("&cERROR, Missing WorldGuard. WorldGuard is required for OpenEventsMC to function properly");
            getPluginLoader().disablePlugin(this);
        }
        if(checkForMissingDependency("PlaceholderAPI")){
            writeToConsole("&cPlaceholderAPI not found, disabling PAPI integration");

        }else{
            writeToConsole("&aPlaceholderAPI found, enabling PAPI integration");
            PAPI=true;
            //TODO: add PAPI hook
        }
        EventManager.loadEventsFromConfig();
        getCommand("events").setExecutor(new EventsCommand());
        new PlayerListener(this);
    }

    //Returns true if the the dependency is missing
    private boolean checkForMissingDependency(String dependencyname) {
        if(Bukkit.getPluginManager().getPlugin(dependencyname)==null){
            //Could not find the dependency
            return true;
        }
        return false;
    }
}
