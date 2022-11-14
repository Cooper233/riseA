package org.rise.Listener;

import lk.vexview.event.KeyBoardPressEvent;
import lk.vexview.event.MinecraftKeys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.rise.*;
import org.rise.GUI.exhpGUI;
import org.rise.State.AttrModifier;
import org.rise.State.BuffStack;
import org.rise.State.RAstate;
import org.rise.activeSkills.ConstantEffect;
import org.rise.activeSkills.effect.ActiveBase;
import org.rise.activeSkills.effect.ShieldCovered;
import org.rise.activeSkills.effect.ShieldCrusaders;
import org.rise.skill.Effect.*;
import org.rise.skill.Enable.EnableEffectBase;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;
import org.rise.talent.TalentType;
import org.rise.team.TeamBase;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class EntityAttackProcess implements Listener {
    private double pow(double x) {
        return x * x;
    }

    private double getDis(Vector x) {
        return Math.sqrt(pow(x.getX()) + 0 + pow(x.getZ()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void EntityAttackEntityProcess(EntityDamageByEntityEvent event) {
        Player tp = Bukkit.getPlayer("Tech635");
//        if (event.isCancelled()) return;
        event.setCancelled(false);
//        tp.sendMessage(event.getEntity().getClass().getName());

        if (!(event.getEntity() instanceof LivingEntity && event.getEntity() instanceof Damageable)) return;
        World world = event.getEntity().getWorld();
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity def = (LivingEntity) event.getEntity();
        UUID uuid = def.getUniqueId();
        RAstate defState = new RAstate();
        if (def instanceof Player) {
            defState = EntityInf.getPlayerState((Player) def);
            long time = System.currentTimeMillis();
            long pt = 2500;
            if (world.getName().startsWith("pvp")) pt = 100;
            if (time - EntityInf.getLastProtect(def) <= pt) {
                event.setCancelled(true);
                return;
            }
        }
        defState.setDefault();
        defState.init(def);
        riseAPI.reAnalyseLores(defState, def.getHealth() / def.getMaxHealth());
        if (def instanceof Player) defState = EntityInf.getPlayerState(def.getUniqueId());
        LivingEntity att = null;
        Entity damager = event.getDamager();
        if (damager.getType().getName() != null && damager.getType().getName().startsWith("mw_Ammo")) {
            try {
                att = BridgeVic.getShooter(damager);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (event.getDamager().getType() == EntityType.UNKNOWN) {
            try {
                att = (LivingEntity) BridgeNpc.getShooter(damager);
                event.setCancelled(true);
                def.damage(0, att);
                return;
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                att = (LivingEntity) projectile.getShooter();
            } else att = (LivingEntity) damager;
        }
        if (att == null) return;
        if (att instanceof Player && def instanceof Player) {
            if (!world.getName().startsWith("pvp")) {
                event.setCancelled(true);
                return;
            } else if (TeamBase.getNowTeam((Player) att) == TeamBase.getNowTeam((Player) def) && TeamBase.getNowTeam((Player) att) != null) {
                event.setCancelled(true);
                return;
            }
        }
        RAstate attState = new RAstate();
        if (att instanceof Player) attState = EntityInf.getPlayerState((Player) att);
//        tp.sendMessage("2");
        if (attState.downed) {
            event.setCancelled(true);
            return;
        }
        attState.setDefault();
        attState.init(att);
        riseAPI.reAnalyseLores(attState, att.getHealth() / att.getMaxHealth());
        attState = attState.applyModifier(att);
        defState = defState.applyModifier(def);
        if (att instanceof Player && ConstantEffect.usingShield.contains(att.getUniqueId())) {
            if (System.currentTimeMillis() - ConstantEffect.lastUseShield.get(att.getUniqueId()) <= 500) {
                event.setCancelled(true);
                return;
            }
            ActiveBase now = null;
            if (ConstantEffect.constant.containsKey(att.getUniqueId())) {
                for (ActiveBase i : ConstantEffect.constant.get(att.getUniqueId())) {
                    if (i.type.name().contains("SHIELD")) {
                        now = i;
                        break;
                    }
                }
                if (now != null) {
                    List<String> conType = new LinkedList<>();
                    switch (Objects.requireNonNull(now).type) {
                        case SHIELD_COVERED: {
                            conType = Arrays.asList("手枪", "近战武器");
                            if (attState.hasTalent(TalentType.COURAGE)) {
                                conType.add("冲锋枪");
                                conType.add("轻型枪械");
                            }
                            break;
                        }
                        case SHIELD_CRUSADERS: {
                            conType = Arrays.asList("手枪", "近战武器", "冲锋枪", "霰弹枪", "轻型枪械");
                            break;
                        }
                    }
                    boolean sec = false;
                    for (String s : conType) {
                        if (attState.hasType(s)) {
                            sec = true;
                            break;
                        }
                    }
                    if (!sec) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
//        tp.sendMessage("3");
        double damage = event.getDamage();
        if (damage == 0) damage = attState.damage;
        if (att instanceof Player) damage = attState.damage;
        damage *= 1.0 + attState.finalDamage / 100.0;
        double hitMod = 1.0;
        attState.hit = Math.max(1, attState.hit * hitMod);
        if (att.getWorld() == def.getWorld()) {
            int light = world.getBlockAt(def.getLocation()).getLightLevel();
            double lightRate = 1.0 * (light + 1) / 16.0;
            hitMod = 0.1 + 0.9 * (lightRate + (1 - lightRate) * attState.nfAbility / 100);
            attState.hit = Math.max(1, attState.hit * hitMod);
            double dis = att.getLocation().distance(def.getLocation());
            if (dis <= 3) attState.hit *= 1.5;
            if (dis <= 2) attState.hit *= 2;
            if (dis <= 1) attState.hit *= 2;

        }

        double chance2 = Math.random();
        boolean ifCrit = false, ifDodged = false, ifKilled = false, ifIndirect = false, ifHeadshot = false;
        if (chance2 <= attState.critChance / 100) {//暴击判定
            ifCrit = true;
            damage = damage * ((100 + attState.critRate) / 100);
        }
        double hitRate = attState.hit / (attState.hit + defState.avoid);
        double chance1 = Math.random();
        if (chance1 > hitRate) {//命中判定
            ifDodged = true;
            event.setCancelled(true);
            world.spawnParticle(Particles.getMissParticle(), def.getLocation(), 2, 0, 0, 0, 0.05);
        }
        if (ifCrit && !ifDodged)
            world.spawnParticle(Particles.getCritParticle(), def.getLocation(), 3);
        if (damager.getType().name().startsWith("PLAYER")) {
            Location loc1 = damager.getLocation();
            Vector dir = loc1.getDirection();
            double v1 = Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ());
            Location loc2 = def.getEyeLocation();
            double dis = getDis(new Vector(loc1.getX() - loc2.getX(), 0, loc1.getZ() - loc2.getZ()));
            double y1 = dis / v1 * dir.getY() + loc1.getY();
//            tp.sendMessage("y1:"+Math.abs(y1-loc2.getY())+ "def:"+def.getEyeHeight()+" pt:"+Math.abs(Math.abs(y1-loc2.getY())-def.getEyeHeight() )/def.getEyeHeight());
            if (!defState.nonHeadshot) {
                if (Math.abs(Math.abs(y1 - loc2.getY()) - def.getEyeHeight()) / def.getEyeHeight() <= 0.15) {
                    ifHeadshot = true;
                    damage *= 1.0 + attState.headshotRate / 100;
                }
            }
        }
        damage += Math.min(attState.percentDamage / 100 * def.getMaxHealth(), riseA.percentDamageMax);
        double bfDamage = damage;
        if (att instanceof Player && def instanceof Player) {
            bfDamage *= 2;
            damage *= 0.5;
        }
        double ab = defState.physicalResistance - attState.physicalPiercing;
        if (ab > 0) damage -= ab;
        else damage += Math.min(ab, 2);
        damage = Math.max(damage, 1);
        damage = damage * defState.damageReceive;
        damage += Math.min(riseA.trueDamageMax, attState.trueDamage);
        bfDamage += Math.min(riseA.trueDamageMax, attState.trueDamage);
        damage *= (100 - defState.specialResistance) / 100;
        if (def instanceof Player) {
            damage = defState.resistDamage(damage);
            EntityInf.playersAttr.put(def.getUniqueId(), defState);
        }
        event.setDamage(damage);
        double fd = event.getFinalDamage();
        if (!ifDodged && fd >= def.getHealth()) {
            ifKilled = true;
        }
        LivingEntity ori = EntityInf.getOriginAttacker(att.getUniqueId());
        if (EntityInf.getOriginAttacker(att.getUniqueId()) != null) {
            //有关技能击杀的判定之类的
            ifIndirect = true;
        }
        for (TalentType i : attState.activeTalent) {
            switch (i) {
                case KILLER: {
                    if (ifCrit && ifKilled) {
                        SkillBase skill = new SkillBase("杀手", 0, 1, 10, "KILLER", Arrays.asList(new EffectAttr(AttrModifier.Attr.CRIT_RATE, 10, 40, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case MAINTAIN: {
                    if (ifKilled) {
                        double mod = 0.02;
                        if (ifHeadshot) mod *= 2;
                        SkillBase skill = new SkillBase("维护保存", 0, 1, 0, "MAINTAIN", Arrays.asList(new EffectAttr(AttrModifier.Attr.HP_REGEN, 5, att.getMaxHealth() * mod, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case MAINTAIN_PERFECT: {
                    if (ifKilled) {
                        double mod = 0.04;
                        if (ifHeadshot) mod *= 2;
                        SkillBase skill = new SkillBase("完美维护保存", 0, 1, 0, "MAINTAIN_PERFECT", Arrays.asList(new EffectAttr(AttrModifier.Attr.HP_REGEN, 5, att.getMaxHealth() * mod, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case PRESSURE: {
                    if (System.currentTimeMillis() - EntityInf.getLastAttack(att) <= 500) {
                        SkillBase skill = new SkillBase("压力使然", 0, 1, 0, "PRESSURE", Arrays.asList(new EffectStack(BuffStack.StackType.PRESSURE, 1, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case CLOSE_COMBAT: {
                    if (ifKilled && att.getWorld() == def.getWorld()) {
                        if (att.getLocation().distance(def.getLocation()) <= 5) {
                            SkillBase skill = new SkillBase("短兵相接", 0, 1, 10, "CLOSE_COMBAT", Arrays.asList(new EffectAttr(AttrModifier.Attr.FINAL_DAMAGE, 10, 30, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                            SkillAPI.performSkill(att, skill, false);
                        }
                    }
                    break;
                }
                case SADISM: {
                    if (EntityInf.killCount.containsKey(att.getUniqueId()) && EntityInf.killCount.get(att.getUniqueId()) == 4) {
                        EntityInf.killCount.put(att.getUniqueId(), 0);
                        SkillBase skill = new SkillBase("虐待狂", 0, 1, 0, "SADISM", Arrays.asList(new EffectPotion(PotionEffectType.SLOW, 5 * attState.debuffEffect, new int[]{1, 1, 1, 1, 1, 1, 1}, true, TargetBase.SELF)));
                        SkillAPI.performSkill(def, skill, false);
                    }
                    if (def.hasPotionEffect(PotionEffectType.SLOW)) {
                        damage *= 1.2;
                    }
                    break;
                }
                case REVENGE: {
                    if (ifKilled) {
                        if (EntityInf.entityModifier.containsKey(def.getUniqueId())) {
                            List<AttrModifier> l = EntityInf.entityModifier.get(def.getUniqueId());
                            boolean find = false;
                            for (AttrModifier j : l) {
                                if (j.val < 0 || (j.tar == AttrModifier.Attr.DAMAGE_RECEIVE && j.val > 0)) {
                                    find = true;
                                    break;
                                }
                            }
                            if (find) {
                                SkillBase skill = new SkillBase("报复之心", 0, 1, 20, "REVENGE", Arrays.asList(new EffectAttr(AttrModifier.Attr.CRIT_RATE, 20, 15, AttrModifier.ModType.PLUS, false, TargetBase.TEAM), new EffectAttr(AttrModifier.Attr.CRIT, 20, 15, AttrModifier.ModType.PLUS, false, TargetBase.TEAM)));
                                SkillAPI.performSkill(att, skill, false);
                            }
                        }
                    }
                    break;
                }
                case RANGER: {
                    if (att.getWorld() == def.getWorld()) {
                        double mod = 1.0 + 0.03 * att.getLocation().distance(def.getLocation()) / 2;
                        damage *= mod;
                    }
                    break;
                }
                case RANGER_PERFECT: {
                    if (att.getWorld() == def.getWorld()) {
                        double mod = 1.0 + 0.05 * att.getLocation().distance(def.getLocation()) / 2;
                        damage *= mod;
                    }
                    break;
                }
                case STEADY: {
                    if (!ifDodged) {
                        SkillBase skill = new SkillBase("双手沉稳", 0, 1, 0, "STEADY", Arrays.asList(new EffectAttr(AttrModifier.Attr.HIT, 10, 1, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case PRICK: {
                    if (!ifDodged && ifHeadshot) {
                        SkillBase skill = new SkillBase("刺击", 0, 1, 15, "PRICK", Arrays.asList(new EffectAttr(AttrModifier.Attr.SKILL_DAMAGE, 15, 0.2, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case BLIND: {
                    if (EntityInf.killCount.containsKey(att.getUniqueId()) && EntityInf.killCount.get(att.getUniqueId()) == 4) {
                        EntityInf.killCount.put(att.getUniqueId(), 0);
                        SkillBase skill = new SkillBase("致盲", 0, 1, 0, "BLIND", Arrays.asList(new EffectPotion(PotionEffectType.BLINDNESS, 5 * attState.debuffEffect, new int[]{0, 0, 0, 0, 0, 0, 0}, true, TargetBase.SELF)));
                        SkillAPI.performSkill(def, skill, false);
                    }
                    if (def.hasPotionEffect(PotionEffectType.SLOW)) {
                        damage *= 1.2;
                    }
                    break;
                }
                case PERMANENCE: {
                    if (!ifDodged && ifHeadshot) {
                        SkillBase skill = new SkillBase("长存", 0, 1, 25, "PERMANENCE", Arrays.asList(new EffectAttr(AttrModifier.Attr.DEBUFF_EFFECT, 5, 0.5, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case REVOLT: {
                    if (!ifDodged && ifHeadshot) {
                        SkillBase skill = new SkillBase("改革", 0, 1, 25, "REVOLT", Arrays.asList(new EffectAttr(AttrModifier.Attr.RECOVER_EFFECT, 15, 0.3, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case FUTURE: {
                    if (ifKilled) {
                        SkillBase skill;
                        if (attState.skillLevel < 6) {
                            skill = new SkillBase("未来完成式", 0, 1, 5, "FUTURE", Arrays.asList(new EffectAttr(AttrModifier.Attr.SKILL_LEVEL, 15, 1, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        } else {
                            skill = new SkillBase("未来完成式-6", 0, 1, 15, "FUTURE-6", Arrays.asList(new EffectAttr(AttrModifier.Attr.SKILL_DAMAGE, 15, 0.3, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        }
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case SYNCHRO://默认只有玩家拥有这个天赋
                {
                    if (!ifDodged) {
                        double val = 0.15;
                        if (System.currentTimeMillis() - EntityInf.getLastSkillAttack(att) < 5000) val *= 2;
                        SkillBase skill = new SkillBase("同步伤害-1", 0, 1, 5, "REVOLT-1", Arrays.asList(new EffectAttr(AttrModifier.Attr.SKILL_DAMAGE, 5, val, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    if (ifIndirect && !ifDodged) {
                        LivingEntity e = EntityInf.getOriginAttacker(att.getUniqueId());
                        RAstate s = EntityInf.getPlayerState(e.getUniqueId());
                        if (s.activeTalent.contains(TalentType.SYNCHRO)) {
                            double val = 15;
                            if (System.currentTimeMillis() - EntityInf.getLastAttack(e) < 5000) val *= 2;
                            SkillBase skill = new SkillBase("同步伤害-2", 0, 1, 5, "REVOLT-2", Arrays.asList(new EffectAttr(AttrModifier.Attr.FINAL_DAMAGE, 5, val, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                            SkillAPI.performSkill(e, skill, false);
                        }
                    }
                    break;
                }
                case ASYSTOLE: {
                    if (EntityInf.killCount.containsKey(att.getUniqueId()) && EntityInf.killCount.get(att.getUniqueId()) == 3) {
                        EntityInf.killCount.put(att.getUniqueId(), 0);
                        SkillBase skill = new SkillBase("停搏", 0, 1, 0, "ASYSTOLE", Arrays.asList(new EffectStack(BuffStack.StackType.PULSE_AFFECT, 50, TargetBase.SELF)));
                        SkillAPI.performSkill(def, skill, false);
                    }
                    if (defState.buffStack.containsKey(BuffStack.StackType.PULSE_AFFECT)) {
                        damage *= 1.15;
                    }
                    break;
                }
                case FIRST_BLOOD: {
                    if (System.currentTimeMillis() - EntityInf.getLastAttack(att) > 15000) {
                        damage *= 1.0 + attState.critRate / 100;
                    }
                    break;
                }
                case PUNCH: {
                    if (ifKilled && System.currentTimeMillis() - EntityInf.getLastKilled(att) < 3000) {
                        SkillBase skill = new SkillBase("拳拳到肉", 0, 1, 10, "PUNCH", Arrays.asList(new EffectAttr(AttrModifier.Attr.FINAL_DAMAGE, 10, 40, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case OUTSIDER: {
                    if (ifKilled) {
                        SkillBase skill = new SkillBase("局外人", 0, 1, 10, "OUTSIDER", Arrays.asList(new EffectAttr(AttrModifier.Attr.HIT, 10, 2, AttrModifier.ModType.MULTIPLY, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case ICE_STORM: {
                    double val = 0.005;
                    if (def.hasPotionEffect(PotionEffectType.SLOW)) val *= 2;
                    damage += def.getMaxHealth() * val;
                    break;
                }
                case BARRIER: {
                    if (!ifDodged && ifCrit) {
                        riseAPI.addBuffStack(att, BuffStack.StackType.SUPPRESS, 2);
                    }
                    break;
                }
                case HEMOPHAGIA: {
                    if (ifKilled) {
                        SkillBase skill = new SkillBase("吸血生物", 0, 1, 2, "HEMOPHAGIA", Arrays.asList(new EffectExHp(10, att.getMaxHealth() * 0.1, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case UNSTOPPABLE: {
                    if (ifKilled) {
                        SkillBase skill = new SkillBase("无人能挡", 0, 1, 0, "UNSTOPPABLE", Arrays.asList(new EffectStack(BuffStack.StackType.UNSTOPPABLE, 1, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case SHOCK: {
                    if (ifHeadshot && !ifDodged) {
                        double d = 1.5;
                        if (attState.type.containsKey("狙击枪")) {
                            d = 5;
                        }
                        SkillBase skill = new SkillBase("震荡", 0, 1, d, "SHOCK", Arrays.asList(new EffectAttr(AttrModifier.Attr.FINAL_DAMAGE, d, 10, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    if (ifHeadshot && ifKilled) {
                        SkillBase skill = new SkillBase("震荡-1", 0, 1, 10, "SHOCK-1", Arrays.asList(new EffectAttr(AttrModifier.Attr.FINAL_DAMAGE, 10, 15, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case OBLITERATIVE: {
                    if (ifCrit && !ifDodged) {
                        SkillBase skill = new SkillBase("抹灭性破坏", 0, 1, 0, "OBLITERATIVE", Arrays.asList(new EffectStack(BuffStack.StackType.OBLITERATIVE, 1, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case HEAD_HUNTER: {
                    if (EntityInf.getLastKilled(att) == EntityInf.getLastHeadshot(att) && EntityInf.getLastAttack(att) == EntityInf.getLastKilled(att) && System.currentTimeMillis() - EntityInf.getLastKilled(att) <= 30000) {
                        double val = EntityInf.getLastAttackAmount(att);
                        double src = attState.damage;
                        double _max = src * 8;
                        if (attState.headshotRate > 150) {
                            _max = src * 12.5;
                        }
                        val = Math.min(_max, val * 1.25);
                        damage += val;
                        break;
                    }
                }
                case WHITE_AOGOU: {
                    if (!ifDodged) {
                        double sec = Math.random();
                        if (sec > 0.15) break;
                        SkillBase skill = new SkillBase("白獒之眼", 0, 1, 5, "WHITE_AOGOU", Arrays.asList(new EffectAttr(AttrModifier.Attr.FINAL_DAMAGE, 3, 40, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    break;
                }
                case SILENT_KILLING: {
                    if (defState.physicalResistance <= attState.physicalPiercing * 0.2) {
                        damage *= 1.05;
                    }
                    break;
                }
                case KIMERA: {
                    if (!ifDodged && ifHeadshot && ifCrit) {
                        List<EffectBase> list = new LinkedList<>();
                        list.add(new EffectPotion(PotionEffectType.SLOW, 3, new int[]{4, 4, 4, 4, 4, 4, 4}, true, TargetBase.SELF));
                        list.add(new EffectPotion(PotionEffectType.WEAKNESS, 20, new int[]{1, 1, 1, 1, 1, 1}, true, TargetBase.SELF));
                        SkillBase skill;
                        skill = new SkillBase("KIMERA", 0, 1, 60, "KIMERA_debuff", list);
                        SkillAPI.performSkill(def, skill, false);
                    }
                    break;
                }
                case CHAMELEON: {
                    if (ifDodged) break;
                    if (ifHeadshot) {
                        int head = attState.getStackNum(BuffStack.StackType.CHAMELEON_HEAD);
//                        tp.sendMessage("HEAD:"+head);
                        if (head == 14) {
                            riseAPI.addBuffStack(att, BuffStack.StackType.CHAMELEON_HEAD, -14);
                            List<EffectBase> list = new LinkedList<>();
                            list.add(new EffectStack(BuffStack.StackType.CHAMELEON_HEAD_BUFF, 1, TargetBase.SELF));
                            SkillBase skill = new SkillBase("适应本能-爆头", 0, 1, 45, "CHAMELEON_HEAD", list);
                            SkillAPI.performSkill(att, skill, false);
                        } else {
                            if (System.currentTimeMillis() - EntityInf.getLastSkillAffect(att, "CHAMELEON_HEAD") > 45000) {
                                riseAPI.addBuffStack(att, BuffStack.StackType.CHAMELEON_HEAD, 1);
                            }
                        }
                    } else {
                        int body = attState.getStackNum(BuffStack.StackType.CHAMELEON_BODY);
//                        tp.sendMessage("BODY:"+body);
                        if (body == 29) {
                            riseAPI.addBuffStack(att, BuffStack.StackType.CHAMELEON_BODY, -29);
                            List<EffectBase> list = new LinkedList<>();
                            list.add(new EffectStack(BuffStack.StackType.CHAMELEON_BODY_BUFF, 1, TargetBase.SELF));
                            SkillBase skill = new SkillBase("适应本能-躯干", 0, 1, 45, "CHAMELEON_BODY", list);
                            SkillAPI.performSkill(att, skill, false);
                        } else {
                            if (System.currentTimeMillis() - EntityInf.getLastSkillAffect(att, "CHAMELEON_BODY") > 45000) {
                                riseAPI.addBuffStack(att, BuffStack.StackType.CHAMELEON_BODY, 1);
                            }
                        }
                    }
                    break;
                }
                case CRAFTSMAN: {
                    riseAPI.addBuffStack(att, BuffStack.StackType.CRAFTSMAN, -1);
                    break;
                }
                case RISK: {
                    if (!(att instanceof Player)) break;
                    if (ifKilled) {
                        double val = 0.1;
                        if (attState.hasType("无尽锻造")) val *= 2;
                        riseAPI.addBuffStack(att, BuffStack.StackType.RISK, -20);
                        SkillBase skill = new SkillBase("铤而走险", 0, 1, 0, "RISK", Arrays.asList(new EffectRecover(true, 0.1, TargetBase.SELF)));
                        SkillAPI.performSkill(att, skill, false);
                    }
                    if (attState.hasType("无尽锻造")) {
                        int num = attState.getStackNum(BuffStack.StackType.RISK);
                        if (num >= 4) {
                            riseAPI.addBuffStack(att, BuffStack.StackType.RISK, -4);
                            damage *= 2.5;
                            event.setDamage(damage);
                            fd = event.getFinalDamage();
                        }
                    }
                    if (!ifDodged && attState.hasTalent(TalentType.RISK_TRANSFORM)) {
                        riseAPI.addExHp((Player) att, fd * 0.1, 30);
                    }
                    break;
                }
                case PATHFINDER: {
                    if (attState.getTotalExHp() > 0.1) {
                        damage *= 1.2;
                    }
                    break;
                }
                case NOMAD: {
                    if (!ifDodged) {
                        double x = Math.random();
                        if (x <= 0.25) {
                            SkillBase skill = new SkillBase("牧羊人", 0, 1, 10, "NOMAD", Arrays.asList(new EffectAttr(AttrModifier.Attr.HP_REGEN, 3, att.getMaxHealth() * 0.1 / 3, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                            SkillAPI.performSkill(att, skill, false);
                        }
                    }
                    break;
                }
            }
        }
        event.setDamage(damage);
        fd = event.getFinalDamage();
        if (def.getWorld() == att.getWorld() && def instanceof Player && ConstantEffect.usingShield.contains(def.getUniqueId()))//在使用护盾的玩家
        {
            double hp = ConstantEffect.ShieldHealth.get(def.getUniqueId());
            ActiveBase now = null;
            for (ActiveBase i : ConstantEffect.constant.get(def.getUniqueId())) {
                if (i.type.name().contains("SHIELD")) {
                    now = (ActiveBase) i;
                    break;
                }
            }
            double mod = 1.0 + defState.skillLevel * 0.3;
            double max = 1;
            double cd = 0;
            switch (now.type) {
                case SHIELD_COVERED: {
                    ShieldCovered tt = (ShieldCovered) now;
                    max = tt.maxHealth * mod * (1.0 + 0.3 * defState.hp / 50);
                    hp -= Math.max(0, bfDamage - tt.armor * mod);
                    cd = tt.cd * (1.0 - defState.skillLevel * 0.05);
                    break;
                }
                case SHIELD_CRUSADERS: {
                    ShieldCrusaders tt = (ShieldCrusaders) now;
                    max = tt.maxHealth * mod * (1.0 + 0.3 * defState.hp / 50);
                    hp -= Math.max(0, bfDamage - tt.armor * mod);
                    cd = tt.cd * (1.0 - defState.skillLevel * 0.05);
                    break;
                }
            }
            BossBar bar = ConstantEffect.shieldGUI.get(def.getUniqueId());
            ((Player) def).playSound(def.getEyeLocation(), "modularwarfare:impact.metal", 16, 1);
            if (hp > 0) {
                ConstantEffect.ShieldHealth.put(def.getUniqueId(), hp);
                bar.setProgress(hp / max);
                if (hp / max <= 0.3) bar.setColor(BarColor.RED);
                else bar.setColor(BarColor.WHITE);
                ConstantEffect.lastShieldDamaged.put(def.getUniqueId(), System.currentTimeMillis());
            } else {
                List<ActiveBase> list = ConstantEffect.constant.get(def.getUniqueId());
                list.remove(now);
                ConstantEffect.constant.put(def.getUniqueId(), list);
                ConstantEffect.usingShield.remove(def.getUniqueId());
                ConstantEffect.ShieldHealth.remove(def.getUniqueId());
                bar.removePlayer((Player) def);
                SkillBase skill = new SkillBase("盾牌冷却", cd, "SHIELD", 0, 1, 0, "SHIELD", null, new EnableEffectBase("§f[§6ISAAC§f]§c盾牌遭到损坏！", null, null, "modularwarfare:effect.shield_break"));
                SkillAPI.performSkill(def, skill, true);
                PotionEffect p1 = new PotionEffect(PotionEffectType.getById(66), 20, 1);
                PotionEffect p2 = new PotionEffect(PotionEffectType.getById(2), 20, 5);
                def.addPotionEffects(Arrays.asList(p1, p2));
            }
            event.setCancelled(true);
            damage = 0;
            event.setDamage(damage);
            return;
        }
        for (TalentType i : defState.activeTalent) {
            switch (i) {
                case COURAGE: {
                    if (!ifDodged && !ifKilled) {
                        double d = 30, val = 0.2;
                        if (defState.activeTalent.contains(TalentType.COURAGE_FIX)) val = 0.3;
                        if (defState.activeTalent.contains(TalentType.COURAGE_REFINE)) d = 20;
                        SkillBase skill = new SkillBase("鼓起勇气", 0, 1, 0, "COURAGE", Arrays.asList(new EffectAttr(AttrModifier.Attr.HP_REGEN, d, fd * val / d, AttrModifier.ModType.PLUS, false, TargetBase.SELF)));
                        SkillAPI.performSkill(def, skill, false);
                    }
                    break;
                }
                case UNBREAKABLE: {
                    if (ifKilled) {
                        SkillBase skill = new SkillBase("牢不可破-检测", 60, "UNBREAKABLE", 0, 1, 0, "UNBREAKABLE", null, null);
                        boolean sec = SkillAPI.performSkill(def, skill, false);
                        if (sec) {
                            event.setCancelled(true);
                            ifKilled = false;
                            damage = 0;
                            def.setHealth(def.getMaxHealth() * 0.95);
                        }
                    }
                    break;
                }
                case UNBREAKABLE_PERFECT: {
                    if (ifKilled) {
                        SkillBase skill = new SkillBase("完美牢不可破-检测", 50, "UNBREAKABLE", 0, 1, 0, "UNBREAKABLE", null, null);
                        boolean sec = SkillAPI.performSkill(def, skill, false);
                        if (sec) {
                            event.setCancelled(true);
                            ifKilled = false;
                            damage = 0;
                            def.setHealth(def.getMaxHealth() * 1);
                        }
                    }
                    break;
                }

            }
        }
        event.setDamage(damage);
        fd = event.getFinalDamage();
//        tp.sendMessage("fd:"+fd+" damage:"+damage);
        if (fd >= def.getHealth()) {
            ifKilled = true;
        }
        if (ifKilled && def instanceof Player)//对重创保护和濒死的操作
        {
            Player player = (Player) def;
            defState = EntityInf.getPlayerState(player);
//            tp.sendMessage(""+defState.downed);
            if (!defState.downed) {
                if (System.currentTimeMillis() - EntityInf.getLastProtect(player) > 60000) {
                    player.sendTitle("§c设备受损", "§f[§6ISAAC§f]已启用重创保护", 10, 20, 10);
                    EntityInf.lastProtect.put(player.getUniqueId(), System.currentTimeMillis());
                    if (riseA.downedSound != null) player.playSound(player.getLocation(), riseA.downedSound, 16, 1);
                    if (riseA.protectSound != null) player.playSound(player.getLocation(), riseA.protectSound, 16, 1);
                    event.setDamage(0);
                    player.setHealth(1);
                } else {
                    if (System.currentTimeMillis() - EntityInf.getLastRevive(player) > 20000) {
                        player.sendTitle("§4遭到重创", "§f[§6ISAAC§f]急需医疗协助", 10, 40, 10);
                        if (riseA.needReviveSound != null)
                            player.getWorld().playSound(player.getLocation(), riseA.needReviveSound, 64, 1);
                        if (riseA.downedSound != null) player.playSound(player.getLocation(), riseA.downedSound, 16, 1);
                        for (Entity i : player.getNearbyEntities(20, 20, 20)) {
                            if (i instanceof Player) {
                                Player tmp = (Player) i;
                                tmp.sendMessage("§f[§6ISAAC§f]特工 " + player.getDisplayName() + "§f 急需医疗协助！");
                            }
                        }
                        riseAPI.setPlayerDowned(player);
                        player.setHealth(player.getMaxHealth());
                        event.setDamage(0);
                    }
                }
            }
        }
        long time = System.currentTimeMillis();
        if (!ifDodged) {
            EntityInf.setLastAttack(att);
            EntityInf.setLastAttackAmount(att, damage);
            EntityInf.setLastDamaged(def);
            if (ifCrit) EntityInf.setLastCrit(att);
            if (ifHeadshot) EntityInf.setLastHeadshot(att);
        } else {
            EntityInf.setLastDodge(def);
        }
        if (ifKilled) {
            EntityInf.setLastKilled(att);
            EntityInf.killEvent(att);
            EntityInf.entityStack.remove(def.getUniqueId());
            EntityInf.entityModifier.remove(def.getUniqueId());
            att.setHealth(Math.min(att.getHealth() + att.getMaxHealth() * attState.onKillRegen / 100, att.getMaxHealth()));
            if (attState.onKillRegen > 0) world.spawnParticle(Particles.getRecoverParticle(), att.getLocation(), 3);
            if (def instanceof Player) {
                riseAPI.resetPlayerAttr((Player) def, true);
            }
            if (att instanceof Player) {
                Player player = (Player) att;
                player.playSound(player.getLocation(), riseA.killedSound, 64, 1);
            }
        }
        if (ifIndirect)
            EntityInf.setLastSkillAttack(Objects.requireNonNull(EntityInf.getOriginAttacker(att.getUniqueId())));
        def.setNoDamageTicks(0);
        att.setNoDamageTicks(0);
        if (def instanceof Player) {
            exhpGUI.barCheck((Player) def);
        }
        if (att instanceof Player) {
            exhpGUI.barCheck((Player) att);
        }
    }

    @EventHandler
    public void playerExecution(KeyBoardPressEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().startsWith("pvp")) return;
        int key = event.getKey();
        if (MinecraftKeys.KEY_TAB.isTheKey(key) && event.getEventKeyState()) {
            List<Player> list = new LinkedList<>();
            for (Entity i : player.getNearbyEntities(1, 1, 1)) {
                if (i instanceof Player && !i.isDead()) {
                    RAstate state = EntityInf.getPlayerState(i.getUniqueId());
                    if (state.downed) {
                        list.add((Player) i);
                    }
                }
            }
            player.sendMessage("§4[§f叛变协议§4]§c已处决玩家 " + list.get(0).getDisplayName() + " §c！");
            player.playSound(player.getLocation(), riseA.killedSound, 16, 1);
            list.get(0).setHealth(0);
            PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 20, 3);
            effect.apply(player);

        }
    }
}
