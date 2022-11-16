package org.rise.State;

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

}

