package org.rise.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rise.EntityInf;
import org.rise.State.RAstate;
import org.rise.riseA;
import org.rise.talent.TalentType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ModuleGui implements Listener {
    public static Map<UUID, Inventory> guiList = new HashMap<>();

    public static ItemStack getBorder() {
        ItemStack border = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta borderI = border.getItemMeta();
        borderI.setLore(riseA.moduleDescribe);
        borderI.setDisplayName(riseA.moduleName);
        border.setItemMeta(borderI);
        return border;
    }

    public static ItemStack getItem(Player player, int slot) {
        if (guiList.containsKey(player.getUniqueId())) {
            return guiList.get(player.getUniqueId()).getItem(slot);
        }
        return null;
    }

    public static void setItem(Player player, int slot, ItemStack itemStack) {
        Inventory inv = guiList.get(player.getUniqueId());
        inv.setItem(slot, itemStack);
        guiList.put(player.getUniqueId(), inv);
    }

    public static ItemStack getInfo(Player player) {
        RAstate state = EntityInf.playersAttr.get(player.getUniqueId());
//        state=state.applyModifier(player);
        ItemStack info = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
        ItemMeta i = info.getItemMeta();
        i.setDisplayName("§e§l///   §f设备状态   §e§l///");
        List<String> lore = new LinkedList<>();
        lore.add("§6§l|||§7§l| §f作战效能： §e" + state.getEffectiveness());
        lore.add("§b>> §f额外生命值: §e" + state.getTotalExHp());
        lore.add("");
        lore.add("§6§l|||§7§l| §f已启用的天赋:");
        for (TalentType j : state.activeTalent) {
            lore.add("§6[§f§l" + riseA.talentMapReflect.get(j) + "§6]");
        }
        i.setLore(lore);
        info.setItemMeta(i);
        info.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        return info;
    }

    public static void guiInit(Player player, boolean gui) {
        Inventory inventory = Bukkit.createInventory(player, 36, riseA.moduleTitle + "-" + player.getName());
        ItemStack[] items = new ItemStack[36];

        try {
            File loc = new File(riseA.modFolder, player.getName() + ".yml");
            if (!loc.exists()) {
                loc.createNewFile();
            }
            ConfigurationSection file = YamlConfiguration.loadConfiguration(loc);
            if (file.contains("S1")) {
                items[11] = file.getItemStack("S1", new ItemStack(Material.AIR));
            }
            if (file.contains("S2")) {
                items[12] = file.getItemStack("S2", new ItemStack(Material.AIR));
            }
            if (file.contains("S3")) {
                items[13] = file.getItemStack("S3", new ItemStack(Material.AIR));
            }
            if (file.contains("Sk1")) {
                items[15] = file.getItemStack("Sk1", new ItemStack(Material.AIR));
            }
            if (file.contains("Sk2")) {
                items[24] = file.getItemStack("Sk2", new ItemStack(Material.AIR));
            }
            for (int i = 1; i <= 6; i++) {
                items[26 + i] = file.getItemStack("Eq" + i, new ItemStack(Material.AIR));
            }
        } catch (Throwable e) {
            player.sendMessage(riseA.modFolder.toString());
        }
        inventory.setContents(items);
        guiList.put(player.getUniqueId(), inventory);
    }

    public static void saveGui(Player player, Inventory inv) {
        ItemStack s1 = inv.getItem(11), s2 = inv.getItem(12), s3 = inv.getItem(13), sk1 = inv.getItem(15), sk2 = inv.getItem(24);
        ItemStack[] eq = new ItemStack[7];
        for (int i = 1; i <= 6; i++) {
            eq[i] = inv.getItem(26 + i);
        }
        File loc = new File(riseA.modFolder, player.getName() + ".yml");
        if (!loc.exists()) {
            try {
                loc.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration file = YamlConfiguration.loadConfiguration(loc);
        if (s1 != null && s1.getType() != Material.AIR) {
            file.set("S1", s1);
        } else file.set("S1", null);
        if (s2 != null && s2.getType() != Material.AIR) {
            file.set("S2", s2);
        } else file.set("S2", null);
        if (s3 != null && s3.getType() != Material.AIR) {
            file.set("S3", s3);
        } else file.set("S3", null);
        if (sk1 != null && sk1.getType() != Material.AIR) {
            file.set("Sk1", sk1);
        } else file.set("Sk1", null);
        if (sk2 != null && sk2.getType() != Material.AIR) {
            file.set("Sk2", sk2);
        } else file.set("Sk2", null);
        for (int i = 1; i <= 6; i++) {
            if (eq[i] != null && eq[i].getType() != Material.AIR) {
                file.set("Eq" + i, eq[i]);
            }
        }
        try {
            file.save(loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
