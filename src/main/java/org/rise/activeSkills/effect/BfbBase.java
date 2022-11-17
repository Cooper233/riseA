package org.rise.activeSkills.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.rise.EntityInf;
import org.rise.State.Attr;
import org.rise.State.RAState;
import org.rise.activeSkills.ConstantEffect;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.utils.RayTraceUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class BfbBase extends ActiveBase {
    public double range;
    public double duration;

    public double maxFlyingRange = 30;

    public String explodeSound;

    public byte color;

    public void Indicator(Player player, Location loc, double r) {

        MaterialData data = new MaterialData(Material.STAINED_GLASS);
        data.setData(color);
        for (double a = 0; a < 360; a += 60 / r) {
            double rad = Math.toRadians(a);
            loc.add(r * Math.cos(rad), 0, r * Math.sin(rad));
            player.spawnParticle(Particle.FALLING_DUST, loc, 2, 0, 0, 0, 0, data);
            player.spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0, 0, 0, 0, data);
            loc.subtract(r * Math.cos(rad), 0, r * Math.sin(rad));
        }
    }

    @Override
    public List<String> ApplyMod(RAState state) {
        return null;
    }

    public abstract SkillBase getSkill(RAState state);

    public Location getTargetLoc(Player player) {
//        for(Entity entity:player.getNearbyEntities(maxFlyingRange,maxFlyingRange,maxFlyingRange)){
//            if(player.hasLineOfSight(entity)){
//                return entity.getLocation();
//            }
//        }
        return RayTraceUtils.CurrentHitLoc(player.getEyeLocation(), player.getLocation().getDirection(), 50, Arrays.asList(player));
    }

    @Override
    public void skillAffect(Player player, boolean keyState) {
        RAState state = EntityInf.getPlayerState(player);
        if (keyState) {
            if (ConstantEffect.isActiveTypeAffecting(player, type)) return;
            SkillBase skill = new SkillBase("使用粘弹", 0, "BFB", 0, 1, 0, "BFB", null, new EnableEffectBase("§f[§6ISAAC§f]已准备粘弹发射器，瞄准目标后松开按键以发射！", null, null, "modularwarfare:effect.bfb_prepare"));
            boolean ifSec = SkillAPI.performSkill(player, skill, false);
            if (!ifSec) return;
            UUID i = player.getUniqueId();
            List<ActiveBase> a = ConstantEffect.getAffectingSkill(player);
            a.add(this);
            ConstantEffect.constant.put(i, a);
        } else {
            if (!ConstantEffect.isActiveTypeAffecting(player, type)) return;
            double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
            double r = this.range * mod;
            Location loc = getTargetLoc(player);
            if (loc != null) {
                SkillBase skill = getSkill(state);
                SkillAPI.performSkill(player, loc, skill, false);
                Indicator(player, loc, r);
                MaterialData data = new MaterialData(Material.STAINED_GLASS);
                data.setData(color);
                player.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 45, r, 0.3, r, 0.3, data);
                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 45, r, 0.3, r, 0.3, data);
            } else {
                player.sendMessage("§f[§6ISAAC§f]已取消射击§c【射程不足】");
            }
            UUID i = player.getUniqueId();
            List<ActiveBase> a = ConstantEffect.getAffectingSkill(player);
            a.remove(this);
            ConstantEffect.constant.put(i, a);
            ConstantEffect.usingGel.remove(i);
        }
    }

    @Override
    public void ticklyCheck(Player player) {
        RAState state = EntityInf.getPlayerState(player);
        double mod = 1.0 + state.getAttr(Attr.SKILL_LEVEL) * levelModifier;
        double r = this.range * mod;
        player.playSound(player.getEyeLocation(), "modularwarfare:effect.bfb_aiming", 16, 1);
        Location loc = getTargetLoc(player);
        if (loc != null) {
            Indicator(player, loc, r);
        }
    }

    @Override
    public void secondlyCheck(Player player) {

    }
}
