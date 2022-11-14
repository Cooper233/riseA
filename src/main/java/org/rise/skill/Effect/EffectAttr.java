package org.rise.skill.Effect;

import org.bukkit.configuration.ConfigurationSection;
import org.rise.State.AttrModifier;
import org.rise.skill.TargetBase;

public class EffectAttr extends EffectBase {
    public AttrModifier.Attr id;
    public double duration;
    public double duration_increase;
    public double val;
    public AttrModifier.ModType modType;
    public double val_increase;
    public boolean aggressive;

    public EffectAttr(ConfigurationSection config) {
        type = Type.ATTR;
        String a = config.getString("id");
        id = AttrModifier.Attr.valueOf(a);
        duration = config.getDouble("duration");
        duration_increase = config.getDouble("duration-increase", 0);
        val = config.getDouble("val");
        modType = AttrModifier.ModType.valueOf(config.getString("val-type"));
        val_increase = config.getDouble("val-increase", 0);
        aggressive = config.getBoolean("aggressive");
        target = new TargetBase(config);

    }

    public EffectAttr(AttrModifier.Attr ty, double d, double v, AttrModifier.ModType mT, boolean a, TargetBase tar) {
        type = Type.ATTR;
        id = ty;
        duration = d;
        val = v;
        modType = mT;
        aggressive = a;
        target = tar;
    }
}
