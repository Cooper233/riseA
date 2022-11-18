package org.rise.State;

import java.util.List;

public class AttrModifier {
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

    public static void overdueCheck(List<AttrModifier> attrMod) {
        while (!attrMod.isEmpty() && attrMod.get(0).disappear <= System.currentTimeMillis()) {
            attrMod.remove(0);
        }
    }

    public static void addAttrModifier(List<AttrModifier> attrMod, AttrModifier val) {
        int l = 0, r = attrMod.size() - 1, ans = -1;
        while (l <= r) {
            int mid = ((l + r) >> 1);
            if (attrMod.get(mid).disappear < val.disappear) {
                ans = mid;
                l = mid + 1;
            } else r = mid - 1;
        }
        attrMod.add(ans + 1, val);
    }
}

