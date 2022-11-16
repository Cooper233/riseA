package org.rise.activeSkills.effect;

import org.bukkit.potion.PotionEffectType;
import org.rise.State.Attr;
import org.rise.State.RAState;
import org.rise.activeSkills.ActiveType;
import org.rise.skill.Effect.EffectBase;
import org.rise.skill.Effect.EffectPotion;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.NpcType;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BfbFlash extends BfbBase {
    public BfbFlash(int lv, double r, double d, double c) {
        type = ActiveType.BFB_FLASH;
        lev = lv;
        range = r;
        duration = d;
        cd = c;
        cdModifier = 0.05;
        levelModifier = 0.1;
        color = 0;
        explodeSound = "modularwarfare:effect.bfb_flash";
    }

    @Override
    public SkillBase getSkill(RAState state) {
        double cd = this.cd * (1.0 - state.getAttr(Attr.SKILL_LEVEL) * cdModifier);
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
        double dur = this.duration * mod * (1.0 + state.getAttr(Attr.DEBUFF_EFFECT) / 100);
        double r = this.range * mod;
        List<EffectBase> eff = new LinkedList<>();
        TargetBase target = new TargetBase(TargetBase.Type.AROUND, r, 100, Arrays.asList(NpcType.NPC_FRIEND, NpcType.PLAYER, NpcType.ELC_NPC));
        eff.add(new EffectPotion(PotionEffectType.BLINDNESS, dur, new int[]{0, 0, 0, 0, 0, 0, 0, 0}, true, target));
        eff.add(new EffectPotion(PotionEffectType.SLOW, dur, new int[]{1, 1, 1, 1, 2, 2, 2, 2}, true, target));
        EnableEffectBase enable = new EnableEffectBase("§f[§6ISAAC§f]闪光弹已发射！", null, null, explodeSound);
        return new SkillBase("粘弹-闪光弹", cd, "BFB", 0, 1, 0, "BFB", eff, enable);
    }

    @Override
    public List<String> ApplyMod(RAState state) {
        List<String> list = new LinkedList<>();
        double cd = this.cd * (1.0 - state.getAttr(Attr.SKILL_LEVEL) * cdModifier) / (1.0 + state.getAttr(Attr.SKILL_ACCELERATE) / 100);
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
        double dur = this.duration * mod * (1.0 + state.getAttr(Attr.DEBUFF_EFFECT) / 100);
        double r = this.range * mod;
        list.add("§6应用加成后数值：");
        list.add("§7爆炸范围         §e§l" + String.format("%.2f", r));
        list.add("§7持续时间         §e§l" + String.format("%.2f", dur));
        list.add("§7冷却时间         §e§l" + String.format("%.2f", cd));
        return list;
    }
}
