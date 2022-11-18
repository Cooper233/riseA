package org.rise.Runnable;

import org.bukkit.entity.Entity;
import org.rise.EntityInf;
import org.rise.State.ExtraHp;
import org.rise.riseA;

import java.util.List;

public class AddExHpRunnable implements Runnable {
    List<ExtraHp> exhp;
    ExtraHp ex;
    Entity tar;

    public double getTotalExhp() {
        double res = 0;
        for (ExtraHp i : exhp) res += i.left;
        return res;
    }

    @Override
    public void run() {
        ex.left = Math.min(ex.left, riseA.extraHpMax - getTotalExhp());
        if (ex.left <= 0) return;
        boolean done = false;
        for (int i = 0; i < exhp.size(); i++) {
            if (exhp.get(i).disappear < ex.disappear) {
                done = true;
                exhp.add(i + 1, ex);
                break;
            }
        }
        if (!done) exhp.add(ex);
        EntityInf.setEntityExtraHp(tar, exhp);
    }

    public AddExHpRunnable(List<ExtraHp> exhp, ExtraHp ex, Entity entity) {
        this.ex = ex;
        this.exhp = exhp;
        this.tar = entity;
    }
}
