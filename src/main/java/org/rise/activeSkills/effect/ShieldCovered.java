package org.rise.activeSkills.effect;

import org.rise.activeSkills.ActiveType;

public class ShieldCovered extends ShieldBase {
    public ShieldCovered(int l, double mh, double rs, double ar, double c) {
        type = ActiveType.SHIELD_COVERED;
        cd = c;
        lev = l;
        maxHealth = mh;
        armor = ar;
        recoverSpeed = rs;
        cdModifier = 0.05;
        levelModifier = 0.1;
        shieldName = "全身盾";
    }

}
