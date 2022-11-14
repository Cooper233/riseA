package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.skill.TargetBase;

public class EffectRecover extends EffectBase {
    public boolean isPercent;
    public double val;
    public double val_increase;

    public EffectRecover(ConfigurationSection config) {
        type = Type.RECOVER;
        String a = config.getString("id");
        isPercent = config.getBoolean("isPercent");
        val = config.getDouble("val");
        val_increase = config.getDouble("val-increase", 0);
        target = new TargetBase(config);

    }

    public EffectRecover(boolean ip, double v, TargetBase tar) {
        type = Type.RECOVER;
        isPercent = ip;
        val = v;
        target = tar;
    }
}
