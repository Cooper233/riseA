package org.rise;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.rise.State.AttrModifier;
import org.rise.State.BuffStack;
import org.rise.State.RAstate;
import org.rise.activeSkills.ConstantEffect;
import org.rise.effect.CustomEffect;
import org.rise.effect.CustomEffectBase;
import org.rise.skill.NpcType;
import org.rise.team.TeamBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class riseAPI implements Listener {
    public static void reAnalyseLores(RAstate state, double healthP)//已加载lore，进行分析
    {
        if (state.lores.isEmpty() || (state.lores.size() == 1 && Objects.equals(state.lores.get(0), ""))) return;
        state.setDefault();
        List<String> newLores = new LinkedList<>();
        //用过的词条记得标记为true，然后判定一次跳过
        //高等级检测，用来识别套装，类型之类的
        //叠加的优先级低于套装
        //类型加成只会吃到一次
        //去除所有开头为§7的lore（不解析的描述用语句）
        Player tp = Bukkit.getPlayer("Tech635");
//        tp.sendMessage("1");
        for (int i = 0; i < state.lores.size(); i++) {
            String tmp = state.lores.get(i);
            if (tmp.contains(riseA.suitMarkS)) {
                for (String j : riseA.suitMap.keySet()) {
                    if (tmp.contains(j)) {
                        if (state.suit.containsKey(j)) {
                            state.suit.put(j, state.suit.get(j) + 1);
                        } else state.suit.put(j, 1);
                        break;

                    }
                }
            } else if (tmp.contains(riseA.typeMarkS)) {
                tmp = tmp.replaceAll(riseA.typeMarkS, "");
                if (riseA.reflexTypeMap.containsKey(tmp)) {
                    state.type.put(riseA.reflexTypeMap.get(tmp), true);
                }
            } else {
                if (!tmp.startsWith("§7")) newLores.add(tmp);
            }
        }
        //加入套装lore
//        tp.sendMessage("2");
        for (String i : state.suit.keySet()) {
            int amont = state.suit.get(i);
            for (riseA.suitEffect j : riseA.suitMap.get(i)) {
                if (j.amont <= amont) {
                    newLores.addAll(j.lores);
                }
            }
        }
        state.lores.clear();
        state.lores.addAll(newLores);
        newLores.clear();
        //次高级，对类型加成/血量
//        tp.sendMessage("3");
        for (String tmp : state.lores) {
            if (tmp.contains(riseA.typeBuffS)) {
                tmp = tmp.replaceAll(riseA.typeBuffS, "");
                for (String i : state.type.keySet()) {
                    if (tmp.contains(i)) {
                        tmp = tmp.replaceAll(i, "");
                        newLores.add(tmp);
                    }
                }
            } else if (tmp.contains(riseA.healthBuffS)) {
                if (tmp.contains("|")) {
                    String front = tmp.substring(0, tmp.indexOf("|"));
                    String back = tmp.substring(tmp.indexOf("|") + 1, tmp.length());
                    front = front.replaceAll("§[0-9]", "§f");
                    double res = 0;
                    Pattern p = Pattern.compile("[0-9]+");
                    Matcher m = p.matcher(front);
                    if (m.find()) {
                        res = Double.parseDouble(m.group());
                    }
                    if (front.contains(">")) {
                        if (healthP * 100 > res) newLores.add(back);
                    }
                    if (front.contains("<")) {
                        if (healthP * 100 < res) newLores.add(back);
                    }
                }
            } else {
                newLores.add(tmp);
            }
        }
        state.lores.clear();
        state.lores.addAll(newLores);
        newLores.clear();
        //中等级的检测，用来加药水效果，天赋之类的
//        tp.sendMessage("4");
        for (int i = 0; i < state.lores.size(); i++) {
            String tmp = state.lores.get(i);
            if (tmp.contains(riseA.buffGiverS)) {
                Integer res = 0;
                tmp = tmp.replaceAll("§[0-9]", "§f");
                Pattern p = Pattern.compile("[0-9]+");
                Matcher m = p.matcher(tmp);
                if (m.find()) {
                    res = Integer.parseInt(m.group());
                }
                if (res == 0) continue;
                for (String j : riseA.buffMap.keySet()) {
                    if (tmp.contains(j)) {
                        if (!state.potions.containsKey(riseA.buffMap.get(j)) || res > state.potions.get(riseA.buffMap.get(j))) {
                            state.potions.put(riseA.buffMap.get(j), res);
                        }
                        break;
                    }
                }
            } else if (tmp.contains(riseA.talentS)) {
                String s1 = state.lores.get(i + 1);
                s1 = s1.replaceAll("§6\\[§f§l", "");
                s1 = s1.replaceAll("§6\\]", "");
                s1 = s1.replaceAll("§c\\[§f§l", "");
                s1 = s1.replaceAll("§c\\]", "");
                s1 = s1.replaceAll("§2\\[§f§l", "");
                s1 = s1.replaceAll("§2\\]", "");
                if (riseA.talentMap.containsKey(s1)) {
                    state.activeTalent.add(riseA.talentMap.get(s1));
                }
            } else {
                newLores.add(tmp);
            }
        }
        state.lores.clear();
        state.lores.addAll(newLores);
        newLores.clear();
        RAstate cg = new RAstate();//过量词条
        cg.setDefault();
        //最次级的检测，前面放套装啊天赋啊叠加啊之类的
        //应该需要单独写一个函数用来剔除高级词条并转化为普通的
        //检测到§6§c就直接判定为减属性
        //因为不会写正则表达式所以叫他们不要给基础词条关键词加颜色符号就好了
        //超越标签也放在这里检测
        //套装的lore在这里加入常规lore表
//        tp.sendMessage("5");
        state.critRate = riseA.critRateDefault;
        state.headshotRate = riseA.headshotRateDefault;
        state.hit = riseA.hitRateDefault;
        for (int i = 0; i < state.lores.size(); i++) {
            String tmp = state.lores.get(i);
            boolean reduce = false;
            boolean over = false;
            double res = 0;
            if (tmp.contains("§6§c")) reduce = true;
            if (tmp.contains(riseA.overChargeS)) over = true;
            tmp = tmp.replaceAll("§[0-9]", "§f");
            Pattern p = Pattern.compile("[0-9]+(\\.[0-9]+)?");
            Matcher m = p.matcher(tmp);
            if (m.find()) res = Float.parseFloat(m.group());//只加第一次出现的浮点数
            if (reduce) res = -res;
            if (tmp.contains(riseA.critChanceS)) {
                if (over) cg.critChance += res;
                else state.critChance += res;
            } else if (tmp.contains(riseA.critRateS)) {
                if (over) cg.critRate += res;
                else state.critRate += res;
            } else if (tmp.contains(riseA.headshotRateS)) {
                if (over) cg.headshotRate += res;
                else state.headshotRate += res;
            } else if (tmp.contains(riseA.nonHeadshotS)) {
                state.nonHeadshot = true;
            } else if (tmp.contains(riseA.damageS)) {
                if (over) cg.damage += res;
                else state.damage += res;
            } else if (tmp.contains(riseA.finalDamageS)) {
                if (over) cg.finalDamage += res;
                else state.finalDamage += res;
            } else if (tmp.contains(riseA.trueDamageS)) {
                if (over) cg.trueDamage += res;
                else state.trueDamage += res;
            } else if (tmp.contains(riseA.hpS)) {
                if (over) cg.hp += res;
                else state.hp += res;
            } else if (tmp.contains(riseA.hpRegenS)) {
                if (over) cg.hpRegen += res;
                else state.hpRegen += res;
            } else if (tmp.contains(riseA.percentHpS)) {
                if (over) cg.percentHp += res;
                else state.percentHp += res;
            } else if (tmp.contains(riseA.specialResistanceS)) {
                if (over) cg.specialResistance += res;
                else state.specialResistance += res;
            } else if (tmp.contains(riseA.physicalResistanceS)) {
                if (over) cg.physicalResistance += res;
                else state.physicalResistance += res;
            } else if (tmp.contains(riseA.physicalPiercingS)) {
                if (over) cg.physicalPiercing += res;
                else state.physicalPiercing += res;
            } else if (tmp.contains(riseA.avoidRateS)) {
                if (over) cg.avoid += res;
                else state.avoid += res;
            } else if (tmp.contains(riseA.hitRateS)) {
                if (over) cg.hit += res;
                else state.hit += res;
            } else if (tmp.contains(riseA.speedS)) {
                if (over) cg.speed += res;
                else state.speed += res;
            } else if (tmp.contains(riseA.expBounceS)) {
                if (over) cg.expBounce += res;
                else state.expBounce += res;
            } else if (tmp.contains(riseA.onKillRegenS)) {
                if (over) cg.onKillRegen += res;
                else state.onKillRegen += res;
            } else if (tmp.contains(riseA.nfAbilityS)) {
                if (over) cg.nfAbility += res;
                else state.nfAbility += res;
            } else if (tmp.contains(riseA.debuffResistanceS)) {
                if (over) cg.debuffResistance += res;
                else state.debuffResistance += res;
            } else if (tmp.contains(riseA.percentDamageS)) {
                if (over) cg.percentDamage += res;
                else state.percentDamage += res;
            } else if (tmp.contains(riseA.skillLevelS)) {
                if (over) cg.skillLevel += res;
                else state.skillLevel += res;
            } else if (tmp.contains(riseA.skillDamageS)) {
                if (over) cg.skillDamage += res / 100;
                else state.skillDamage += res / 100;
            } else if (tmp.contains(riseA.debuffEffectS)) {
                if (over) cg.debuffEffect += res / 100;
                else state.debuffEffect += res / 100;
            } else if (tmp.contains(riseA.recoverEffectS)) {
                if (over) cg.recoverEffect += res / 100;
                else state.recoverEffect += res / 100;
            } else if (tmp.contains(riseA.skillAccelerateS)) {
                if (over) cg.skillAccelerate += res / 100;
                else state.skillAccelerate += res / 100;
            } else if (tmp.contains(riseA.pulseResistanceS)) {
                if (over) cg.pulseResistance += res;
                else state.pulseResistance += res;
            } else {
                continue;
            }
        }
        cg.safeCheck();
        state.safeCheck();
        state.maxCheck();
        state.add(cg);
        state.lores.clear();
    }


    public static List<String> getItemType(ItemStack item) {
        if (item == null) return null;
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return null;
        List<String> lore = item.getItemMeta().getLore();
        List<String> res = new LinkedList<>();
        for (String tmp : lore) {
            if (tmp.contains(riseA.typeMarkS)) {
                tmp = tmp.replaceAll(riseA.typeMarkS, "");
                res.add(riseA.reflexTypeMap.get(tmp));
            }
        }
        return res;
    }

    public static void resetPlayerAttr(Player player) {
        RAstate state;
        if (EntityInf.playersAttr.containsKey(player.getUniqueId()))
            state = EntityInf.playersAttr.get(player.getUniqueId());
        else state = new RAstate();
        state.setDefault();
        state.init(player);
        reAnalyseLores(state, player.getHealth() / player.getMaxHealth());
        EntityInf.playersAttr.put(player.getUniqueId(), state);
    }

    public static void resetPlayerAttr(Player player, boolean alldefault) {
        RAstate state;
        if (EntityInf.playersAttr.containsKey(player.getUniqueId()))
            state = EntityInf.playersAttr.get(player.getUniqueId());
        else state = new RAstate();
        if (alldefault) state.AllDefault();
        state.setDefault();
        state.init(player);
        reAnalyseLores(state, player.getHealth() / player.getMaxHealth());
        EntityInf.playersAttr.put(player.getUniqueId(), state);
    }

    public static void addAttrMod(LivingEntity entity, AttrModifier modifier) {
        if (entity.isDead()) return;
        if (entity instanceof Player) {
            RAstate tmp = EntityInf.getPlayerState(entity.getUniqueId());
            assert tmp != null;
            tmp.addAttrModifier(modifier);
            EntityInf.playersAttr.put(entity.getUniqueId(), tmp);
        } else {
            List<AttrModifier> tmp = null;
            if (EntityInf.entityModifier.containsKey(entity.getUniqueId()))
                tmp = EntityInf.entityModifier.get(entity.getUniqueId());
            if (tmp == null) tmp = new LinkedList<>();
            int l = 0, r = tmp.size() - 1, ans = -1;
            while (l <= r) {
                int mid = ((l + r) >> 1);
                if (tmp.get(mid).disappear < modifier.disappear) {
                    ans = mid;
                    l = mid + 1;
                } else r = mid - 1;
            }
            tmp.add(ans + 1, modifier);
            EntityInf.entityModifier.put(entity.getUniqueId(), tmp);
        }
    }

    public static void addExHp(Player player, double hp, long length)//length以毫秒为单位
    {
        RAstate state = EntityInf.getPlayerState(player);
        state.addExHp(new RAstate.extraHp(hp, length));
        EntityInf.playersAttr.put(player.getUniqueId(), state);
    }

    public static void addBuffStack(LivingEntity entity, BuffStack.StackType tar, int val) {
        if (entity instanceof Player) {
            RAstate state = EntityInf.getPlayerState((Player) entity);
            int bef = state.getStackNum(tar);
            state.addBuffStack(tar, val);
            int aft = state.getStackNum(tar);
            EntityInf.playersAttr.put(entity.getUniqueId(), state);
            switch (tar) {
                case RISK: {
                    if (aft > bef) {
                        addExHp((Player) entity, 30, 40000);
                    }
                    break;
                }
            }
        } else {
            Map<BuffStack.StackType, Integer> buffStack = new HashMap<>();
            if (EntityInf.entityStack.containsKey(entity.getUniqueId())) {
                buffStack = EntityInf.entityStack.get(entity.getUniqueId());
            }
            RAstate s = new RAstate();
            s.AllDefault();
            s.buffStack = buffStack;
            s.addBuffStack(tar, val);
            EntityInf.entityStack.put(entity.getUniqueId(), s.buffStack);
        }
    }

    public static void setPlayerDowned(Player player) {
        resetPlayerAttr(player, true);
        RAstate state = EntityInf.getPlayerState(player);
        state.downed = true;
        EntityInf.playersAttr.put(player.getUniqueId(), state);
    }

    public static void setPlayerReviving(Player player, LivingEntity reviver) {
        EntityInf.revivingMap.put(reviver.getUniqueId(), player.getUniqueId());
        EntityInf.revivingMapReflect.put(player.getUniqueId(), reviver.getUniqueId());
    }

    public static void pushRevivingProgress(Player res, LivingEntity reviver) {
        if (EntityInf.reviveProgress.containsKey(res.getUniqueId())) {
            int val = EntityInf.reviveProgress.get(res.getUniqueId());
            if (val < 49) EntityInf.reviveProgress.put(res.getUniqueId(), val + 1);
            else {
                riseAPI.setPlayerRevived(res, reviver);
                EntityInf.reviveProgress.put(res.getUniqueId(), 0);
            }
        } else {
            EntityInf.reviveProgress.put(res.getUniqueId(), 1);
        }
        MaterialData data = new MaterialData(Material.STAINED_GLASS);
        data.setData((byte) Integer.parseInt(String.valueOf(5)));
        Location loc = res.getLocation();
        double r = 1.5;
        World world = res.getWorld();
        for (double a = 0; a < 1.0 * 360 * EntityInf.reviveProgress.get(res.getUniqueId()) / 50; a += 5) {
            double rad = Math.toRadians(a);
            loc.add(r * Math.cos(rad), 0, r * Math.sin(rad));
            world.spawnParticle(Particle.FALLING_DUST, loc, 2, 0, 0, 0, 0, data);
            world.spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0, 0, 0, 0, data);
            loc.subtract(r * Math.cos(rad), 0, r * Math.sin(rad));
        }
    }

    public static void setPlayerRevived(Player player, LivingEntity reviver) {

        resetPlayerAttr(player, true);
        RAstate state = EntityInf.getPlayerState(player);
        state.downed = false;
        EntityInf.playersAttr.put(player.getUniqueId(), state);
        String s1 = "§f受到医疗协助。";
        if (reviver instanceof Player) {
            s1 += "协助者：" + ((Player) reviver).getName();
        }
        player.sendTitle(s1, "§f[§6ISAAC§f]已恢复行动能力", 10, 20, 10);
        if (!player.isDead()) {
            player.getWorld().playSound(player.getLocation(), riseA.revivedSound, 15, 1);
            MaterialData data = new MaterialData(Material.STAINED_GLASS);
            data.setData((byte) 5);
            player.getWorld().spawnParticle(Particle.FALLING_DUST, player.getEyeLocation(), 50, 1, 1, 1, 3, data);
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getEyeLocation(), 15, 1, 1, 1, 3, data);
        }
        EntityInf.lastRevive.put(player.getUniqueId(), System.currentTimeMillis());
        EntityInf.revivingMap.remove(reviver.getUniqueId());
        EntityInf.revivingMapReflect.remove(player.getUniqueId());
    }

    public static void setPlayerRevived(Player player) {
        resetPlayerAttr(player, true);
        RAstate state = EntityInf.getPlayerState(player);
        state.downed = false;
        EntityInf.playersAttr.put(player.getUniqueId(), state);
        if (!player.isDead()) {
            player.getWorld().playSound(player.getLocation(), riseA.revivedSound, 15, 1);
            MaterialData data = new MaterialData(Material.STAINED_GLASS);
            data.setData((byte) 5);
            player.getWorld().spawnParticle(Particle.FALLING_DUST, player.getEyeLocation(), 50, 1, 1, 1, 3, data);
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getEyeLocation(), 15, 1, 1, 1, 3, data);
        }
        EntityInf.lastRevive.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public static List<NpcType> getEntityType(org.bukkit.entity.Entity entity) {
        return getEntityType(null, entity);
    }

    public static List<NpcType> getEntityType(Player player, org.bukkit.entity.Entity entity) {
        Player tp = Bukkit.getPlayer("Tech635");
        net.minecraft.server.v1_12_R1.Entity ientity;
        CraftEntity c = (CraftEntity) entity;
        List<NpcType> types = new LinkedList<>();
        Method m = null;
        try {
            m = c.getClass().getMethod("getHandle");
            ientity = (net.minecraft.server.v1_12_R1.Entity) m.invoke(c);
            NBTTagCompound nbt = new NBTTagCompound();
            ientity.save(nbt);

            if (nbt.hasKey("FactionID")) {
                int f = nbt.getInt("FactionID");
                if (riseA.npcFriendly.contains(f)) types.add(NpcType.NPC_FRIEND);
                if (riseA.npcEnemy.contains(f)) types.add(NpcType.NPC_ENEMY);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        for (UUID i : ConstantEffect.platformId.keySet()) {
            if (entity.getUniqueId() == ConstantEffect.platformId.get(i)) {
                types.add(NpcType.NPC_FRIEND);
            }
        }
        if (entity instanceof Player && player != null) {
            if (entity.getWorld().getName().startsWith("pvp")) {
                if (TeamBase.getNowTeam(player) == TeamBase.getNowTeam((Player) entity)) {
                    types.add(NpcType.PLAYER);
                } else types.add(NpcType.PLAYER_ENEMY);
            } else types.add(NpcType.PLAYER);
        } else types.add(NpcType.PLAYER);
        boolean ifElc = false;
        for (String i : riseA.npcElc) {
            if (entity.getCustomName() != null && entity.getCustomName().contains(i)) {
                ifElc = true;
                break;
            }
        }
        if (ifElc) types.add(NpcType.ELC_NPC);
        return types;
    }

    public static void addEffect(RAstate self, LivingEntity tar, CustomEffect type, double modifier, int level, double length) {
        List<CustomEffectBase> list = EntityInf.entityEffect.get(tar.getUniqueId());
        if (list == null) list = new LinkedList<>();
        for (CustomEffectBase base : list) {
            if (base.type == type) return;
        }
        CustomEffectBase now = new CustomEffectBase(type, level, modifier, (long) (length * 1000));
        list.add(now);
        EntityInf.entityEffect.put(tar.getUniqueId(), list);
    }

    public static boolean checkEffect(LivingEntity entity, CustomEffect type) {
        List<CustomEffectBase> list = EntityInf.entityEffect.get(entity.getUniqueId());
        if (list == null) return false;
        for (CustomEffectBase base : list) {
            if (base.type == type && base.start + base.length > System.currentTimeMillis()) return true;
        }
        return false;
    }

    public static Location getEntityAimingLoc(LivingEntity entity) {
        Location eye = entity.getEyeLocation();
//        entity.getLineOfSight()
        return null;
    }
}
