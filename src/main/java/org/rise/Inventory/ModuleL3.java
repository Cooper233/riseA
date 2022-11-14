package org.rise.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.rise.activeSkills.ActiveAPI;
import org.rise.activeSkills.ConstantEffect;
import org.rise.activeSkills.effect.ActiveBase;
import org.rise.riseA;
import org.rise.riseAPI;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;

import java.io.IOException;
import java.util.List;

public class ModuleL3 implements Listener {
    @EventHandler
    public static void process3(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getInventory().getTitle().startsWith(riseA.moduleTitle)) {
            HumanEntity player = event.getViewers().get(0);
            riseAPI.resetPlayerAttr((Player) player);
            Inventory a = event.getInventory();
            a.setItem(8, ModuleGui.getInfo((Player) player));
            if (event.getRawSlot() >= 27) return;
            InventoryAction action = event.getAction();
            if (action.equals(InventoryAction.PLACE_ALL) || action.equals(InventoryAction.PLACE_ONE) || action.equals(InventoryAction.PLACE_SOME) || action.equals(InventoryAction.SWAP_WITH_CURSOR)) {
                ItemStack item = event.getCursor();
                if (!item.hasItemMeta()) {
                    event.setCancelled(true);
                    player.sendMessage(riseA.moduleError);
                    return;
                }
                if (!item.getItemMeta().hasDisplayName()) {
                    event.setCancelled(true);
                    player.sendMessage(riseA.moduleError);
                    return;
                }
                switch (event.getSlot()) {
                    case 11: {
                        if (!item.getItemMeta().getDisplayName().contains(riseA.module1S)) {
                            event.setCancelled(true);
                            player.sendMessage(riseA.moduleError + "本槽位应为:" + riseA.module1S);
                        }
                        break;
                    }
                    case 12: {
                        if (!item.getItemMeta().getDisplayName().contains(riseA.module2S)) {
                            event.setCancelled(true);
                            player.sendMessage(riseA.moduleError + "本槽位应为:" + riseA.module2S);
                        }
                        break;
                    }
                    case 13: {
                        if (!item.getItemMeta().getDisplayName().contains(riseA.module3S)) {
                            event.setCancelled(true);
                            player.sendMessage(riseA.moduleError + "本槽位应为:" + riseA.module3S);
                        }
                        break;
                    }
                    case 15:
                    case 24: {
                        if (!item.getItemMeta().getDisplayName().contains(riseA.moduleSkillS)) {
                            event.setCancelled(true);
                            player.sendMessage(riseA.moduleError + "本槽位应为:" + riseA.moduleSkillS);
                        }
                        break;
                    }
                    default: {
                        event.setCancelled(true);
                        player.sendMessage(riseA.moduleError);
                    }
                }
            }

        }
    }

    @EventHandler
    public void invClose(InventoryCloseEvent event) throws IOException {
        if (event.getInventory().getTitle().startsWith(riseA.moduleTitle)) {
            Player player = (Player) event.getPlayer();
            ModuleGui.saveGui(player, event.getInventory());
        }
    }

    @EventHandler
    public void preventActiveChange(InventoryClickEvent event) {
        Player tp = Bukkit.getPlayer("Tech635");
        if (event.getInventory().getTitle().startsWith(riseA.moduleTitle)) {
            Player player = (Player) event.getViewers().get(0);
            riseAPI.resetPlayerAttr((Player) player);
            if (event.getRawSlot() != 15 && event.getRawSlot() != 24) return;
            ActiveBase now = ActiveAPI.getActiveSkill(event.getCurrentItem());
            if (now == null) return;
            String name = now.type.name();
            SkillBase skill = new SkillBase("技能冷却检测", 0, name.substring(0, name.indexOf('_')), 0, 1, 0, name.substring(0, name.indexOf('_')), null, null);
            boolean sec = SkillAPI.performSkill(player, skill, false);
            List<ActiveBase> list = ConstantEffect.constant.get(player.getUniqueId());
            if (list != null && list.contains(now)) sec = false;
            if (!sec) {
                event.setCancelled(true);
                player.sendMessage("§f[§6ISSAC§f]§c该技能冷却中，暂时无法更换！");
            }
        }
    }
}
