package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.skill.TargetBase;

public class EffectRevive extends EffectBase {
    public EffectRevive(ConfigurationSection config) {
        type = Type.REVIVE;
        target = new TargetBase(config);
    }

    public EffectRevive(TargetBase tar) {
        type = Type.REVIVE;
        target = tar;
    }
}
