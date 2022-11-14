package org.rise.activeSkills.effect;

import org.rise.activeSkills.ActiveType;

public class ShieldCrusaders extends ShieldBase {
    public double maxHealth;
    public double recoverSpeed;
    public double armor;
    public double cd;

    public ShieldCrusaders(int l, double mh, double rs, double ar, double c) {
        type = ActiveType.SHIELD_CRUSADERS;
        cd = c;
        lev = l;
        maxHealth = mh;
        armor = ar;
        recoverSpeed = rs;
        cdModifier = 0.05;
        levelModifier = 0.1;
        shieldName = "十字军";
    }
}
