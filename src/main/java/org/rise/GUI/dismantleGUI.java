package org.rise.GUI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class dismantleGUI implements Listener {
    public static Inventory createDismantleGui(Player player) {
        Inventory inv = Bukkit.createInventory(player, 36, "§6[§fISAAC§6]§f装备拆解系统");
        return inv;
    }

    @EventHandler
    public void dismantleEvent(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (Objects.equals(inv.getTitle(), "§6[§fISAAC§6]§f装备拆解系统")) {

        }
    }
}
