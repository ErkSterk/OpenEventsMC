package me.erksterk.openeventsmc;

import me.erksterk.openeventsmc.config.Config;
import me.erksterk.openeventsmc.config.Language;
import me.erksterk.openeventsmc.events.shared.commands.EventsCommand;
import me.erksterk.openeventsmc.events.waterdrop.Waterdrop;
import me.erksterk.openeventsmc.events.waterdrop.listeners.PlayerListener;
import me.erksterk.openeventsmc.libraries.tinyphoenix.config.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Context;

public class Main extends JavaPlugin {


    public static Context context;
    public static String version;
    private static ConsoleCommandSender console;
    public static boolean PAPI=false;

    public static Waterdrop waterdrop = new Waterdrop();

    public static void writeToConsole(String message){
        console.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    @Override
    public void onEnable(){
        console= Bukkit.getConsoleSender();
        version=getDescription().getVersion();
        writeToConsole("&cStarting loading of OpenEventsMC");
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
        waterdrop.setup(this);
        getCommand("events").setExecutor(new EventsCommand());
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
