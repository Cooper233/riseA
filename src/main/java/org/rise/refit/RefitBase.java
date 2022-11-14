package org.rise.refit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rise.State.AttrModifier;
import org.rise.riseA;
import org.rise.talent.TalentType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RefitBase {
    public static List<String> refitType = new LinkedList<>();
    public Map<String, List<RefitSlotBase>> refits = new HashMap<>();//type-改装槽可用项
    public String id;
    public String mark;
    public String prefix;

    public RefitBase(ConfigurationSection config) {
        id = config.getName();
        mark = config.getString("mark");
        prefix = config.getString("prefix");
        ConfigurationSection slot = config.getConfigurationSection("slot");
        for (String i : slot.getKeys(false)) {
            List<RefitSlotBase> tmp = new LinkedList<>();
            for (String j : slot.getStringList(i)) {
                tmp.add(new RefitSlotBase(j));
            }
            refits.put(i, tmp);
        }
    }

    public RefitSlotBase getSlot(String type, AttrModifier.Attr attr) {
        List<RefitSlotBase> list = refits.get(type);
        if (list == null) return null;
        for (RefitSlotBase i : list) {
            if (i.type == attr) return i;
        }
        return null;
    }

    private static int getrand(int n) {
        return (int) (Math.floor(Math.random() * 10000000) % n);
    }

    public static boolean performRefit(ItemStack ori) {
        Player tp = Bukkit.getPlayer("Tech635");
        List<String> used = new LinkedList<>();
        List<String> lore, aft = new LinkedList<>();
        if (ori == null) return false;
        if (ori.getType() == Material.AIR) return false;
        if (!ori.hasItemMeta()) return false;
        if (!ori.getItemMeta().hasLore()) return false;
        ItemMeta meta = ori.getItemMeta();
        boolean ifRefit = false;
        lore = meta.getLore();
        String kind = null;
        int level = 0;
        for (String s : lore) {
            if (s.contains(riseA.levelMarkS)) {
                if (s.contains("I")) level = 1;
                if (s.contains("II")) level = 2;
                if (s.contains("III")) level = 3;
                if (s.contains("IV")) level = 4;
                if (s.contains("V")) level = 5;
            } else if (s.contains(riseA.refitMarkS)) {
                kind = s.replaceAll(riseA.refitMarkS, "");
            } else if (kind != null) {
                for (String na : riseA.refitBaseMap.keySet()) {
                    RefitBase tmp = riseA.refitBaseMap.get(na);
                    if (!s.contains(tmp.mark)) continue;
                    double high = 0, low = 0;
                    switch (level) {
                        case 1: {
                            high = 5;
                            break;
                        }
                        case 2: {
                            high = 15;
                            break;
                        }
                        case 3: {
                            high = 30;
                            break;
                        }
                        case 4: {
                            low = 10;
                            high = 60;
                            break;
                        }
                        case 5: {
                            low = 40;
                            high = 100;
                            break;
                        }
                    }
                    double t = getrand((int) (high + 1 - low)) + low;
                    double t1 = t;
                    t = t / 100.0;
                    List<RefitSlotBase> tt = tmp.refits.get(kind);
                    int x = getrand(tt.size());
                    while (used.contains(tmp.mark + x)) x = getrand(tt.size());
                    RefitSlotBase slot = tt.get(x);
                    t = (slot.max - slot.min) * t + slot.min;
                    String res = tmp.prefix + "§l❯§f";
                    double rs = Math.floor(t * 100) / 100;
                    res = res + riseA.attrName.get(slot.type) + " " + tmp.prefix + "§l" + rs;
                    if (slot.ifPercent) res += "%";
                    res = res + "  §f" + tmp.prefix;
                    for (int j = 10; j <= 100; j += 10) {
                        if (j <= t1) res = res + "◆";
                        else res = res + "◇";
                    }
                    s = res;
                    used.add(tmp.mark + x);
                    ifRefit = true;
                }
                if (s.contains(TalentRefit.talentMark)) {
                    s = "§t§e§f";
                    aft.add(TalentRefit.prefix);
                    TalentType type = TalentRefit.equipmentSlot.get(kind).get(getrand(TalentRefit.equipmentSlot.get(kind).size()));
                    aft.add("§6[§f§l" + riseA.talentMapReflect.get(type) + "§6]");
                    aft.addAll(TalentRefit.talentDescription.get(type));
                }
            }
            aft.add(s);

        }
        meta.setLore(aft);
        ori.setItemMeta(meta);
        return ifRefit;
    }
}
