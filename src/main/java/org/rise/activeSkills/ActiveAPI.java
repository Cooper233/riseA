package org.rise.activeSkills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rise.Inventory.ModuleGui;
import org.rise.activeSkills.effect.*;
import org.rise.riseA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveAPI {
    public static Map<ActiveType, Map<Integer, ActiveBase>> activeMap = new HashMap<>();
    public static Map<String, ActiveType> activeSymbol = new HashMap<>();
    public static Map<String, String> activeSound = new HashMap<>();

    public static void init() {
        activeMap = new HashMap<>();

        Map<Integer, ActiveBase> m1 = new HashMap<>();
        m1.put(1, new PulseScan(1, 10, 20, 5, 15, 7, 25));
        m1.put(2, new PulseScan(2, 15, 25, 15, 20, 10, 25));
        m1.put(3, new PulseScan(3, 20, 30, 20, 30, 12, 25));
        m1.put(4, new PulseScan(4, 25, 35, 25, 35, 14, 25));
        m1.put(5, new PulseScan(5, 30, 40, 35, 35, 15, 25));
        activeMap.put(ActiveType.PULSE_SCAN, m1);

        Map<Integer, ActiveBase> m2 = new HashMap<>();
        m2.put(1, new PlatformRecover(1, 7, 150, 3, 30, 40));
        m2.put(2, new PlatformRecover(2, 7, 500, 6, 30, 40));
        m2.put(3, new PlatformRecover(3, 7, 1100, 10, 30, 40));
        m2.put(4, new PlatformRecover(4, 7, 2000, 15, 30, 40));
        m2.put(5, new PlatformRecover(5, 7, 2600, 20, 30, 40));
        activeMap.put(ActiveType.PLATFORM_RECOVER, m2);

        Map<Integer, ActiveBase> m3 = new HashMap<>();
        m3.put(1, new BfbRecover(1, 5, 20, 30));
        m3.put(2, new BfbRecover(2, 5, 40, 30));
        m3.put(3, new BfbRecover(3, 5, 80, 30));
        m3.put(4, new BfbRecover(4, 5, 100, 30));
        m3.put(5, new BfbRecover(5, 5, 150, 30));
        activeMap.put(ActiveType.BFB_RECOVER, m3);

        Map<Integer, ActiveBase> m4 = new HashMap<>();
        m4.put(1, new BfbFlash(1, 3, 1.5, 30));
        m4.put(2, new BfbFlash(2, 3, 3, 30));
        m4.put(3, new BfbFlash(3, 3, 4, 30));
        m4.put(4, new BfbFlash(4, 3, 5, 30));
        m4.put(5, new BfbFlash(5, 3, 7, 30));
        activeMap.put(ActiveType.BFB_FLASH, m4);

        Map<Integer, ActiveBase> m5 = new HashMap<>();
        m5.put(1, new PulseEMP(1, 5, 1.5, 100, 40));
        m5.put(2, new PulseEMP(2, 7, 3, 300, 40));
        m5.put(3, new PulseEMP(3, 10, 4, 600, 40));
        m5.put(4, new PulseEMP(4, 15, 6, 1000, 40));
        m5.put(5, new PulseEMP(5, 15, 7, 2000, 40));
        activeMap.put(ActiveType.PULSE_EMP, m5);

        Map<Integer, ActiveBase> m6 = new HashMap<>();
        m6.put(1, new ShieldCovered(1, 100, 5, 1, 30));
        m6.put(2, new ShieldCovered(2, 200, 5, 2, 30));
        m6.put(3, new ShieldCovered(3, 400, 8, 4, 30));
        m6.put(4, new ShieldCovered(4, 700, 10, 4, 30));
        m6.put(5, new ShieldCovered(5, 1000, 10, 4, 30));
        activeMap.put(ActiveType.SHIELD_COVERED, m6);

        Map<Integer, ActiveBase> m7 = new HashMap<>();
        m7.put(1, new ShieldCrusaders(1, 60, 3, 1, 30));
        m7.put(2, new ShieldCrusaders(2, 100, 4, 2, 30));
        m7.put(3, new ShieldCrusaders(3, 200, 6, 2, 30));
        m7.put(4, new ShieldCrusaders(4, 300, 8, 2, 30));
        m7.put(5, new ShieldCrusaders(5, 400, 8, 2, 30));
        activeMap.put(ActiveType.SHIELD_CRUSADERS, m7);

        Map<Integer, ActiveBase> m8 = new HashMap<>();
        m8.put(1, new PlatformRevive(1, 7, 150, 2, 30, 45));
        m8.put(2, new PlatformRevive(2, 8, 400, 6, 30, 45));
        m8.put(3, new PlatformRevive(3, 9, 600, 8, 30, 45));
        m8.put(4, new PlatformRevive(4, 10, 800, 10, 30, 45));
        m8.put(5, new PlatformRevive(5, 10, 1200, 10, 30, 45));
        activeMap.put(ActiveType.SHIELD_CRUSADERS, m8);

        activeSymbol.put("扫描脉冲", ActiveType.PULSE_SCAN);
        activeSymbol.put("支援平台-恢复", ActiveType.PLATFORM_RECOVER);
        activeSymbol.put("支援平台-复活", ActiveType.PLATFORM_REVIVE);
        activeSymbol.put("粘弹-治疗弹", ActiveType.BFB_RECOVER);
        activeSymbol.put("粘弹-闪光弹", ActiveType.BFB_FLASH);
        activeSymbol.put("脉冲-EMP", ActiveType.PULSE_EMP);
        activeSymbol.put("力场盾-全身盾", ActiveType.SHIELD_COVERED);
        activeSymbol.put("力场盾-十字军", ActiveType.SHIELD_CRUSADERS);
    }

    public static ActiveBase getActiveSkill(Player player, int slot) {
        Inventory inv = ModuleGui.guiList.get(player.getUniqueId());
        ItemStack item;
        Player tp = Bukkit.getPlayer("Tech635");
        if (slot == 1) item = inv.getItem(15);
        else item = inv.getItem(24);
        return getActiveSkill(item);
    }

    public static ActiveBase getActiveSkill(ItemStack item) {
        if (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore()) {
            ActiveType type = null;
            ItemMeta meta = item.getItemMeta();
            for (String i : activeSymbol.keySet()) {
                if (meta.getDisplayName().contains(i)) {
                    type = activeSymbol.get(i);
                    break;
                }
            }
            if (type == null) return null;
//            tp.sendMessage(type.name());
            List<String> lore = meta.getLore();
            int level = 0;
            for (String s : lore) {
                if (s.contains(riseA.skillBaseLevelS)) {
                    if (s.contains("I")) level = 1;
                    if (s.contains("II")) level = 2;
                    if (s.contains("III")) level = 3;
                    if (s.contains("IV")) level = 4;
                    if (s.contains("V")) level = 5;
                    break;
                }
            }
            if (level == 0) return null;
            return activeMap.get(type).get(level);
        }
        return null;
    }

    public static String getActiveSound(ActiveType type) {
        return activeSound.get(type.name());
    }

    public static String getActiveSound(String type) {
        return activeSound.get(type);
    }
}
