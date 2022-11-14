package org.rise.Listener;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rise.riseA;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PlayerPickupItemProcess implements Listener {
    @EventHandler
    public void bindingCheck(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        ItemStack item = event.getItem().getItemStack();
        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasLore()) return;
        Player player = (Player) event.getEntity();
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
                    event.getItem().setItemStack(item);
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
