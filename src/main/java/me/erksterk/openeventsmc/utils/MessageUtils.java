package me.erksterk.openeventsmc.utils;

import me.erksterk.openeventsmc.config.Language;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class MessageUtils {
    public static String translateMessage(String old, HashMap<String, String> placeholders){
        String newstring = old.replace("%tag%", Language.TAG);
        for(String s : placeholders.keySet()){
            newstring=newstring.replace(s,placeholders.get(s));
        }
        return ChatColor.translateAlternateColorCodes('&',newstring);
    }
}
