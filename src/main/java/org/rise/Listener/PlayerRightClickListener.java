package org.rise.Listener;

import lk.vexview.event.KeyBoardPressEvent;
import lk.vexview.event.MinecraftKeys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.rise.GUI.testgui;
import org.rise.refit.RefitBase;
import org.rise.riseA;
import org.rise.skill.SkillAPI;

import java.util.List;

public class PlayerRightClickListener implements Listener {
    @EventHandler
    public void process(KeyBoardPressEvent event) {
        int key = event.getKey();
        if (!MinecraftKeys.KEY_X.isTheKey(key)) return;
        Player player = event.getPlayer();
        ItemStack s = player.getEquipment().getItemInMainHand();
        boolean f = RefitBase.performRefit(s);
        if (f) {
            event.getPlayer().sendMessage("§f[§6ISAAC§f]装备 " + s.getItemMeta().getDisplayName() + " §b已认证§f！");
            event.getPlayer().sendMessage("§f[§6ISAAC§f]§6改装模块§f已部署!");
            player.playSound(player.getLocation(), riseA.refitSound, 1, 1);
        }
        event.getPlayer().getEquipment().setItemInMainHand(s);
    }

    @EventHandler
    public void skill_process(KeyBoardPressEvent event) {
        int key = event.getKey();
        if (!MinecraftKeys.KEY_G.isTheKey(key) || !event.getEventKeyState()) return;
        Player player = event.getPlayer();
        ItemStack s = player.getEquipment().getItemInMainHand();
        if (s.hasItemMeta() && s.getItemMeta().hasLore()) {
            List<String> lore = s.getItemMeta().getLore();
            for (String i : lore) {
                for (String j : riseA.pressSkillMap.keySet()) {
                    if (i.contains(j)) {
                        SkillAPI.performSkill(event.getPlayer(), riseA.getSkill(riseA.pressSkillMap.get(j)), false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void openTestMenu(KeyBoardPressEvent event) {
        int key = event.getKey();
        if (!MinecraftKeys.KEY_I.isTheKey(key) || !event.getEventKeyState()) return;
        Player player = event.getPlayer();
        testgui.test(player);
    }
}
