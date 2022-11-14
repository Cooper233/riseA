package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.skill.TargetBase;

public class EffectSummon extends EffectBase {
    public String id;

    public EffectSummon(ConfigurationSection config) {
        type = Type.SUMMON;
        id = config.getString("id");
        target = new TargetBase(config);

    }

    public EffectSummon(String i, TargetBase tar) {
        type = Type.SUMMON;
        id = i;
        target = tar;
    }
}
