package org.rise.activeSkills;

import lk.vexview.event.KeyBoardPressEvent;
import lk.vexview.event.MinecraftKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.rise.activeSkills.effect.ActiveBase;
import org.rise.effect.CustomEffect;
import org.rise.riseAPI;

@SuppressWarnings("ConstantConditions")
public class ActiveListener implements Listener {
    @EventHandler
    public void activeSkill(KeyBoardPressEvent event) {
        int key = event.getKey();
        Player tp = Bukkit.getPlayer("Tech635");
        boolean keyState = event.getEventKeyState();
        if (MinecraftKeys.KEY_Q.isTheKey(key) || MinecraftKeys.KEY_T.isTheKey(key)) {
            Player player = event.getPlayer();
            int slot = 1;
            if (MinecraftKeys.KEY_T.isTheKey(key)) slot = 2;
            ActiveBase active = ActiveAPI.getActiveSkill(player, slot);
            if (active == null) return;
            if (riseAPI.checkEffect(player, CustomEffect.DISTURBED) && keyState) {
                player.sendMessage("§6[§fISAAC§6]§c系统被干扰，无法使用主动技能!");
                return;
            }
            active.skillAffect(player, keyState);
        }
    }
}
