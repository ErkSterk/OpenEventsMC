package me.erksterk.openeventsmc.libraries.clicktunnel;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class GuiManager {

    public static HashMap<String,Gui> guis = new HashMap<>();

    public static Gui getGuiFromId(String gui) {
        if(guis.containsKey(gui)){
            return guis.get(gui);
        }
        return null;
    }

    public static void createGui(String name, int size, String id){
        Gui g = new Gui(name,size,id);
        guis.put(id,g);
    }


    public static Gui getGuiFromInv(Inventory inv){
        for(String s : guis.keySet()){
            Gui g = guis.get(s);
            if(g.guiInv.getTitle().equalsIgnoreCase(inv.getTitle())){
                return g;
            }
        }
        return null;
    }
}
