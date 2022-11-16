package org.rise.activeSkills.effect;

import org.rise.State.Attr;
import org.rise.State.RAState;
import org.rise.activeSkills.ActiveType;
import org.rise.skill.Effect.EffectBase;
import org.rise.skill.Effect.EffectRecover;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.NpcType;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BfbRecover extends BfbBase {
    public double recover;

    public BfbRecover(int lv, double r, double rc, double c) {
        type = ActiveType.BFB_RECOVER;
        lev = lv;
        range = r;
        recover = rc;
        cd = c;
        cdModifier = 0.04;
        levelModifier = 0.15;
        color = 5;
        explodeSound = "modularwarfare:effect.revived";
    }

    @Override
    public List<String> ApplyMod(RAState state) {
        List<String> list = new LinkedList<>();
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
        double cd = this.cd * (1.0 - state.getAttr(Attr.SKILL_LEVEL) * cdModifier) / (1.0 + state.getAttr(Attr.SKILL_ACCELERATE) / 100);
        double l = this.range * mod;
        double re = this.recover * mod * (1.0 + state.getAttr(Attr.RECOVER_EFFECT) / 100);
        list.add("§6应用加成后数值：");
        list.add("§7治疗范围         §e§l" + String.format("%.2f", l));
        list.add("§7恢复量          §e§l" + String.format("%.2f", re));
        list.add("§7冷却时间         §e§l" + String.format("%.2f", cd));
        return list;
    }

    @Override
    public SkillBase getSkill(RAState state) {
        double cd = this.cd * (1.0 - state.getAttr(Attr.SKILL_LEVEL) * cdModifier);
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
        double hp = this.recover * mod * (1.0 + state.getAttr(Attr.RECOVER_EFFECT) / 100);
        double r = this.range * mod;
        List<EffectBase> eff = new LinkedList<>();
        TargetBase target = new TargetBase(TargetBase.Type.AROUND, r, 100, Arrays.asList(NpcType.NPC_ENEMY, NpcType.OTHER, NpcType.PLAYER_ENEMY));
        eff.add(new EffectRecover(false, hp, target));
        EnableEffectBase enable = new EnableEffectBase("§f[§6ISAAC§f]医疗凝胶已发射！", null, null, explodeSound);
        SkillBase skill = new SkillBase("化学物质发射器-治疗", cd, "BFB", 0, 1, 0, "BFB", eff, enable);
        return skill;
    }
}
