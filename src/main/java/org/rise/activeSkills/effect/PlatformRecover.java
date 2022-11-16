package org.rise.activeSkills.effect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.material.MaterialData;
import org.rise.EntityInf;
import org.rise.State.Attr;
import org.rise.State.RAState;
import org.rise.activeSkills.ActiveType;
import org.rise.activeSkills.ConstantEffect;
import org.rise.skill.Effect.EffectRecover;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.NpcType;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;

import java.util.*;

public class PlatformRecover extends PlatformBase {
    public double recover;

    public PlatformRecover(int lv, double r, double h, double rc, double d, double c) {
        type = ActiveType.PLATFORM_RECOVER;
        lev = lv;
        hp = h;
        range = r;
        recover = rc;
        dur = d;
        cd = c;
        cdModifier = 0.05;
        levelModifier = 0.1;
    }

    @Override
    public List<String> ApplyMod(RAState state) {
        List<String> list = new LinkedList<>();
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * 0.3;
        double cd = this.cd * (1.0 - state.getAttr(Attr.SKILL_LEVEL) * 0.05) / (1.0 + state.getAttr(Attr.SKILL_ACCELERATE) / 100);
        double l = this.range * mod;
        double d = this.dur * mod;
        double re = this.recover * mod;
        double hp = this.hp * mod;
        list.add("§6应用加成后数值：");
        list.add("§7治疗范围         §e§l" + String.format("%.2f", l));
        list.add("§7平台生命值       §e§l" + String.format("%.2f", hp));
        list.add("§7每秒恢复量       §e§l" + String.format("%.2f", re));
        list.add("§7持续时间         §e§l" + String.format("%.2f", d));
        list.add("§7冷却时间         §e§l" + String.format("%.2f", cd));
        return list;
    }

    @Override
    public SkillBase disableSkill(Player player) {
        RAState state = EntityInf.getPlayerState(player);
        state = state.applyModifier(player);
        Map<ActiveBase, Long> tmp = ConstantEffect.lastActive.get(player.getUniqueId());
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
        long lst = tmp.get(this);
        double val = 0.3 * Math.min((System.currentTimeMillis() - lst) / 1000, 15) / 15;
        double cd = this.cd * (1.0 - state.getAttr(Attr.SKILL_LEVEL) * cdModifier);
        double r = this.range * mod;
        player.sendMessage("§f[§6ISAAC§f]已手动关闭支援平台！");
        EnableEffectBase ee = new EnableEffectBase(null, null, null, "modularwarfare:effect.platform_disable");
        SkillBase skill = new SkillBase("支援平台-恢复-手动修复", cd, "PLATFORM-r", 0, 1, 0, "PLATFORM-r", Arrays.asList(new EffectRecover(true, val, new TargetBase(TargetBase.Type.AROUND, r, 100, Arrays.asList(NpcType.NPC_ENEMY, NpcType.OTHER)))), ee);
        UUID zid = ConstantEffect.platformId.get(player.getUniqueId());
        UUID i = player.getUniqueId();
        if (zid != null) {
            SkillAPI.performSkill((LivingEntity) Bukkit.getEntity(zid), skill, false);
            Entity e = Bukkit.getEntity(ConstantEffect.platformId.get(i));
            Zombie z = (Zombie) e;
            z.remove();
        }
        return new SkillBase("支援平台-冷却", cd, "PLATFORM", 0, 1, 0, "PLATFORM", null, null);
    }

    @Override
    public void secondlyCheck(Player player) {
        RAState state = EntityInf.getPlayerState(player);
        state.applyModifier(player);
        Entity e = Bukkit.getEntity(ConstantEffect.platformId.get(player.getUniqueId()));
        Zombie z = (Zombie) e;
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * this.levelModifier;
        double l = this.range * mod;
        double v = this.recover * mod * (1.0 + state.getAttr(Attr.RECOVER_EFFECT) / 100);
        SkillBase skill = new SkillBase("支援平台-恢复-治疗", 0, "PLATFORM-R", 0, 1, 0, "PLATFORM-R", Arrays.asList(new EffectRecover(false, v, new TargetBase(TargetBase.Type.AROUND, l, 100, Arrays.asList(NpcType.NPC_ENEMY, NpcType.OTHER), Arrays.asList(NpcType.PLAYER)))), new EnableEffectBase(null, EnableEffectBase.ParticleType.PLATFORM, Arrays.asList("5"), "modularwarfare:effect.platform_constant"));
        SkillAPI.performSkill(z, skill, false);
        MaterialData data = new MaterialData(Material.STAINED_GLASS);
        data.setData((byte) 5);
        Location loc = z.getLocation();
        for (double a = 0; a < 360; a += 60 / l) {
            double rad = Math.toRadians(a);
            loc.add(l * Math.cos(rad), 1, l * Math.sin(rad));
            z.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 2, 0, 0, 0, 0, data);
            z.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0, 0, 0, 0, data);
            loc.subtract(l * Math.cos(rad), 1, l * Math.sin(rad));
        }
    }
}
