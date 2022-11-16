package org.rise.activeSkills.effect;

import org.bukkit.entity.Player;
import org.rise.State.RAState;
import org.rise.activeSkills.ActiveType;

import java.util.List;

public abstract class ActiveBase {

    public ActiveType type;
    public int lev;
    public double cd;
    public double cdModifier;
    public double levelModifier;

    public abstract List<String> ApplyMod(RAState state);

    public ActiveBase() {
    }

    public abstract void skillAffect(Player player, boolean keyState);

    public abstract void ticklyCheck(Player player);

    public abstract void secondlyCheck(Player player);

}
