package org.rise.skill.Effect;

import org.rise.effect.CustomEffect;
import org.rise.skill.TargetBase;

public class EffectCustomEffect extends EffectBase {
    public CustomEffect effectType;
    public double length;
    public int level;

    public EffectCustomEffect(CustomEffect effectType, double length, int level, TargetBase tar) {
        type = Type.CUSTOMEFFECT;
        this.effectType = effectType;
        this.length = length;
        this.level = level;
        target = tar;
    }

}
