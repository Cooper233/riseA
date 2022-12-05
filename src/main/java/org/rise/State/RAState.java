package org.rise.State;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.rise.EntityInf;
import org.rise.Inventory.ModuleGui;
import org.rise.activeSkills.ConstantEffect;
import org.rise.riseA;
import org.rise.talent.TalentType;
import org.rise.utils.GetNumUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.rise.State.Attr.*;

public class RAState implements Cloneable {
    public List<String> floatingLore = new LinkedList<>();
    public List<String> staticLore = new LinkedList<>();

    public Map<Attr, Double> attrMap = new HashMap<>();
    public boolean downed;
    public boolean applied = false;
    public boolean nonHeadshot = false;

    public RAState() {
        floatingLore = new LinkedList<>();
        staticLore = new LinkedList<>();
        attrMap = new HashMap<>();
        potions = new HashMap<>();
        suit = new HashMap<>();
        type = new HashMap<>();
        activeTalent = new LinkedList<>();
        applied = false;
    }

    @Override
    public RAState clone() {
        RAState clone = new RAState();
        clone.applied = applied;
        for (Attr i : attrMap.keySet()) {
            clone.attrMap.put(i, attrMap.get(i));
        }
        clone.downed = downed;
        clone.floatingLore.addAll(floatingLore);
        clone.staticLore.addAll(staticLore);
        for (PotionEffectType i : potions.keySet())
            clone.potions.put(i, potions.get(i));
        for (String i : suit.keySet())
            clone.suit.put(i, suit.get(i));
        for (String i : type.keySet())
            clone.type.put(i, type.get(i));
        clone.activeTalent = new LinkedList<>(activeTalent);
        clone.applied = applied;
        return clone;
    }

    public void setAttr(Attr tar, double val) {
        attrMap.put(tar, val);
    }

    public double getAttr(Attr tar) {
        return attrMap.getOrDefault(tar, 0.0);
    }

    public void addAttr(Attr tar, double val) {
        attrMap.put(tar, getAttr(tar) + val);
    }

    public void multiAttr(Attr tar, double val) {
        attrMap.put(tar, getAttr(tar) * val);
    }

    public void checkAttrMax(Attr tar, double val) {
        double tmp = getAttr(tar);
        if (tmp > val) setAttr(tar, val);
    }

    public void checkAttrMin(Attr tar, double val) {
        double tmp = getAttr(tar);
        if (tmp < val) setAttr(tar, val);
    }

    public void setSuitNum(String tar, int val) {
        suit.put(tar, val);
    }

    public int getSuitNum(String tar) {
        return suit.getOrDefault(tar, 0);
    }

    public void addSuitNum(String tar) {
        suit.put(tar, getSuitNum(tar) + 1);
    }

    public Map<PotionEffectType, Integer> potions = new HashMap<>();
    public Map<String, Integer> suit = new HashMap<>();
    public Map<String, Boolean> type = new HashMap<>();
    public List<TalentType> activeTalent = new LinkedList<>();

    public void AllDefault() {
        setAttr(CRIT, 0);
        setAttr(CRIT_RATE, 0);
        nonHeadshot = false;
        setAttr(HEADSHOT_RATE, 0);
        setAttr(DAMAGE, 0);
        setAttr(FINAL_DAMAGE, 0);
        setAttr(TRUE_DAMAGE, 0);
        setAttr(HP, 0);
        setAttr(HP_REGEN, 0);
        setAttr(PERCENT_HP, 0);
        setAttr(PHYSICAL_RESISTANCE, 0);
        setAttr(SPECIAL_RESISTANCE, 0);
        setAttr(PHYSICAL_PIERCING, 0);
        setAttr(SPEED, 0);
        setAttr(PERCENT_DAMAGE, 0);
        setAttr(HIT, 0);
        setAttr(AVOID, 0);
        setAttr(EXP_BOUNCE, 0);
        setAttr(ON_KILL_REGEN, 0);
        setAttr(NF_ABILITY, 0);
        setAttr(DEBUFF_RESISTANCE, 0);
        setAttr(DAMAGE_RECEIVE, 0);
        setAttr(SKILL_LEVEL, 0);
        setAttr(SKILL_DAMAGE, 0);
        setAttr(DEBUFF_EFFECT, 0);
        setAttr(RECOVER_EFFECT, 0);
        setAttr(SKILL_ACCELERATE, 0);
        setAttr(PULSE_RESISTANCE, 0);
//        staticLore.clear();
//        floatingLore.clear();
        potions.clear();
        suit.clear();
        type.clear();
        activeTalent.clear();
        applied = false;
    }


    public void setDefault()//不重置临时血量，叠层
    {
        setAttr(CRIT, 0);
        setAttr(CRIT_RATE, 0);
        nonHeadshot = false;
        setAttr(HEADSHOT_RATE, 0);
        setAttr(DAMAGE, 0);
        setAttr(FINAL_DAMAGE, 0);
        setAttr(TRUE_DAMAGE, 0);
        setAttr(HP, 0);
        setAttr(HP_REGEN, 0);
        setAttr(PERCENT_HP, 0);
        setAttr(PHYSICAL_RESISTANCE, 0);
        setAttr(SPECIAL_RESISTANCE, 0);
        setAttr(PHYSICAL_PIERCING, 0);
        setAttr(SPEED, 0);
        setAttr(PERCENT_DAMAGE, 0);
        setAttr(HIT, 0);
        setAttr(AVOID, 0);
        setAttr(EXP_BOUNCE, 0);
        setAttr(ON_KILL_REGEN, 0);
        setAttr(NF_ABILITY, 0);
        setAttr(DEBUFF_RESISTANCE, 0);
        setAttr(DAMAGE_RECEIVE, 0);
        setAttr(SKILL_LEVEL, 0);
        setAttr(SKILL_DAMAGE, 0);
        setAttr(DEBUFF_EFFECT, 0);
        setAttr(RECOVER_EFFECT, 0);
        setAttr(SKILL_ACCELERATE, 0);
        setAttr(PULSE_RESISTANCE, 0);
        potions.clear();
        suit.clear();
        type.clear();
        activeTalent.clear();
        applied = false;

    }

    public void add(RAState a) {
        for (Attr i : a.attrMap.keySet()) {
            addAttr(i, a.attrMap.get(i));
        }
    }



    public void secondlyCheck() {
    }


    public void safeCheck() {
        checkAttrMin(CRIT, 0);
        checkAttrMin(CRIT_RATE, 0);
        checkAttrMin(HEADSHOT_RATE, 0);
        checkAttrMin(PERCENT_HP, -99);
        checkAttrMin(SPEED, -100);
        checkAttrMin(HIT, 0);
        checkAttrMin(AVOID, 0);
        checkAttrMin(EXP_BOUNCE, 0);
        checkAttrMin(ON_KILL_REGEN, 0);
        checkAttrMin(NF_ABILITY, 0);
        checkAttrMin(PULSE_RESISTANCE, 0);
    }

    public void maxCheck() {
        checkAttrMax(CRIT_RATE, riseA.critRateMax);
        checkAttrMax(CRIT, riseA.critChanceMax);
        checkAttrMax(TRUE_DAMAGE, riseA.trueDamageMax);
        checkAttrMax(PHYSICAL_RESISTANCE, riseA.physicalMax);
        checkAttrMax(SPEED, riseA.speedMax);
    }

    public long getEffectiveness()//娱乐用的作战效能
    {
        long res = 0;
        res += getAttr(CRIT) * 30;
        res += getAttr(CRIT_RATE) * 50;
        res += getAttr(HEADSHOT_RATE) * 50;
        res += getAttr(DAMAGE) * 25;
        res += getAttr(FINAL_DAMAGE) * 50;
        res += getAttr(TRUE_DAMAGE) * 80;
        res += getAttr(HP) * 25;
        res += getAttr(HP_REGEN) * 40;
        res += getAttr(PERCENT_HP) * 50;
        res += Math.pow(1.2, getAttr(PHYSICAL_RESISTANCE) + getAttr(SPECIAL_RESISTANCE)) * 17;
        res += getAttr(PHYSICAL_PIERCING) * 50;
        res += getAttr(SPEED) * 10;
        res += Math.pow(1.3, getAttr(PERCENT_DAMAGE)) * 200;
        res += getAttr(HIT) * 25;
        res += getAttr(AVOID) * 30;
        res += getAttr(EXP_BOUNCE) * 10;
        res += getAttr(ON_KILL_REGEN) * 55;
        res += getAttr(NF_ABILITY) * 20;
        res += getAttr(DEBUFF_RESISTANCE) * 30;
        res += (getAttr(DAMAGE_RECEIVE) - 1.0) * 6000;
        res += getAttr(SKILL_LEVEL) * 500;
        res += (getAttr(SKILL_DAMAGE)) * 45;
        res += (getAttr(DEBUFF_EFFECT)) * 45;
        res += (getAttr(RECOVER_EFFECT)) * 25;
        res += (getAttr(SKILL_ACCELERATE)) * 30;
        res += (getAttr(PULSE_RESISTANCE) * 50);
        return res;
    }

    public static boolean check(ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasLore();
    }

    /***
     * 全部重新读取（包括装备和手中物品）
     * @param entity 读取装备的实体
     */
    public void initAll(LivingEntity entity) {
        staticLore.clear();
        floatingLore.clear();
        Player tmp = Bukkit.getPlayer("Tech635");
        boolean done = false;
        EntityEquipment a = entity.getEquipment();
        a.getArmorContents();
        ItemStack s1 = a.getBoots(), s2 = a.getLeggings(), s3 = a.getChestplate(), s4 = a.getHelmet();
        try {
            if (s1 != null && s1.getType() != Material.AIR && s1.hasItemMeta() && s1.getItemMeta().hasLore()) {
                staticLore.addAll(s1.getItemMeta().getLore());
            }
            if (s2 != null && s2.getType() != Material.AIR && s2.hasItemMeta() && s2.getItemMeta().hasLore()) {
                staticLore.addAll(s2.getItemMeta().getLore());
            }
            if (s3 != null && s3.getType() != Material.AIR && s3.hasItemMeta() && s3.getItemMeta().hasLore()) {
                staticLore.addAll(s3.getItemMeta().getLore());
            }
            if (s4 != null && s4.getType() != Material.AIR && s4.hasItemMeta() && s4.getItemMeta().hasLore()) {
                staticLore.addAll(s4.getItemMeta().getLore());
            }
            if (staticLore.size() != 0) done = true;
        } catch (Throwable err) {
            tmp.sendMessage("装备出错" + err.toString());
        }
        try {
            if (a.getItemInMainHand() != null && a.getItemInMainHand().getType() != Material.AIR) {
                if (a.getItemInMainHand().hasItemMeta() && a.getItemInMainHand().getItemMeta().hasLore()) {
                    floatingLore.addAll(entity.getEquipment().getItemInMainHand().getItemMeta().getLore());
                }
            }
            if (a.getItemInOffHand() != null && a.getItemInOffHand().getType() != Material.AIR) {
                if (a.getItemInOffHand().hasItemMeta() && a.getItemInOffHand().getItemMeta().hasLore()) {
                    List<String> list = a.getItemInOffHand().getItemMeta().getLore();
                    for (String i : list) {
                        if (i.contains(riseA.typeMarkS)) {
                            i = i.replaceAll(riseA.typeMarkS, "");
                            if (riseA.secAbleType.contains(i)) {
                                floatingLore.addAll(list);
                            }
                        }
                    }
                }
            }
            if (floatingLore.size() != 0) done = true;
        } catch (Throwable err) {
            tmp.sendMessage("手持物品出错" + err.toString());
        }
        if (entity instanceof Player) {
            staticLore.add(riseA.attrName.get(PULSE_RESISTANCE) + "+30%");
            Player player = (Player) entity;
            if (!ModuleGui.guiList.containsKey(player.getUniqueId())) ModuleGui.guiInit(player, false);
            Inventory inv = ModuleGui.guiList.get(player.getUniqueId());
            if (check(inv.getItem(11))) staticLore.addAll(inv.getItem(11).getItemMeta().getLore());
            if (check(inv.getItem(12))) staticLore.addAll(inv.getItem(12).getItemMeta().getLore());
            if (check(inv.getItem(13))) staticLore.addAll(inv.getItem(13).getItemMeta().getLore());
            for (int i = 0; i < 6; i++) {
                if (check(inv.getItem(26 + i))) staticLore.addAll(inv.getItem(26 + i).getItemMeta().getLore());
            }
            net.minecraft.server.v1_12_R1.Entity iplayer;
            CraftEntity c = (CraftEntity) player;
            try {
                Method m = c.getClass().getMethod("getHandle");
                iplayer = (net.minecraft.server.v1_12_R1.EntityPlayer) m.invoke(c);
                NBTTagCompound nbt = iplayer.save(new NBTTagCompound());
                iplayer.f(nbt);
                if (nbt.hasKey("ForgeCaps") && nbt.getCompound("ForgeCaps").hasKey("modularwarfare:extraslots")) {
                    NBTTagCompound vicinv = nbt.getCompound("ForgeCaps").getCompound("modularwarfare:extraslots");
                    NBTTagList list = vicinv.getList("Items", 10);
                    if (!list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            NBTTagCompound it = list.get(i);
                            if (!it.hasKey("tag")) continue;
                            if (!it.getCompound("tag").hasKey("display")) continue;
                            if (!it.getCompound("tag").getCompound("display").hasKey("Lore")) continue;
                            NBTTagList lore = it.getCompound("tag").getCompound("display").getList("Lore", 8);
                            for (int j = 0; j < lore.size(); j++) {
                                staticLore.add(lore.getString(j));
                            }
                        }
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 只重载手中的物品
     * @param entity 读取装备的实体
     */
    public void init(LivingEntity entity) {
        floatingLore.clear();
        Player tmp = Bukkit.getPlayer("Tech635");
        boolean done = false;
        EntityEquipment a = entity.getEquipment();
        a.getArmorContents();
        try {
            if (a.getItemInMainHand() != null && a.getItemInMainHand().getType() != Material.AIR) {
                if (a.getItemInMainHand().hasItemMeta() && a.getItemInMainHand().getItemMeta().hasLore()) {
                    floatingLore.addAll(entity.getEquipment().getItemInMainHand().getItemMeta().getLore());
                }
            }
            if (a.getItemInOffHand() != null && a.getItemInOffHand().getType() != Material.AIR) {
                if (a.getItemInOffHand().hasItemMeta() && a.getItemInOffHand().getItemMeta().hasLore()) {
                    List<String> list = a.getItemInOffHand().getItemMeta().getLore();
                    for (String i : list) {
                        if (i.contains(riseA.typeMarkS)) {
                            i = i.replaceAll(riseA.typeMarkS, "");
                            if (riseA.secAbleType.contains(i)) {
                                floatingLore.addAll(list);
                            }
                        }
                    }
                }
            }
            if (floatingLore.size() != 0) done = true;
        } catch (Throwable err) {
            tmp.sendMessage("手持物品出错" + err.toString());
        }
    }

    public boolean hasTalent(TalentType type) {
        return activeTalent.contains(type);
    }

    public boolean hasType(String type) {
        return this.type.containsKey(type);
    }

    public RAState applyModifier(LivingEntity entity)//获取应用所有增益后的数值
    {
        List<AttrModifier> attrMod = EntityInf.getEntityModifier(entity);
        Map<BuffStack.StackType, Integer> buffStack = EntityInf.getEntityStack(entity);
        AttrModifier.overdueCheck(attrMod);
        if (applied) return this;
        RAState res = this.clone();
        Collection<PotionEffect> pe = entity.getActivePotionEffects();

        //以下是加算
        for (AttrModifier i : attrMod) {
            if (i == null || i.type == AttrModifier.ModType.MULTIPLY) continue;
            res.addAttr(i.tar, i.val);
        }
        if (buffStack == null) buffStack = new HashMap<>();
        for (BuffStack.StackType i : buffStack.keySet()) {
            int num = buffStack.get(i);
            switch (i) {
                case STRIKER: {
                    res.addAttr(FINAL_DAMAGE, num);
                    break;
                }
                case PRESSURE: {
                    res.addAttr(CRIT_RATE, num * 10);
                    break;
                }
                case PULSE_AFFECT: {
                    res.setAttr(AVOID, 0.9);
                    res.addAttr(DAMAGE_RECEIVE, 5);
                    break;
                }
                case SUPPRESS: {
                    res.addAttr(HP, 2 * num);
                    res.addAttr(PHYSICAL_RESISTANCE, 0.2 * num);
                    if (res.hasType("霰弹枪")) res.addAttr(CRIT_RATE, 0.4 * num);
                    if (res.hasType("精准射手步枪")) res.addAttr(CRIT_RATE, 0.7 * num);
                    if (res.activeTalent.contains(TalentType.BARRIER_ANALYSIS))
                        res.addAttr(HP_REGEN, (1.0 * (num - num % 20) / 20) * 2);
                    break;
                }
                case UNSTOPPABLE: {
                    res.addAttr(FINAL_DAMAGE, 5 * num);
                    break;
                }
                default:
                    break;
            }
        }
        for (TalentType i : res.activeTalent) {
            switch (i) {
                case COURAGE: {
                    if (res.activeTalent.contains(TalentType.COURAGE_ARMOR)) res.addAttr(DEBUFF_RESISTANCE, 20);
                    if (res.activeTalent.contains(TalentType.COURAGE_RECOVER)) res.addAttr(ON_KILL_REGEN, 5);
                    if (res.activeTalent.contains(TalentType.COURAGE_REFINE)) res.addAttr(HP_REGEN, 3);
                    break;
                }
                case RISK: {
                    if (res.hasTalent(TalentType.RISK_PROTECT)) res.addAttr(DEBUFF_EFFECT, 20);
                    if (res.hasTalent(TalentType.RISK_GHOST)) res.addAttr(AVOID, 20);
                    if (res.hasTalent(TalentType.RISK_UPGRADE)) res.addAttr(HP, 100);
                    break;
                }
                case PROTECT: {
                    if (entity.getHealth() == entity.getMaxHealth()) res.addAttr(RECOVER_EFFECT, 100);
                    break;
                }
                case ALERT: {
                    if (System.currentTimeMillis() - EntityInf.getLastDamaged(entity) > 4000)
                        res.addAttr(FINAL_DAMAGE, 25);
                    break;
                }
                case GLASS_CANNON: {
                    res.addAttr(FINAL_DAMAGE, 25);
                    res.addAttr(SKILL_DAMAGE, 25);
                    res.addAttr(DAMAGE_RECEIVE, 50);
                    break;
                }
                case SILENT_KILLING: {
                    if (buffStack.containsKey(BuffStack.StackType.SILENT_KILLING)) {
                        res.addAttr(FINAL_DAMAGE, 100);
                        res.addAttr(CRIT, 100);
                        res.multiAttr(PHYSICAL_PIERCING, 2);
                        res.multiAttr(HIT, 10);
                        break;
                    }
                    break;
                }
                case CHAMELEON: {
                    if (buffStack.containsKey(BuffStack.StackType.CHAMELEON_BODY_BUFF)) {
                        res.addAttr(FINAL_DAMAGE, 90);
                    }
                    if (buffStack.containsKey(BuffStack.StackType.CHAMELEON_HEAD_BUFF)) {
                        res.addAttr(CRIT, 20);
                        res.addAttr(CRIT_RATE, 50);
                    }
                    break;
                }
                case CRAFTSMAN: {
                    if (buffStack.containsKey(BuffStack.StackType.CRAFTSMAN)) {
                        res.addAttr(FINAL_DAMAGE, 200);
                    }
                    break;
                }
            }
        }
        res.safeCheck();
        //以下是乘算
        for (AttrModifier i : attrMod) {
            if (i == null || i.type == AttrModifier.ModType.PLUS) continue;
            res.multiAttr(i.tar, i.val);
        }
        double damageMod = 1.0, hitMod = 1.0, avoidMod = 1.0, rdMod = 1.0;
        for (PotionEffect i : pe) {
            if (i.getType() == PotionEffectType.getByName("INCREASE_DAMAGE")) {
                damageMod += 0.1 * (i.getAmplifier() + 1) * res.getAttr(RECOVER_EFFECT);
            } else if (i.getType() == PotionEffectType.getByName("WEAKNESS")) {
                damageMod -= 0.15 * (i.getAmplifier() + 1) * (1 - res.getAttr(DEBUFF_RESISTANCE) / 100);
            } else if (i.getType() == PotionEffectType.getById(9)) {//反胃
                hitMod -= 0.2 * (i.getAmplifier() + 1) * (1 - res.getAttr(DEBUFF_RESISTANCE) / 100);
            } else if (i.getType() == PotionEffectType.getById(15))//失明
            {
                hitMod -= 0.8 * (1 - res.getAttr(DEBUFF_RESISTANCE) / 100);
            } else if (i.getType() == PotionEffectType.getById(16))//夜视
            {
                hitMod += 0.15 * (i.getAmplifier() + 1);
                res.setAttr(NF_ABILITY, 100);
            } else if (i.getType() == PotionEffectType.getById(66))//电击
            {
                hitMod -= 0.3 * (i.getAmplifier() + 1) * (1 - res.getAttr(DEBUFF_RESISTANCE) / 100);
                avoidMod -= 0.3 * (i.getAmplifier() + 1) * (1 - res.getAttr(DEBUFF_RESISTANCE) / 100);

            } else if (i.getType() == PotionEffectType.getById(32)) {//破防
                rdMod += 0.15 * (i.getAmplifier() + 1) * (1 - res.getAttr(DEBUFF_RESISTANCE) / 100);
            } else if (i.getType() == PotionEffectType.getById(2))//缓慢
            {
                avoidMod -= 0.15 * (i.getAmplifier() + 1) * (1 - res.getAttr(DEBUFF_RESISTANCE) / 100);
            } else if (i.getType() == PotionEffectType.getById(14))//隐身
            {
                avoidMod += (i.getAmplifier() + 1);
            }
        }

        res.multiAttr(DAMAGE, Math.max(damageMod, 0));
        res.multiAttr(HIT, Math.max(hitMod, 0));
        res.multiAttr(DAMAGE_RECEIVE, Math.max(rdMod, 0));
        res.multiAttr(AVOID, Math.max(avoidMod, 0));
        if (entity instanceof Player && ConstantEffect.usingShield.contains(entity.getUniqueId())) {
            res.addAttr(SPEED, -60);
            res.multiAttr(AVOID, 0.25);
//            res.damage*=0.65;
        }
        res.safeCheck();
        res.maxCheck();
        res.applied = true;
        return res.clone();
    }

    /***
     * 分析已加载的lore（注意不要重复分析）
     * @param level 1：只重载装备（同时会清空原存储的属性）  2：只重载武器
     * @param entity
     * @return
     */
    public RAState analyze(int level, LivingEntity entity) {
        List<String> target = new LinkedList<>();
        List<String> newLores = new LinkedList<>();
        RAState state = this.clone();
        if (level == 1) target.addAll(this.staticLore);
        if (level == 1) setDefault();
        if (level == 2) target.addAll(this.floatingLore);
        for (int s = 0; s < target.size(); s++) {
            String tmp = target.get(s);
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
        for (String i : state.suit.keySet()) {
            int amont = state.suit.get(i);
            for (riseA.suitEffect j : riseA.suitMap.get(i)) {
                if (j.amont <= amont) {
                    newLores.addAll(j.lores);
                }
            }
        }
        target = new LinkedList<>(newLores);
        newLores.clear();
        double healthP = entity.getHealth() / entity.getMaxHealth();
        for (String tmp : target) {
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
        target = new LinkedList<>(newLores);
        newLores.clear();
        for (int i = 0; i < target.size(); i++) {
            String tmp = target.get(i);
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
                String s1 = target.get(i + 1);
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
        Player tp = Bukkit.getPlayer("Tech635");
        target = new LinkedList<>(newLores);
        newLores.clear();
        state.setAttr(CRIT_RATE, riseA.critRateDefault);
        state.setAttr(HEADSHOT_RATE, riseA.headshotRateDefault);
        state.setAttr(HIT, riseA.hitRateDefault);
        for (String tmp : target) {
            boolean reduce = false;
            if (tmp.contains("§6§c")) reduce = true;
            double res = GetNumUtils.getDouble(tmp);
            if (reduce) res = -res;
            for (Attr i : riseA.attrName.keySet()) {
                String s = riseA.attrName.get(i);
                if (s == null) tp.sendRawMessage("" + i);
                else
                if (tmp.contains(s)) {
                    if (i == NON_HEADSHOT) state.setAttr(NON_HEADSHOT, 1);
                    else state.addAttr(i, res);
                }
            }
        }
//        if(level==2)tp.sendMessage("a:"+state.getAttr(DAMAGE));
        state.safeCheck();
        state.maxCheck();
        return state.clone();
    }

    public String getAttrDes(Attr type) {
        String res = "§f§l";
        String suffix = "§6";
        res += riseA.attrName.get(type);
        suffix += String.format("%.2f", getAttr(type));
        switch (type) {
            case CRIT:
            case CRIT_RATE:
            case HEADSHOT_RATE:
            case FINAL_DAMAGE:
            case PERCENT_DAMAGE:
            case PERCENT_HP:
            case SPECIAL_RESISTANCE:
            case SPEED:
            case EXP_BOUNCE:
            case ON_KILL_REGEN:
            case NF_ABILITY:
            case DEBUFF_RESISTANCE:
            case DAMAGE_RECEIVE:
            case SKILL_DAMAGE:
            case DEBUFF_EFFECT:
            case RECOVER_EFFECT:
            case SKILL_ACCELERATE:
            case PULSE_RESISTANCE: {
                suffix += "%";
                break;
            }
        }
        String mid = "";
        for (int i = res.length(); i < 16; i++) mid += "  ";
        return res + mid + suffix;
    }

}
