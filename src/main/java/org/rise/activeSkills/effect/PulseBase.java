package org.rise.activeSkills.effect;

import org.bukkit.entity.Player;
import org.rise.State.RAState;

import java.util.List;

public abstract class PulseBase extends ActiveBase {
    public double range;
    public double dur;

    @Override
    public List<String> ApplyMod(RAState state) {
        return null;
    }

    public abstract void beginningEffect(Player player);

    public abstract void endingEffect(Player player);

    @Override
    public void skillAffect(Player player, boolean keyState) {
        if (keyState) {
            beginningEffect(player);
        } else {
            endingEffect(player);
        }
    }

    @Override
    public void ticklyCheck(Player player) {

    }

    @Override
    public void secondlyCheck(Player player) {

    }
}
