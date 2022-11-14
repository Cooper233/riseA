package org.rise.activeSkills.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.rise.EntityInf;
import org.rise.State.RAstate;
import org.rise.activeSkills.ActiveType;
import org.rise.activeSkills.ConstantEffect;
import org.rise.effect.CustomEffect;
import org.rise.skill.Effect.EffectBase;
import org.rise.skill.Effect.EffectCustomEffect;
import org.rise.skill.Effect.EffectDamage;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.NpcType;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;

import java.util.*;

public class PulseEMP extends PulseBase {
    public double damage_elc;

    public PulseEMP(int lv, double r, double d, double de, double c) {
        type = ActiveType.PULSE_EMP;
        lev = lv;
        range = r;
        dur = d;
        damage_elc = de;
        cd = c;
        cdModifier = 0.05;
        levelModifier = 0.15;
    }

    @Override
    public List<String> ApplyMod(RAstate state) {
        List<String> list = new LinkedList<>();
        double cd = this.cd * (1.0 - state.skillLevel * cdModifier) / state.skillAccelerate;
        double mod = 1.0 + state.skillLevel * levelModifier;
        double d = this.dur * mod;
        double de = this.damage_elc * mod * state.skillDamage;
        double r = this.range * mod;
        list.add("§6应用加成后数值：");
        list.add("§7最大脉冲范围     §e§l" + String.format("%.2f", r));
        list.add("§7造成真实伤害     §e§l" + String.format("%.2f", de));
        list.add("§7持续时间        §e§l" + String.format("%.2f", d));
        list.add("§7冷却时间        §e§l" + String.format("%.2f", cd));
        return list;
    }

    @Override
    public void beginningEffect(Player player) {
        if (ConstantEffect.isActiveTypeAffecting(player, this.type)) return;
        SkillBase skill = new SkillBase("开启脉冲充能", 0, "PULSE", 0, 1, 0, "PULSE", null, new EnableEffectBase("§f[§6ISAAC§f]EMP脉冲发生器正在充能！", null, null, ""));
        boolean ifSec = SkillAPI.performSkill(player, skill, false);
        if (!ifSec) return;
        player.playSound(player.getLocation(), "modularwarfare:effect.pulse_emp_prepare", 64, 1);
        UUID i = player.getUniqueId();
        List<ActiveBase> a = ConstantEffect.getAffectingSkill(player);
        a.add(this);
        ConstantEffect.constant.put(i, a);
        Map<ActiveBase, Long> tmp = ConstantEffect.lastActive.get(player.getUniqueId());
        if (tmp == null) tmp = new HashMap<>();
        tmp.put(this, System.currentTimeMillis());
        ConstantEffect.lastActive.put(i, tmp);
    }

    @Override
    public void endingEffect(Player player) {
        if (!ConstantEffect.isActiveTypeAffecting(player, this.type)) return;
        empBlast(player);
    }

    public void empBlast(Player player) {
        RAstate state = EntityInf.getPlayerState(player);
        player.stopSound("modularwarfare:effect.pulse_emp_prepare");
        Map<ActiveBase, Long> tmp = ConstantEffect.lastActive.get(player.getUniqueId());
        double pt = System.currentTimeMillis() - tmp.get(this);
        double pg = pt / 4000 * 0.8 + 0.2;
        double cd = this.cd * (1.0 - state.skillLevel * cdModifier);
        double mod = 1.0 + state.skillLevel * levelModifier;
        double d = this.dur * mod;
        double de = this.damage_elc * mod * state.skillDamage;
        double r = this.range * mod * pg;
        List<EffectBase> eff = new LinkedList<>();
        TargetBase t1 = new TargetBase(TargetBase.Type.AROUND, r, 100, Arrays.asList(NpcType.PLAYER, NpcType.NPC_FRIEND));
        TargetBase t2 = new TargetBase(TargetBase.Type.AROUND, r, 100, Arrays.asList(NpcType.PLAYER, NpcType.NPC_FRIEND), Arrays.asList(NpcType.ELC_NPC));
        eff.add(new EffectCustomEffect(CustomEffect.DISTURBED, d, this.lev, t1));
        eff.add(new EffectDamage(0, 30000, 0, 0, 3000, de, 0, t2));
        EnableEffectBase enable = new EnableEffectBase("§f[§6ISAAC§f]EMP脉冲已启动!", null, null, "modularwarfare:effect.pulse_emp_release");
        SkillBase skill = new SkillBase("脉冲-EMP", cd, "PULSE", 0, 1, 0, "PULSE", eff, enable);
        SkillAPI.performSkill(player, skill, false);
        MaterialData data = new MaterialData(Material.STAINED_GLASS);
        Location loc = player.getLocation();
        data.setData((byte) 11);
        for (double a = 0; a < 360; a += 60 / r) {
            double rad = Math.toRadians(a);
            loc.add(r * Math.cos(rad), 1.4, r * Math.sin(rad));
            player.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 2, 0, 0, 0, 0, data);
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0, 0, 0, 0, data);
            loc.subtract(r * Math.cos(rad), 1.4, r * Math.sin(rad));
        }
        player.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 100, r, 1, r, 0.2, data);
        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 100, r, 1, r, 0.2, data);
        UUID i = player.getUniqueId();
        List<ActiveBase> a = ConstantEffect.getAffectingSkill(player);
        a.remove(this);
        ConstantEffect.constant.put(i, a);
        ConstantEffect.usingPulse.remove(i);
    }

    @Override
    public void ticklyCheck(Player player) {
        RAstate state = EntityInf.getPlayerState(player);
        Map<ActiveBase, Long> tt = ConstantEffect.lastActive.get(player.getUniqueId());
        double pt = System.currentTimeMillis() - tt.get(this);
        double pg = pt / 4000 * 0.8 + 0.2;
        pg = Math.min(1.0, pg);
        double cd = this.cd * (1.0 - state.skillLevel * cdModifier);
        double mod = 1.0 + state.skillLevel * levelModifier;
        double r = this.range * mod * pg;
        MaterialData data = new MaterialData(Material.STAINED_GLASS);
        Location loc = player.getLocation();
        data.setData((byte) 11);
        for (double a = 0; a < 360; a += 60 / r) {
            double rad = Math.toRadians(a);
            loc.add(r * Math.cos(rad), 1.4, r * Math.sin(rad));
            player.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 2, 0, 0, 0, 0, data);
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0, 0, 0, 0, data);
            loc.subtract(r * Math.cos(rad), 1.4, r * Math.sin(rad));
        }
        if (pt >= 4600) {
            player.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 100, r, 1, r, 0.2, data);
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 100, r, 1, r, 0.2, data);
            player.stopSound("modularwarfare:effect.pulse_emp_prepare");
            double d = this.dur * mod;
            double de = this.damage_elc * mod * state.skillDamage;
            List<EffectBase> eff = new LinkedList<>();
            TargetBase t1 = new TargetBase(TargetBase.Type.AROUND, r, 100, Arrays.asList(NpcType.PLAYER, NpcType.NPC_FRIEND));
            TargetBase t2 = new TargetBase(TargetBase.Type.AROUND, r, 100, Arrays.asList(NpcType.PLAYER, NpcType.NPC_FRIEND), Arrays.asList(NpcType.ELC_NPC));
            eff.add(new EffectCustomEffect(CustomEffect.DISTURBED, d, this.lev, t1));
            eff.add(new EffectDamage(0, 30000, 0, 0, 3000, de, 0, t2));
            EnableEffectBase enable = new EnableEffectBase("§f[§6ISAAC§f]EMP脉冲已启动!", null, null, "modularwarfare:effect.pulse_emp_release");
            SkillBase skill = new SkillBase("脉冲-EMP", cd, "PULSE", 0, 1, 0, "PULSE", eff, enable);
            SkillAPI.performSkill(player, skill, false);
            List<ActiveBase> a = ConstantEffect.getAffectingSkill(player);
            a.remove(this);
            ConstantEffect.constant.put(player.getUniqueId(), a);
            ConstantEffect.usingPulse.remove(player.getUniqueId());
        }
    }
}
