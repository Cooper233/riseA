package org.rise.activeSkills.effect;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.rise.EntityInf;
import org.rise.State.AttrModifier;
import org.rise.State.BuffStack;
import org.rise.State.RAstate;
import org.rise.activeSkills.ActiveType;
import org.rise.skill.Effect.EffectAttr;
import org.rise.skill.Effect.EffectBase;
import org.rise.skill.Effect.EffectPotion;
import org.rise.skill.Effect.EffectStack;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.NpcType;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PulseScan extends PulseBase {
    public int stack;
    public double dec_avoid;
    public double inc_damage;

    public PulseScan(int lv, int s, double r, double da, double id, double d, double c) {
        type = ActiveType.PULSE_SCAN;
        lev = lv;
        stack = s;
        range = r;
        dec_avoid = da;
        inc_damage = id;
        dur = d;
        cd = c;
        cdModifier = 0.05;
        levelModifier = 0.15;
    }

    @Override
    public List<String> ApplyMod(RAstate state) {
        List<String> list = new LinkedList<>();
        double mod = 1.0 + state.skillLevel * 0.3;
        double cd = this.cd * (1.0 - state.skillLevel * 0.05) / state.skillAccelerate;
        double l = this.range * mod;
        double d = this.dur * mod;
        double da = this.dec_avoid * mod * state.debuffEffect;
        double ic = this.inc_damage * mod * state.debuffEffect / 100;
        int st = (int) (this.stack * mod);
        list.add("§6应用加成后数值：");
        list.add("§7扫描范围         §e§l" + String.format("%.2f", l));
        list.add("§7叠加“脉冲”层数    §e§l" + String.format("%d", st));
        list.add("§7降低闪避         §e§l" + String.format("%.2f", da) + "%");
        list.add("§7提高受伤害比例    §e§l" + String.format("%.2f", ic));
        list.add("§7持续时间         §e§l" + String.format("%.2f", d));
        list.add("§7冷却时间         §e§l" + String.format("%.2f", cd));
        return list;
    }

    @Override
    public void beginningEffect(Player player) {
        RAstate state = EntityInf.getPlayerState(player);
        double mod = 1.0 + state.skillLevel * levelModifier;
        double cd = this.cd * (1.0 - state.skillLevel * this.cdModifier);
        double l = this.range * mod;
        double d = this.dur * mod;
        double da = Math.max(0, (100 - this.dec_avoid * mod) / 100);
        double ic = this.inc_damage * mod;
        int st = (int) (this.stack * mod);
        da *= state.debuffEffect;
        ic *= state.debuffEffect;
        List<EffectBase> eff = new LinkedList<>();
        TargetBase target = new TargetBase(TargetBase.Type.AROUND, l, 100, Arrays.asList(NpcType.PLAYER, NpcType.NPC_FRIEND));
        eff.add(new EffectAttr(AttrModifier.Attr.AVOID, d, da, AttrModifier.ModType.MULTIPLY, true, target));
        eff.add(new EffectAttr(AttrModifier.Attr.DAMAGE_RECEIVE, d, ic / 100, AttrModifier.ModType.PLUS, true, target));
        eff.add(new EffectPotion(PotionEffectType.getById(24), d, new int[]{0, 0, 0, 0, 0, 0, 0}, true, target));
        eff.add(new EffectStack(BuffStack.StackType.PULSE_AFFECT, st, target));
        EnableEffectBase ee = new EnableEffectBase("§f[§6ISAAC§f]已启用脉冲扫描！", EnableEffectBase.ParticleType.PULSE, Arrays.asList("1", "0"), "modularwarfare:effect.pulse_scan");
        SkillBase skill = new SkillBase("脉冲扫描", cd, "PULSE", 0, 1, 5, "PULSE", eff, ee);
        SkillAPI.performSkill(player, skill, false);
    }

    @Override
    public void endingEffect(Player player) {

    }
}
