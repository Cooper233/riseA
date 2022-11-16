package org.rise.activeSkills.effect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.material.MaterialData;
import org.rise.EntityInf;
import org.rise.State.Attr;
import org.rise.State.RAState;
import org.rise.activeSkills.ActiveType;
import org.rise.activeSkills.ConstantEffect;
import org.rise.skill.Effect.EffectBase;
import org.rise.skill.Effect.EffectRecover;
import org.rise.skill.Effect.EffectRevive;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.NpcType;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PlatformRevive extends PlatformRecover {
    public PlatformRevive(int lv, double r, double h, double rc, double d, double c) {
        super(lv, r, h, rc, d, c);
        type = ActiveType.PLATFORM_REVIVE;
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
        TargetBase target = new TargetBase(TargetBase.Type.AROUND, l, 100, Arrays.asList(NpcType.NPC_ENEMY, NpcType.OTHER), Arrays.asList(NpcType.PLAYER));
        List<EffectBase> eff = new LinkedList<>();
        eff.add(new EffectRecover(false, v, target));
        eff.add(new EffectRevive(target));
        SkillBase skill = new SkillBase("支援平台-复活-治疗", 0, "PLATFORM-R", 0, 1, 0, "PLATFORM-R", eff, new EnableEffectBase(null, EnableEffectBase.ParticleType.PLATFORM, Arrays.asList("5"), "modularwarfare:effect.platform_constant"));
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
