package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.State.RAstate;
import org.rise.skill.TargetBase;

public class EffectDamage extends EffectBase {
    public RAstate state, increase;

    public EffectDamage(ConfigurationSection config) {
        type = Type.DAMAGE;
        state = new RAstate();
        increase = new RAstate();
        state.AllDefault();
        increase.AllDefault();
        state.damage = config.getDouble("damage", 0);
        increase.damage = config.getDouble("damage-increase", 0);
        state.hit = config.getDouble("hit", 0);
        increase.hit = config.getDouble("hit-increase", 0);
        state.critChance = config.getDouble("critChance", 0);
        increase.critChance = config.getDouble("critChance-increase", 0);
        state.critRate = config.getDouble("critRate", 0);
        increase.critRate = config.getDouble("critRate-increase", 0);
        state.physicalPiercing = config.getDouble("physicalPiercing", 0);
        increase.physicalPiercing = config.getDouble("physicalPiercing-increase", 0);
        state.trueDamage = config.getDouble("trueDamage", 0);
        increase.trueDamage = config.getDouble("trueDamage-increase", 0);
        state.percentDamage = config.getDouble("percentDamage", 0);
        increase.percentDamage = config.getDouble("percentDamage-increase", 0);
        target = new TargetBase(config);
    }

    public EffectDamage(double d, double h, double cc, double cr, double ab, double t, double p, TargetBase tar) {
        type = Type.DAMAGE;
        state = new RAstate();
        increase = new RAstate();
        state.AllDefault();
        increase.AllDefault();
        state.damage = d;
        state.hit = h;
        state.critChance = cc;
        state.critRate = cr;
        state.physicalPiercing = ab;
        state.trueDamage = t;
        state.percentDamage = p;
        target = tar;
    }
}
