package org.rise.State;

import org.bukkit.entity.Entity;
import org.rise.Runnable.AddExHpRunnable;

import java.util.List;

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

    public static void overdueCheck(List<ExtraHp> exhp) {
        while (!exhp.isEmpty() && exhp.get(0).disappear <= System.currentTimeMillis()) {
            exhp.remove(0);
        }
    }

    public static double getTotalExhp(List<ExtraHp> exhp) {
        double res = 0;
        for (ExtraHp i : exhp) res += i.left;
        return res;
    }

    public static void addExHp(List<ExtraHp> exhp, ExtraHp ex, Entity tar) {
        Thread thread = new Thread(new AddExHpRunnable(exhp, ex, tar));
        thread.start();
    }

    public static double resistDamage(List<ExtraHp> exhp, double damage)//返回伤害的剩余值
    {
        while (!exhp.isEmpty()) {
            ExtraHp tmp = exhp.get(0);
            if (tmp.left > damage) {
                tmp.left -= damage;
                exhp.remove(0);
                exhp.add(0, tmp);
                damage = 0;
                break;
            } else {
                damage -= tmp.left;
                exhp.remove(0);
            }
        }
        return damage;
    }
}