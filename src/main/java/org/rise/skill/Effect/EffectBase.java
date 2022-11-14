package org.rise.skill.Effect;

import org.rise.skill.TargetBase;

public class EffectBase {
    public enum Type {
        POTION, ATTR, REVIVE, DAMAGE, SUMMON, EXHP, RECOVER, SKILL, STACK, CUSTOMEFFECT
    }

    public Type type;
    public TargetBase target;

}
