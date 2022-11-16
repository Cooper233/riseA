package org.rise.State;

public class ExtraHp implements Cloneable {
    public long disappear;
    public double left;

    public ExtraHp(double val, long delay)//delay以毫秒为单位
    {
        left = val;
        disappear = System.currentTimeMillis() + delay;
    }

    @Override
    public ExtraHp clone() {
        ExtraHp clone = new ExtraHp(0, 0);
        clone.disappear = disappear;
        clone.left = left;
        return clone;
    }
}