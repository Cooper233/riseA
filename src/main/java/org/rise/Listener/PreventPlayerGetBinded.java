package org.rise.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rise.riseA;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PreventPlayerGetBinded implements Listener {
    @EventHandler
    public void process(InventoryClickEvent event) {
        ItemStack item = event.getCursor();
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasLore()) return;
        Player player = (Player) event.getWhoClicked();
        if (player.isOp()) return;
        for (String s : item.getItemMeta().getLore()) {
            if (s.contains(riseA.bindingS)) {
                String name = s.replaceAll(riseA.bindingS, "");
                if (name.equals("#NULL")) {
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lore = new LinkedList<>();
                    for (String ss : item.getItemMeta().getLore()) {
                        if (ss.contains(riseA.bindingS)) {
                            lore.add(riseA.bindingS + player.getName());
                        } else lore.add(ss);
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                    event.setCursor(item);
                    return;
                }
                if (!Objects.equals(player.getName(), name)) {
                    player.sendMessage(riseA.notBelongS);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
