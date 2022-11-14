package org.rise.refit;

import org.rise.State.AttrModifier;

public class RefitSlotBase {
    public AttrModifier.Attr type;
    public double min;
    public double max;
    public boolean ifPercent;

    public RefitSlotBase(String s) {
        String s1, s2;
        s1 = s.substring(0, s.indexOf('-'));
        s2 = s.substring(s.indexOf('-') + 1);
        type = AttrModifier.Attr.valueOf(s1);
        s1 = s2.substring(0, s2.indexOf('-'));
        s2 = s2.substring(s2.indexOf('-') + 1);
        min = Double.parseDouble(s1);
        s1 = s2.substring(0, s2.indexOf('-'));
        s2 = s2.substring(s2.indexOf('-') + 1);
        max = Double.parseDouble(s1);
        ifPercent = Boolean.parseBoolean(s2);
    }
}
