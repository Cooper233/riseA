package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.skill.TargetBase;

public class EffectSkill extends EffectBase {
    public String id;

    public EffectSkill(ConfigurationSection config) {
        type = Type.SKILL;
        id = config.getString("id");
        target = new TargetBase(config);
    }

    public EffectSkill(String i, TargetBase tar) {
        type = Type.SKILL;
        id = i;
        target = tar;
    }

}
