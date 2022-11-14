package org.rise.State;

public class AttrModifier {
    public enum Attr {
        CRIT, CRIT_RATE, HEADSHOT_RATE, NON_HEADSHOT, DAMAGE, FINAL_DAMAGE, TRUE_DAMAGE, HP, HP_REGEN, PERCENT_HP, PHYSICAL_RESISTANCE, SPECIAL_RESISTANCE, PHYSICAL_PIERCING, SPEED, PERCENT_DAMAGE, HIT, AVOID, EXP_BOUNCE, ON_KILL_REGEN, NF_ABILITY, DEBUFF_RESISTANCE, DAMAGE_RECEIVE, SKILL_LEVEL, SKILL_DAMAGE, DEBUFF_EFFECT, RECOVER_EFFECT, SKILL_ACCELERATE, PULSE_RESISTANCE
    }

    public enum ModType {
        PLUS, MULTIPLY
    }

    public double val;
    public ModType type;
    public Attr tar;
    public long disappear;

    public AttrModifier(double val, ModType type, Attr tar, long length) {
        this.val = val;
        this.type = type;
        this.tar = tar;
        this.disappear = System.currentTimeMillis() + length;
    }

}

