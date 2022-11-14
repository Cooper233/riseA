package org.rise.Inventory;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.rise.riseA;

public class ModuleL2 implements Listener {
    @EventHandler
    public static void process2(InventoryClickEvent event) {
        if (event.getInventory().getTitle().startsWith(riseA.moduleTitle)) {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) return;
            if (item.equals(ModuleGui.getBorder())) event.setCancelled(true);
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("§e§l///   §f设备状态   §e§l///"))
                event.setCancelled(true);
            ClickType type = event.getClick();
            if (type.isKeyboardClick() || ((type.isLeftClick() || type.isRightClick()) && type.isShiftClick())) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(riseA.moduleShift);
            }
        }
    }
}
