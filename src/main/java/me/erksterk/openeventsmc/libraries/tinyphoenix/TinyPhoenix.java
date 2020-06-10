package me.erksterk.openeventsmc.libraries.tinyphoenix;

import me.erksterk.openeventsmc.libraries.tinyphoenix.mysql.Connections;
import org.bukkit.Bukkit;

import java.util.List;

public class TinyPhoenix {
    public static void kill(){
        List<String> list = Connections.getAllConnectionNames();
        for(String s:list){
            Bukkit.getConsoleSender().sendMessage("Disconnecting from: "+s);
            Connections.killConnection(s);
        }
    }
}
