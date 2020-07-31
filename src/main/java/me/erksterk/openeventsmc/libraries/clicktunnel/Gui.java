package me.erksterk.openeventsmc.libraries.clicktunnel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Gui {

    public Inventory guiInv;
    private HashMap<Integer, GuiAction> actions = new HashMap<>();
    public String id;

    protected Gui(String name, int size, String id){
        guiInv= Bukkit.createInventory(null,size,name);
        this.id=id;
    }

    public void setItem(ItemStack it, int slot, GuiAction action){
        actions.put(slot,action);
        guiInv.setItem(slot,it);
    }

    public void doActions(Player p, int slot){
        GuiAction action = this.actions.get(slot);
        if(action!=null){
            if(action.commandsCmd.size()>0){
                for(String s : action.commandsCmd){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),s.replace("%player%",p.getName()));
                }
            }
            if(action.commandsPlayer.size()>0){
                for(String s : action.commandsPlayer){
                    Bukkit.dispatchCommand(p,s.replace("%player%",p.getName()));
                }
            }
            if(!action.openGui.equalsIgnoreCase("")){
                Gui g = GuiManager.getGuiFromId(action.openGui);
                p.openInventory(g.guiInv);
            }
        }
    }

    public GuiAction getAction(int s) {
        return actions.get(s);
    }
}
