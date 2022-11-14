package org.rise.activeSkills.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.rise.EntityInf;
import org.rise.State.RAstate;
import org.rise.activeSkills.ConstantEffect;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;

import java.util.*;

public abstract class BfbBase extends ActiveBase {
    public double range;
    public double duration;

    public double maxFlyingRange = 30;

    public String explodeSound;

    public byte color;

    public void Indicator(Player player, Location loc, double r) {

        loc.add(0, 1.2, 0);
        MaterialData data = new MaterialData(Material.STAINED_GLASS);
        data.setData(color);
        for (double a = 0; a < 360; a += 60 / r) {
            double rad = Math.toRadians(a);
            loc.add(r * Math.cos(rad), 0, r * Math.sin(rad));
            player.spawnParticle(Particle.FALLING_DUST, loc, 2, 0, 0, 0, 0, data);
            player.spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0, 0, 0, 0, data);
            loc.subtract(r * Math.cos(rad), 0, r * Math.sin(rad));
        }

        loc.subtract(0, 1.2, 0);
    }

    @Override
    public List<String> ApplyMod(RAstate state) {
        return null;
    }

    public abstract SkillBase getSkill(RAstate state);

    public Location getTargetLoc(Player player) {
//        for(Entity entity:player.getNearbyEntities(maxFlyingRange,maxFlyingRange,maxFlyingRange)){
//            if(player.hasLineOfSight(entity)){
//                return entity.getLocation();
//            }
//        }
        Set<Material> set = new HashSet<>(Arrays.asList(Material.AIR, Material.GRASS, Material.TORCH));
        Block block = player.getTargetBlock(null, (int) (maxFlyingRange));
        if (block == null) return null;
        return block.getType() == Material.AIR ? null : block.getLocation();
    }

    @Override
    public void skillAffect(Player player, boolean keyState) {
        RAstate state = EntityInf.getPlayerState(player);
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
            double mod = 1.0 + state.skillLevel * levelModifier;
            double r = this.range * mod;
            Location loc = getTargetLoc(player);
            if (loc != null) {
                loc.add(0, 1.2, 0);
                SkillBase skill = getSkill(state);
                SkillAPI.performSkill(player, loc, skill, false);
                Indicator(player, loc, r);
                MaterialData data = new MaterialData(Material.STAINED_GLASS);
                data.setData(color);
                player.getWorld().spawnParticle(Particle.FALLING_DUST, loc, 45, r, 0.3, r, 0.3, data);
                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 45, r, 0.3, r, 0.3, data);
                loc.add(0, -1.2, 0);
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
        RAstate state = EntityInf.getPlayerState(player);
        double mod = 1.0 + state.skillLevel * levelModifier;
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
