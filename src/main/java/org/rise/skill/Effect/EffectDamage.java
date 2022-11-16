package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.State.Attr;
import org.rise.State.RAState;
import org.rise.skill.TargetBase;

public class EffectDamage extends EffectBase {
    public RAState state, increase;

    public EffectDamage(ConfigurationSection config) {
        type = Type.DAMAGE;
        state = new RAState();
        increase = new RAState();
        state.AllDefault();
        increase.AllDefault();
        state.setAttr(Attr.DAMAGE, config.getDouble("damage", 0));
        ;
        increase.setAttr(Attr.DAMAGE, config.getDouble("damage-increase", 0));
        state.setAttr(Attr.HIT, config.getDouble("hit", 0));
        increase.setAttr(Attr.HIT, config.getDouble("hit-increase", 0));
        state.setAttr(Attr.CRIT, config.getDouble("critChance", 0));
        increase.setAttr(Attr.CRIT, config.getDouble("critChance-increase", 0));
        state.setAttr(Attr.CRIT_RATE, config.getDouble("critRate", 0));
        increase.setAttr(Attr.CRIT_RATE, config.getDouble("critRate-increase", 0));
        state.setAttr(Attr.PHYSICAL_PIERCING, config.getDouble("physicalPiercing", 0));
        increase.setAttr(Attr.PHYSICAL_PIERCING, config.getDouble("physicalPiercing-increase", 0));
        state.setAttr(Attr.TRUE_DAMAGE, config.getDouble("trueDamage", 0));
        increase.setAttr(Attr.TRUE_DAMAGE, config.getDouble("trueDamage-increase", 0));
        state.setAttr(Attr.PERCENT_DAMAGE, config.getDouble("percentDamage", 0));
        increase.setAttr(Attr.PERCENT_DAMAGE, config.getDouble("percentDamage-increase", 0));
        target = new TargetBase(config);
    }

    public EffectDamage(double d, double h, double cc, double cr, double ab, double t, double p, TargetBase tar) {
        type = Type.DAMAGE;
        state = new RAState();
        increase = new RAState();
        state.AllDefault();
        increase.AllDefault();
        state.setAttr(Attr.DAMAGE, d);
        state.setAttr(Attr.HIT, h);
        ;
        state.setAttr(Attr.CRIT, cc);
        ;
        state.setAttr(Attr.CRIT_RATE, cr);
        state.setAttr(Attr.PHYSICAL_PIERCING, ab);
        state.setAttr(Attr.TRUE_DAMAGE, t);
        state.setAttr(Attr.PERCENT_DAMAGE, p);
        target = tar;
    }
}
