package org.rise.refit;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.talent.TalentType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TalentRefit {
    public static Map<TalentType, List<String>> talentDescription = new HashMap<>();
    public static Map<String, List<TalentType>> equipmentSlot = new HashMap<>();
    public static String talentMark;
    public static String prefix;

    public static void init(ConfigurationSection config) {
        talentDescription.clear();
        equipmentSlot.clear();
        talentMark = config.getString("mark");
        prefix = config.getString("prefix");
        ConfigurationSection des = config.getConfigurationSection("description");
        for (String i : des.getKeys(false)) {
            TalentType now = TalentType.valueOf(i);
            talentDescription.put(now, des.getStringList(i));
        }
        ConfigurationSection slot = config.getConfigurationSection("slot");
        for (String i : slot.getKeys(false)) {
            List<TalentType> tmp = new LinkedList<>();
            for (String j : slot.getStringList(i)) tmp.add(TalentType.valueOf(j));
            equipmentSlot.put(i, tmp);
        }
    }
}
