package org.rise.refit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.rise.State.AttrModifier;
import org.rise.extra.Pair;
import org.rise.riseA;
import org.rise.talent.TalentType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CalibrationData {
    public static Map<UUID, Map<String, Map<String, Map<AttrModifier.Attr, Double>>>> playerAttrData = new HashMap<>();//玩家-改装类型-改装槽位-属性-数值
    public static Map<UUID, Map<String, List<TalentType>>> playerTalentData = new HashMap<>();//玩家-改装类型-记录的天赋

    public static class calibraConsume {
        public String id;
        public int num;

        public calibraConsume(String id, int num) {
            this.id = id;
            this.num = num;
        }
    }

    ;
    public static Map<String, List<List<calibraConsume>>> consume = new HashMap<>();

    public static void performCalibration(Player player, CalibrationItem item, String slot, AttrModifier.Attr now, AttrModifier.Attr attr) {
        PlayerInventory inv = player.getInventory();
        inv.removeItem(item.self);
        List<Pair<AttrModifier.Attr, Double>> data = item.data.get(slot);
        List<Integer> pos = item.dataPos.get(slot);
        int fp = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getKey() == now) {
                fp = pos.get(i);
            }
        }
        List<String> lore = item.self.getItemMeta().getLore();
        List<String> nl = new LinkedList<>(lore.subList(0, fp));
        RefitBase tmp = riseA.refitBaseMap.get(slot);
        String res = tmp.prefix + "§l❯§f";
        double val = getData(player.getUniqueId(), item.type, slot, attr);
        res = res + riseA.attrName.get(attr) + " " + tmp.prefix + "§l" + String.format("%.2f", val);
        RefitSlotBase s = tmp.getSlot(item.type, attr);
        if (s.ifPercent) res += "%";
        res = res + "  §f" + tmp.prefix;
        int t1 = (int) ((val - s.min) / (s.max - s.min) * 100);
        for (int j = 10; j <= 100; j += 10) {
            if (j <= t1) res = res + "◆";
            else res = res + "◇";
        }
        res += "  " + riseA.calibratedMarkS;
        nl.add(res);
        nl.addAll(lore.subList(fp + 1, lore.size()));
        ItemMeta meta = item.self.getItemMeta();
        meta.setLore(nl);
        item.self.setItemMeta(meta);
        inv.addItem(item.self);
    }

    public static Map<String, Map<String, Map<AttrModifier.Attr, Double>>> getTypesData(UUID uuid) {
        Map<String, Map<String, Map<AttrModifier.Attr, Double>>> res = new HashMap<>();
        if (playerAttrData.containsKey(uuid)) {
//            Bukkit.getLogger().info("1");
            res = playerAttrData.get(uuid);
        }
        return res;
    }

    public static Map<String, Map<AttrModifier.Attr, Double>> getSlotsData(UUID uuid, String type) {
        Map<String, Map<AttrModifier.Attr, Double>> res = new HashMap<>();
        Map<String, Map<String, Map<AttrModifier.Attr, Double>>> tmp = getTypesData(uuid);
        if (tmp.containsKey(type)) {
//            Bukkit.getLogger().info("2");
            res = tmp.get(type);
        }
        return res;
    }

    public static Map<AttrModifier.Attr, Double> getAttrData(UUID uuid, String type, String slot) {
        Map<AttrModifier.Attr, Double> res = new HashMap<>();
        Map<String, Map<AttrModifier.Attr, Double>> tmp1 = getSlotsData(uuid, type);
        if (tmp1.containsKey(slot)) {
//            Bukkit.getLogger().info("3");
            res = tmp1.get(slot);
        }
        return res;
    }

    public static double getData(UUID uuid, String type, String slot, AttrModifier.Attr attr) {
        Map<AttrModifier.Attr, Double> attrData = getAttrData(uuid, type, slot);
        if (attrData.containsKey(attr)) {
//            Bukkit.getLogger().info("4");
            return attrData.get(attr);
        }
        return 0;
    }

    public static List<calibraConsume> getConsume(String type, int lev) {
        return consume.get(type).get(lev);
    }

    public static void saveAttrData(UUID uuid, String type, String slot, AttrModifier.Attr tar, double newData) {
        Map<String, Map<String, Map<AttrModifier.Attr, Double>>> data = getTypesData(uuid);
        Map<String, Map<AttrModifier.Attr, Double>> slotsData = getSlotsData(uuid, type);
        Map<AttrModifier.Attr, Double> slotData = getAttrData(uuid, type, slot);
        slotData.put(tar, newData);
        slotsData.put(slot, slotData);
        data.put(type, slotsData);
        playerAttrData.put(uuid, data);
    }

    public static void saveToFile(Player player) {
        File folder = riseA.calibrateFolder;
        File file = new File(folder, player.getName() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("UUID", player.getUniqueId().toString());
        ConfigurationSection data = config.createSection("data");
        for (String i : RefitBase.refitType) {
            ConfigurationSection sec1 = data.createSection(i);
            for (String j : riseA.refitBaseMap.keySet()) {
                RefitBase base = riseA.refitBaseMap.get(j);
                List<RefitSlotBase> slot = base.refits.get(i);
                if (slot == null) continue;
                ConfigurationSection sec2 = sec1.createSection(j);
                for (RefitSlotBase s : slot) {
                    sec2.set(s.type.name(), getData(player.getUniqueId(), i, j, s.type));
                }
                sec1.set(j, sec2);
            }
            data.set(i, sec1);
        }
        config.set("data", data);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        File folder = riseA.calibrateFolder;
        for (File f : Objects.requireNonNull(folder.listFiles())) {
            ConfigurationSection config = YamlConfiguration.loadConfiguration(f);
            UUID uuid = UUID.fromString(config.getString("UUID"));
            ConfigurationSection data = config.getConfigurationSection("data");
            for (String i : RefitBase.refitType) {
                ConfigurationSection sec1 = data.getConfigurationSection(i);
                for (String j : riseA.refitBaseMap.keySet()) {
                    RefitBase base = riseA.refitBaseMap.get(j);
                    List<RefitSlotBase> slot = base.refits.get(i);
                    if (slot == null) continue;
                    ConfigurationSection sec2 = sec1.getConfigurationSection(j);
                    for (RefitSlotBase s : slot) {
                        double val = sec2.getDouble(s.type.name());
                        saveAttrData(uuid, i, j, s.type, val);
                    }
                }
            }
        }
        folder = riseA.folder;
        File f = new File(folder, "refit.yml");
        ConfigurationSection config = YamlConfiguration.loadConfiguration(f);
        config = config.getConfigurationSection("calibration");
        for (String s : config.getKeys(false)) {
            List<List<calibraConsume>> list = new LinkedList<>();
            list.add(null);
            for (int i = 1; i <= 5; i++) {
                List<calibraConsume> tmp = new LinkedList<>();
                ConfigurationSection lev = config.getConfigurationSection(s).getConfigurationSection(String.valueOf(i));
                for (String c : lev.getKeys(false)) {
                    tmp.add(new calibraConsume(c, lev.getInt(c)));
                }
                list.add(tmp);
            }
            consume.put(s, list);
        }
    }
}
