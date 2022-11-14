package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;
import org.rise.skill.TargetBase;

public class EffectPotion extends EffectBase {
    public PotionEffectType id;
    public double duration;
    public double duration_increase;
    public int[] level = new int[10];
    public Boolean aggressive;

    public EffectPotion(ConfigurationSection config) {
        type = Type.POTION;
        String t1 = config.getString("id");
        try {
            int i1 = Integer.parseInt(t1);
            id = PotionEffectType.getById(i1);
        } catch (NumberFormatException e) {
            id = PotionEffectType.getByName(t1);
        }
        duration = config.getDouble("duration");
        duration_increase = config.getDouble("duration-increase,0");
        level[0] = config.getInt("level");
        for (int i = 1; i < 10; i++) {
            if (config.contains("level-lv" + i)) {
                level[i] = config.getInt("level-lv" + i);
            } else level[i] = level[i - 1];
        }
        aggressive = config.getBoolean("aggressive", false);
        target = new TargetBase(config);
    }

    public EffectPotion(PotionEffectType ty, double d, int[] l, Boolean a, TargetBase tar) {
        type = Type.POTION;
        id = ty;
        duration = d;
        level = l;
        aggressive = a;
        target = tar;
    }
}
