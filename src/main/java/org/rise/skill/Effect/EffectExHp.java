package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.skill.TargetBase;

public class EffectExHp extends EffectBase {
    public double duration;
    public double duration_increase;
    public double val;
    public double val_increase;

    public EffectExHp(ConfigurationSection config) {
        type = Type.EXHP;
        duration = config.getDouble("duration");
        duration_increase = config.getDouble("duration-increase", 0);
        val = config.getDouble("val");
        val_increase = config.getDouble("val-increase", 0);
        target = new TargetBase(config);
    }

    public EffectExHp(double d, double v, TargetBase tar) {
        type = Type.EXHP;
        duration = d;
        val = v;
        target = tar;
    }
}
