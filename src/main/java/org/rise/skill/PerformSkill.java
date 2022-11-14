package org.rise.skill;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.rise.EntityInf;
import org.rise.State.AttrModifier;
import org.rise.State.RAstate;
import org.rise.riseA;
import org.rise.riseAPI;
import org.rise.skill.Effect.*;
import org.rise.talent.TalentType;
import org.rise.team.TeamBase;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PerformSkill implements Runnable {
    public World world;
    public Location loc;
    public LivingEntity entity;
    public String name;
    public int delay;
    public int times;
    public List<EffectBase> effect;

    private double pow(double x) {
        return x * x;
    }

    private double getDis(Vector x) {
        return Math.sqrt(pow(x.getX()) + 0 + pow(x.getZ()));
    }

    @Override
    public void run() {
        for (int time = 0; time < times; time++) {
            Player tp = Bukkit.getPlayer("Tech635");
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (effect == null) return;
            for (EffectBase i : effect) {
                List<LivingEntity> target = new LinkedList<>();
                if (i.target.type == TargetBase.Type.SELF && entity != null) target.add(entity);
                if (i.target.type == TargetBase.Type.TEAM && entity instanceof Player) {
                    if (TeamBase.getNowTeam((Player) entity) != null) {
                        for (UUID id : TeamBase.teamInfo.get(TeamBase.getNowTeam((Player) entity))) {
                            target.add(Bukkit.getPlayer(id));
                        }
                    }
                }
                if (i.target.type == TargetBase.Type.AROUND) {
                    for (Entity j : world.getNearbyEntities(loc, i.target.length, i.target.length, i.target.length)) {
                        if (j instanceof LivingEntity) {
                            List<NpcType> et = new LinkedList<>();
                            if (j instanceof Player && j == entity) {
                                et.add(NpcType.PLAYER);
                            } else if (entity instanceof Player) {
                                et = riseAPI.getEntityType((Player) entity, j);
                            } else et = riseAPI.getEntityType(j);
//                            tp.sendMessage(et.toString());
                            boolean s1 = true;
                            //黑白名单逻辑:
                            //npc标签没有白名单中的就不打
                            //npc标签有黑名单中的就不打
                            //优先级相同
                            //白名单可为空，表示任何都打
                            if (!i.target.whitelist.isEmpty()) {
                                boolean ss = false;
                                for (NpcType s : et) {
                                    if (i.target.whitelist.contains(s)) {
                                        ss = true;
                                        break;
                                    }
                                }
                                if (!ss) s1 = false;
                            }
                            if (!i.target.blackList.isEmpty()) for (NpcType s : i.target.blackList) {
                                if (et.contains(s)) {
                                    s1 = false;
                                    break;
                                }
                            }
                            if (!s1) {
                                continue;
                            }
                            if (i.target.angle != null && !i.target.angle.isEmpty()) {
                                boolean sec = false;
                                Location l = j.getLocation();
                                Vector d = loc.getDirection();
                                Vector t = new Vector(l.getX() - loc.getX(), 0, l.getZ() - loc.getZ());//角度不考虑y轴
                                double c = (d.getX() * t.getX() + 0 + d.getZ() * t.getZ()) / getDis(t);
                                c = Math.acos(c);
                                c = Math.toDegrees(c);
//                                tp.sendMessage(j.getCustomName()+":"+c);
                                for (Pair<Integer, Integer> k : i.target.angle) {
                                    if (c >= k.getLeft() && c <= k.getRight()) {
                                        sec = true;
                                        break;
                                    }
                                }
                                if (!sec) continue;
                            }
                            target.add((LivingEntity) j);
                            if (target.size() == i.target.num) break;
                        }
                    }
                }
                RAstate state = new RAstate();
                state.AllDefault();
                state = state.applyModifier(entity);
                if (entity != null) {
                    if (entity instanceof Player) state = EntityInf.getPlayerState((Player) entity);
                    else {
                        state.init(entity);
                        riseAPI.reAnalyseLores(state, entity.getHealth() / entity.getMaxHealth());
                        state = state.applyModifier(entity);
                    }
                }
                switch (i.type) {
                    case POTION: {
                        EffectPotion e = (EffectPotion) i;
                        int duration;
                        if (!e.aggressive) duration = (int) (e.duration * 20);
                        else duration = (int) (e.duration * 20 * (1.0 - state.debuffResistance / 100));
                        for (LivingEntity j : target) {
                            RAstate def = new RAstate();
                            if (j instanceof Player) def = EntityInf.getPlayerState(j.getUniqueId());
                            else {
                                def.AllDefault();
                                def.init(j);
                            }
                            def = def.applyModifier(j);
//                            tp.sendMessage(""+e.id.getId());
//                            tp.sendMessage("dur:"+duration);
                            if (e.id.equals(PotionEffectType.GLOWING)) {
                                duration = (int) (1.0 * duration * Math.max(0, (100 - def.pulseResistance) / 100.0));
                                if (j.getWorld().getName().startsWith("pvp")) duration /= 2;
                            }
//                            tp.sendMessage("dur:"+duration);
                            PotionEffect tmp = new PotionEffect(e.id, duration, e.level[(int) state.skillLevel]);
                            tmp.apply(j);
                        }
                        break;
                    }
                    case ATTR: {
                        EffectAttr e = (EffectAttr) i;
                        double duration;
                        if (!e.aggressive) duration = (e.duration);
                        else duration = e.duration * (1.0 - state.debuffResistance / 100);
                        double val = e.val;
                        for (LivingEntity j : target) {
                            RAstate def = new RAstate();
                            def.AllDefault();
                            if (j != null) {
                                if (j instanceof Player) def = EntityInf.getPlayerState((Player) j);
                                else {
                                    def.init(j);
                                    riseAPI.reAnalyseLores(def, j.getHealth() / j.getMaxHealth());
                                }
                            }
                            def = def.applyModifier(j);
                            if (e.aggressive) {
                                val *= 1.0 - def.debuffResistance / 100;
                                val *= state.debuffEffect;
                            } else val *= state.recoverEffect;
                            if (j != null)
                                riseAPI.addAttrMod(j, new AttrModifier(val, e.modType, e.id, (long) (duration * 1000L)));
                        }
                        break;
                    }
                    case EXHP: {
                        EffectExHp e = (EffectExHp) i;
                        for (LivingEntity j : target) {
                            if (!(j instanceof Player)) continue;
                            double val = e.val * state.recoverEffect;
                            double duration = e.duration;
                            riseAPI.addExHp((Player) j, val, (long) duration * 1000L);
                        }
                        break;
                    }
                    case RECOVER: {
                        EffectRecover e = (EffectRecover) i;
                        for (LivingEntity j : target) {
                            if (j.isDead()) continue;
                            double val = e.val * state.recoverEffect;
                            if (!e.isPercent)
                                j.setHealth(Math.min(j.getHealth() + val, j.getMaxHealth()));
                            else j.setHealth(Math.min(j.getHealth() + val * j.getMaxHealth(), j.getMaxHealth()));
                        }
                        break;
                    }
                    case REVIVE: {
                        for (LivingEntity j : target) {
                            if (j instanceof Player) {
                                RAstate s1 = EntityInf.getPlayerState((Player) j);
                                if (!s1.downed) continue;
                                riseAPI.setPlayerRevived((Player) j, entity);
                            }
                        }
                        break;
                    }
                    case SKILL: {
                        EffectSkill e = (EffectSkill) i;
                        SkillBase s = riseA.skills.get(e.id);
                        for (LivingEntity j : target) {
                            if (j.isDead()) continue;
                            SkillAPI.performSkill(j, s, false);
                        }

                        break;
                    }
                    case STACK: {
                        EffectStack e = (EffectStack) i;
                        for (LivingEntity j : target) {
                            if (j.isDead()) continue;
                            riseAPI.addBuffStack(j, e.id, e.val);
                        }
                        break;
                    }
                    case DAMAGE: {
                        EffectDamage e = (EffectDamage) i;
                        Location tmp = new Location(world, loc.getX(), loc.getY() + 200, loc.getZ());
                        LivingEntity a = (LivingEntity) riseA.tmpWorld.spawnEntity(tmp, EntityType.WITHER_SKELETON);
                        EntityEquipment equip = a.getEquipment();
                        ItemStack item = new ItemStack(Material.WOOD_AXE, 1);
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName("§7技能伤害" + entity.getUniqueId());
                        List<String> lore = new LinkedList<>();
                        double val = 1.0, vd = 0;
                        if (e.target.type != TargetBase.Type.SELF) {
                            vd = e.state.damage * (1.0 + e.increase.damage * state.skillLevel / 100) * state.skillDamage;
//                            tp.sendMessage("s:"+state.skillDamage+" "+vd);
                        } else vd = e.state.damage;
//                        tp.sendMessage("sss:"+vd);

                        lore.add(riseA.damageS + vd);
                        val = e.state.hit * (1.0 + e.increase.hit * state.skillLevel / 100);
                        lore.add(riseA.hitRateS + val);
                        val = e.state.critChance * (1.0 + e.increase.critChance * state.skillLevel / 100);
                        lore.add(riseA.critChanceS + val);
                        val = e.state.critRate * (1.0 + e.increase.critRate * state.skillLevel / 100);
                        lore.add(riseA.critRateS + val);
                        val = e.state.physicalPiercing * (1.0 + e.increase.physicalPiercing * state.skillLevel / 100);
                        lore.add(riseA.physicalPiercingS + val);
                        val = e.state.trueDamage * (1.0 + e.increase.trueDamage * state.skillLevel / 100);
                        lore.add(riseA.trueDamageS + val);
                        val = e.state.percentDamage * (1.0 + e.increase.percentDamage * state.skillLevel / 100);
                        lore.add(riseA.percentDamageS + val);
                        if (state.activeTalent.contains(TalentType.SYNCHRO)) {
                            lore.add(riseA.talentS);
                            lore.add(riseA.talentMapReflect.get(TalentType.SYNCHRO));
                        }
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        equip.setItemInMainHand(item);
                        a.setCustomName(name);
                        if (entity != null)
                            EntityInf.createIndirectDamage(a, entity);
                        for (LivingEntity j : target) {
                            if (j != null) {
                                j.damage(vd, a);
                            }
                        }
                        a.remove();
                        break;
                    }
                    case CUSTOMEFFECT: {
                        EffectCustomEffect e = (EffectCustomEffect) i;
                        for (LivingEntity j : target) {
                            RAstate def = new RAstate();
                            def.AllDefault();
                            def.init(j);
                            def = def.applyModifier(j);
                            double d = e.length * (1.0 - def.debuffResistance / 100.0);
                            riseAPI.addEffect(state, j, e.effectType, state.debuffEffect * (1.0 + 0.3 * state.skillLevel), e.level, d);
                        }
                        break;
                    }
                }
            }
        }
    }

    public PerformSkill(SkillBase base, World w, Location l, LivingEntity e) {
        world = w;
        loc = l;
        entity = e;
        name = base.name;
        delay = base.delay;
        times = base.times;
        effect = base.effect;
    }
}

