package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.State.BuffStack;
import org.rise.skill.TargetBase;

public class EffectStack extends EffectBase {
    public BuffStack.StackType id;
    public int val;

    public EffectStack(ConfigurationSection config) {
        type = Type.STACK;
        id = BuffStack.StackType.valueOf(config.getString("id"));
        val = config.getInt("val");
        target = new TargetBase(config);

    }

    public EffectStack(BuffStack.StackType t, int v, TargetBase tar) {
        type = Type.STACK;
        id = t;
        val = v;
        target = tar;
    }
}
