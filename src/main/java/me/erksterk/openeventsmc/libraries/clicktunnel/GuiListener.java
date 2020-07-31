package me.erksterk.openeventsmc.libraries.clicktunnel;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Gui g = GuiManager.getGuiFromInv(inv);
        if (g != null) {
            Player p = (Player) e.getWhoClicked();
            int s = e.getRawSlot();
            if (g.getAction(s) != null) {
                g.doActions(p, s);
                if (g.getAction(s).itemLock) {
                    e.setCancelled(true);
                }
                if (g.getAction(s).closeGui) {
                    p.closeInventory();
                }
            }

        }
    }
}
