package org.rise.activeSkills.effect;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.rise.EntityInf;
import org.rise.State.RAstate;
import org.rise.activeSkills.ConstantEffect;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class PlatformBase extends ActiveBase {
    public double range;
    public double hp;
    public double dur;
    public double cd;

    @Override
    public List<String> ApplyMod(RAstate state) {
        return null;
    }

    @Override
    public void skillAffect(Player player, boolean keyState) {
        if (!keyState) return;
        RAstate state = EntityInf.getPlayerState(player);
        state = state.applyModifier(player);
        List<ActiveBase> list = ConstantEffect.getAffectingSkill(player);
        if (!list.contains(this) && !ConstantEffect.platformId.containsKey(player.getUniqueId())) {
            SkillBase skill = new SkillBase("部署支援平台", 0, "PLATFORM", 0, 1, 0, "PLATFORM", null, new EnableEffectBase("§f[§6ISAAC§f]已部署支援平台！", null, null, "modularwarfare:effect.platform_set"));
            boolean ifSec = SkillAPI.performSkill(player, skill, false);
            if (!ifSec) return;
            double mod = 1.0 + state.skillLevel * levelModifier;
            double hp = this.hp * mod;
            LivingEntity entity;
            entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
            entity.setAI(false);
            entity.setCustomName(player.getName() + "的支援平台");
            Zombie z = (Zombie) entity;
            EntityEquipment inv = z.getEquipment();
            inv.setHelmet(new ItemStack(Material.getMaterial("MODULARWARFARE_PROTOTYPEMILITARY_CAP_BLACK")));
            z.setBaby(true);
            z.setSilent(true);
            AttributeInstance instance = z.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            instance.setBaseValue(hp);
            z.setHealth(hp);
            PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 0);
            entity.addPotionEffect(effect);
            list.add(this);
            Map<ActiveBase, Long> tmp = ConstantEffect.lastActive.get(player.getUniqueId());
            if (tmp == null) tmp = new HashMap<>();
            tmp.put(this, System.currentTimeMillis());
            ConstantEffect.lastActive.put(player.getUniqueId(), tmp);
            ConstantEffect.platformId.put(player.getUniqueId(), z.getUniqueId());
            ConstantEffect.constant.put(player.getUniqueId(), list);
        } else {
            UUID i = player.getUniqueId();
            SkillBase skill = disableSkill(player);
            SkillAPI.performSkill(Bukkit.getPlayer(i), skill, true);
            ConstantEffect.platformId.remove(i);
            list.remove(this);
            ConstantEffect.constant.put(i, list);
        }
    }

    public abstract SkillBase disableSkill(Player player);

    @Override
    public void ticklyCheck(Player player) {
        UUID i = player.getUniqueId();
        Entity e = Bukkit.getEntity(ConstantEffect.platformId.get(i));
        RAstate state = EntityInf.getPlayerState(player);
        double mod = 1.0 + state.skillLevel * levelModifier;
        long d = (long) (dur * mod * 1000);
        if (System.currentTimeMillis() - ConstantEffect.lastActive.get(player.getUniqueId()).get(this) > d) {
            ((Damageable) (e)).damage(100000000);
        }
        if (e == null || e.isDead()) {
            ConstantEffect.platformId.remove(i);
            List<ActiveBase> a = ConstantEffect.getAffectingSkill(player);
            a.removeIf(j -> j.type.name().contains("PLATFORM"));
            ConstantEffect.constant.put(i, a);
            double cd = this.cd * (1.0 - state.skillLevel * cdModifier);
            SkillBase skill = new SkillBase("支援平台-冷却", cd, "PLATFORM", 0, 1, 0, "PLATFORM", null, null);
            ;
            SkillAPI.performSkill(player, skill, true);
        }
    }

    @Override
    public void secondlyCheck(Player player) {

    }
}
