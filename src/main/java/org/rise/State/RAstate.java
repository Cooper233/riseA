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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RAstate implements Cloneable {
    public List<String> lores = new LinkedList<>();
    public double critChance;
    public double critRate;
    public double headshotRate;
    public boolean nonHeadshot;
    public double damage;
    public double finalDamage;
    public double trueDamage;
    public double hp;
    public double hpRegen;
    public double percentHp;
    public double physicalResistance;
    public double specialResistance;
    public double physicalPiercing;
    public double speed;
    public double percentDamage;
    public double hit;
    public double avoid;
    public double expBounce;
    public double onKillRegen;
    public double nfAbility;
    public double debuffResistance;
    public double damageReceive;
    public double skillLevel;
    public double skillDamage;
    public double debuffEffect;
    public double recoverEffect;
    public double skillAccelerate;
    public double pulseResistance;
    private double totalExHp;
    public boolean downed;
    public boolean applied = false;

    @Override
    public RAstate clone() {
        try {
            RAstate clone = (RAstate) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class extraHp implements Cloneable {
        public long disappear;
        public double left;

        public extraHp(double val, long delay)//delay以毫秒为单位
        {
            left = val;
            disappear = System.currentTimeMillis() + delay;
        }

        @Override
        public extraHp clone() {
            try {
                extraHp clone = (extraHp) super.clone();
                // TODO: copy mutable state here, so the clone can't change the internals of the original
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public Map<PotionEffectType, Integer> potions = new HashMap<>();
    public Map<String, Integer> suit = new HashMap<>();
    public Map<String, Boolean> type = new HashMap<>();
    public Map<BuffStack.StackType, Integer> buffStack = new HashMap<>();
    public Map<BuffStack.StackType, Long> lastBuffReduce = new HashMap<>();
    public List<extraHp> exhp = new LinkedList<>();
    public List<AttrModifier> attrMod = new LinkedList<>();
    public List<TalentType> activeTalent = new LinkedList<>();

    public void AllDefault()//登陆时使用
    {
        critChance = 0;
        critRate = 0;
        headshotRate = 0;
        nonHeadshot = false;
        damage = 0;
        finalDamage = 0;
        trueDamage = 0;
        hp = 0;
        hpRegen = 0;
        percentHp = 0;
        physicalResistance = 0;
        specialResistance = 0;
        physicalPiercing = 0;
        speed = 0;
        percentDamage = 0;
        hit = 0;
        avoid = 0;
        expBounce = 0;
        onKillRegen = 0;
        nfAbility = 0;
        debuffResistance = 0;
        totalExHp = 0;
        damageReceive = 1.0;
        skillLevel = 0;
        skillDamage = 1.0;
        debuffEffect = 1.0;
        recoverEffect = 1.0;
        skillAccelerate = 1.0;
        pulseResistance = 0;
        potions.clear();
        suit.clear();
        type.clear();
        exhp.clear();
        buffStack.clear();
        attrMod.clear();
        activeTalent.clear();
        applied = false;
    }

    public void setDefault()//不重置临时血量，叠层
    {
        critChance = 0;
        critRate = 0;
        headshotRate = 0;
        nonHeadshot = false;
        damage = 0;
        finalDamage = 0;
        trueDamage = 0;
        hp = 0;
        hpRegen = 0;
        percentHp = 0;
        physicalResistance = 0;
        specialResistance = 0;
        physicalPiercing = 0;
        speed = 0;
        percentDamage = 0;
        hit = 0;
        avoid = 0;
        expBounce = 0;
        onKillRegen = 0;
        nfAbility = 0;
        debuffResistance = 0;
        damageReceive = 1.0;
        skillLevel = 0;
        skillDamage = 1.0;
        debuffEffect = 1.0;
        recoverEffect = 1.0;
        skillAccelerate = 1.0;
        pulseResistance = 0;
        potions.clear();
        suit.clear();
        type.clear();
        activeTalent.clear();
        applied = false;

    }

    public void add(RAstate a) {
        this.critChance += a.critChance;
        this.critRate += a.critRate;
        this.headshotRate += a.headshotRate;
        this.damage += a.damage;
        this.finalDamage += a.finalDamage;
        this.trueDamage += a.trueDamage;
        this.hp += a.hp;
        this.hpRegen += a.hpRegen;
        this.physicalResistance += a.physicalResistance;
        this.specialResistance += a.specialResistance;
        this.physicalPiercing += a.physicalPiercing;
        this.speed += a.speed;
        this.percentDamage += a.percentDamage;
        this.percentHp += a.percentHp;
        this.hit += a.hit;
        this.avoid += a.avoid;
        this.expBounce += a.expBounce;
        this.onKillRegen += a.onKillRegen;
        this.nfAbility += a.nfAbility;
        this.debuffResistance += a.debuffResistance;
        this.skillLevel += a.skillLevel;
        this.skillDamage += a.skillDamage - 1.0;
        this.debuffEffect += a.debuffEffect - 1.0;
        this.recoverEffect += a.recoverEffect - 1.0;
        this.skillAccelerate += a.skillAccelerate - 1.0;
        this.pulseResistance += a.pulseResistance;
    }

    public void overdueCheck() {
        while (!exhp.isEmpty() && exhp.get(0).disappear <= System.currentTimeMillis()) {
            totalExHp -= exhp.get(0).left;
            exhp.remove(0);
        }
        while (!attrMod.isEmpty() && attrMod.get(0).disappear <= System.currentTimeMillis()) {
            attrMod.remove(0);
        }
    }

    public void secondlyCheck() {
        overdueCheck();
        BuffStack.stackCheck(buffStack, this);
    }

    public double getTotalExHp() {
        return totalExHp;
    }

    public void addExHp(extraHp ex) {
        ex.left = Math.min(ex.left, riseA.extraHpMax - totalExHp);
        if (ex.left <= 0) return;
        boolean done = false;
        for (int i = 0; i < exhp.size(); i++) {
            if (exhp.get(i).disappear < ex.disappear) {
                done = true;
                exhp.add(i + 1, ex);
                break;
            }
        }
        if (!done) exhp.add(ex);
        totalExHp += ex.left;
    }

    public void addBuffStack(BuffStack.StackType tar, int val) {
        int _max = BuffStack.getMaxStack(tar, this);
        int a;
        if (buffStack.containsKey(tar)) {
            a = buffStack.get(tar);
            a += val;

        } else {
            a = val;
        }
        a = Math.max(0, a);
        a = Math.min(_max, a);
        if (a != 0)
            buffStack.put(tar, a);
        else buffStack.remove(tar);
    }

    public int getStackNum(BuffStack.StackType tar) {
        if (!buffStack.containsKey(tar)) return 0;
        return buffStack.get(tar);
    }

    public void addAttrModifier(AttrModifier val) {
        int l = 0, r = attrMod.size() - 1, ans = -1;
        while (l <= r) {
            int mid = ((l + r) >> 1);
            if (attrMod.get(mid).disappear < val.disappear) {
                ans = mid;
                l = mid + 1;
            } else r = mid - 1;
        }
        attrMod.add(ans + 1, val);
    }

    public double resistDamage(double damage)//返回伤害的剩余值
    {
        while (!exhp.isEmpty()) {
            RAstate.extraHp tmp = exhp.get(0);
            if (tmp.left > damage) {
                tmp.left -= damage;
                totalExHp -= damage;
                exhp.remove(0);
                exhp.add(0, tmp);
                damage = 0;
                break;
            } else {
                damage -= tmp.left;
                totalExHp -= tmp.left;
                exhp.remove(0);
            }
        }
        return damage;
    }

    public void safeCheck() {
        critChance = Math.max(critChance, 0);
        critRate = Math.max(critRate, 0);
        headshotRate = Math.max(headshotRate, 0);
        damage = Math.max(damage, 0);
        hp = Math.max(hp, 0);
        hpRegen = Math.max(hpRegen, -1000.0);
        physicalResistance = Math.max(physicalResistance, 0);
        specialResistance = Math.max(specialResistance, 0);
        physicalPiercing = Math.max(physicalPiercing, 0);
        speed = Math.max(speed, -99.0);
        percentDamage = Math.max(percentDamage, 0);
        percentDamage = Math.min(percentDamage, 100);
        percentHp = Math.max(-99, percentHp);
        hit = Math.max(hit, 0);
        avoid = Math.max(avoid, 0);
        expBounce = Math.max(expBounce, 0);
        onKillRegen = Math.max(onKillRegen, 0);
        nfAbility = Math.max(nfAbility, 0);
        debuffResistance = Math.max(debuffResistance, 0);
        damageReceive = Math.max(damageReceive, 0);
        skillLevel = Math.max(skillLevel, 0);
        skillDamage = Math.max(skillDamage, 0);
        skillAccelerate = Math.max(skillAccelerate, 0);
        debuffEffect = Math.max(debuffEffect, 0);
        recoverEffect = Math.max(recoverEffect, 0);
        pulseResistance = Math.max(pulseResistance, 0);
    }

    public void maxCheck() {
        critChance = Math.min(critChance, riseA.critChanceMax);
        critRate = Math.min(critRate, riseA.critRateMax);
        finalDamage = Math.min(finalDamage, riseA.finalDamageMax);
        trueDamage = Math.min(trueDamage, riseA.trueDamageMax);
        hpRegen = Math.min(hpRegen, riseA.hpRegenMax);
        physicalResistance = Math.min(physicalResistance, riseA.physicalMax);
        physicalPiercing = Math.min(physicalPiercing, riseA.physicalPiercingMax);
        avoid = Math.min(avoid, riseA.avoidRateMax);
        hit = Math.min(hit, riseA.hitRateMax);
        speed = Math.min(speed, riseA.speedMax);
    }

    public long getEffectiveness()//娱乐用的作战效能
    {
        long res = 0;
        res += critChance * 30;
        res += critRate * 50;
        res += headshotRate * 50;
        res += damage * 25;
        res += finalDamage * 50;
        res += trueDamage * 80;
        res += hp * 25;
        res += hpRegen * 40;
        res += percentHp * 50;
        res += Math.pow(1.1, physicalResistance + specialResistance) * 17;
        res += physicalPiercing * 50;
        res += speed * 20;
        res += Math.pow(1.3, percentDamage) * 200;
        res += hit * 25;
        res += avoid * 30;
        res += expBounce * 10;
        res += onKillRegen * 55;
        res += nfAbility * 20;
        res += debuffResistance * 30;
        res += (damageReceive - 1.0) * 6000;
        res += skillLevel * 500;
        res += (skillDamage - 1.0) * 4500;
        res += (debuffEffect - 1.0) * 4500;
        res += (recoverEffect - 1.0) * 2500;
        res += (skillAccelerate - 1.0) * 3000;
        res += (pulseResistance * 50);
        return res;
    }

    public static boolean check(ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasLore();
    }

    public void init(LivingEntity entity) {
        Player tmp = Bukkit.getPlayer("Tech635");
        boolean done = false;
        EntityEquipment a = entity.getEquipment();
        a.getArmorContents();
        ItemStack s1 = a.getBoots(), s2 = a.getLeggings(), s3 = a.getChestplate(), s4 = a.getHelmet();
        try {
            if (s1 != null && s1.getType() != Material.AIR && s1.hasItemMeta() && s1.getItemMeta().hasLore()) {
                lores.addAll(s1.getItemMeta().getLore());
            }
            if (s2 != null && s2.getType() != Material.AIR && s2.hasItemMeta() && s2.getItemMeta().hasLore()) {
                lores.addAll(s2.getItemMeta().getLore());
            }
            if (s3 != null && s3.getType() != Material.AIR && s3.hasItemMeta() && s3.getItemMeta().hasLore()) {
                lores.addAll(s3.getItemMeta().getLore());
            }
            if (s4 != null && s4.getType() != Material.AIR && s4.hasItemMeta() && s4.getItemMeta().hasLore()) {
                lores.addAll(s4.getItemMeta().getLore());
            }
            if (lores.size() != 0) done = true;
        } catch (Throwable err) {
            tmp.sendMessage("装备出错" + err.toString());
        }
        try {
            if (a.getItemInMainHand() != null && a.getItemInMainHand().getType() != Material.AIR) {
                if (a.getItemInMainHand().hasItemMeta() && a.getItemInMainHand().getItemMeta().hasLore()) {
                    lores.addAll(entity.getEquipment().getItemInMainHand().getItemMeta().getLore());
                }
            }
            if (a.getItemInOffHand() != null && a.getItemInOffHand().getType() != Material.AIR) {
                if (a.getItemInOffHand().hasItemMeta() && a.getItemInOffHand().getItemMeta().hasLore()) {
                    List<String> list = a.getItemInOffHand().getItemMeta().getLore();
                    for (String i : list) {
                        if (i.contains(riseA.typeMarkS)) {
                            i = i.replaceAll(riseA.typeMarkS, "");
                            if (riseA.secAbleType.contains(i)) {
                                lores.addAll(list);
                            }
                        }
                    }
                }
            }
            if (lores.size() != 0) done = true;
        } catch (Throwable err) {
            tmp.sendMessage("手持物品出错" + err.toString());
        }
        if (entity instanceof Player) {
            lores.add(riseA.attrName.get(AttrModifier.Attr.PULSE_RESISTANCE) + "+30%");
            Player player = (Player) entity;
            if (!ModuleGui.guiList.containsKey(player.getUniqueId())) ModuleGui.guiInit(player, false);
            Inventory inv = ModuleGui.guiList.get(player.getUniqueId());
            if (check(inv.getItem(11))) lores.addAll(inv.getItem(11).getItemMeta().getLore());
            if (check(inv.getItem(12))) lores.addAll(inv.getItem(12).getItemMeta().getLore());
            if (check(inv.getItem(13))) lores.addAll(inv.getItem(13).getItemMeta().getLore());
            for (int i = 1; i <= 6; i++) {
                if (check(inv.getItem(26 + i))) lores.addAll(inv.getItem(26 + i).getItemMeta().getLore());
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
                                lores.add(lore.getString(j));
                            }
                        }
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (!done) lores.add("");
    }

    public boolean hasTalent(TalentType type) {
        return activeTalent.contains(type);
    }

    public boolean hasType(String type) {
        return this.type.containsKey(type);
    }

    public RAstate applyModifier(LivingEntity entity)//获取应用所有增益后的数值
    {
        overdueCheck();
        if (applied) return this;
        applied = true;
        RAstate res = null;
        res = (RAstate) this;
        if (!(entity instanceof Player) && EntityInf.entityModifier.containsKey(entity.getUniqueId()))
            this.attrMod = EntityInf.entityModifier.get(entity.getUniqueId());
        if (!(entity instanceof Player) && EntityInf.entityStack.containsKey(entity.getUniqueId()))
            this.buffStack = EntityInf.entityStack.get(entity.getUniqueId());
        Collection<PotionEffect> pe = entity.getActivePotionEffects();
        if (attrMod == null) attrMod = new LinkedList<>();

        //以下是加算
        for (AttrModifier i : attrMod) {
            if (i == null || i.type == AttrModifier.ModType.MULTIPLY) continue;
            switch (i.tar) {
                case CRIT: {
                    res.critChance += i.val;
                    break;
                }
                case CRIT_RATE: {
                    res.critRate += i.val;
                    break;
                }
                case HEADSHOT_RATE: {
                    res.headshotRate += i.val;
                    break;
                }
                case NON_HEADSHOT: {
                    res.nonHeadshot = true;
                    break;
                }
                case DAMAGE: {
                    res.damage += i.val;
                    break;
                }
                case FINAL_DAMAGE: {
                    res.finalDamage += i.val;
                    break;
                }
                case TRUE_DAMAGE: {
                    res.trueDamage += i.val;
                    break;
                }
                case HP: {
                    res.hp += i.val;
                    break;
                }
                case HP_REGEN: {
                    res.hpRegen += i.val;
                    break;
                }
                case PERCENT_HP: {
                    res.percentHp += i.val;
                    break;
                }
                case PHYSICAL_RESISTANCE: {
                    res.physicalResistance += i.val;
                    break;
                }
                case SPECIAL_RESISTANCE: {
                    res.specialResistance += i.val;
                    break;
                }
                case PHYSICAL_PIERCING: {
                    res.physicalPiercing += i.val;
                    break;
                }
                case SPEED: {
                    res.speed += i.val;
                    break;
                }
                case PERCENT_DAMAGE: {
                    res.percentDamage += i.val;
                    break;
                }
                case HIT: {
                    res.hit += i.val;
                    break;
                }
                case AVOID: {
                    res.avoid += i.val;
                    break;
                }
                case EXP_BOUNCE: {
                    res.expBounce += i.val;
                    break;
                }
                case ON_KILL_REGEN: {
                    res.onKillRegen += i.val;
                    break;
                }
                case NF_ABILITY: {
                    res.nfAbility += i.val;
                    break;
                }
                case DEBUFF_RESISTANCE: {
                    res.debuffResistance += i.val;
                    break;
                }
                case DAMAGE_RECEIVE: {
                    res.damageReceive += i.val;
                    break;
                }
                case SKILL_LEVEL: {
                    res.skillLevel += i.val;
                    break;
                }
                case SKILL_DAMAGE: {
                    res.skillDamage += i.val;
                    break;
                }
                case DEBUFF_EFFECT: {
                    res.debuffEffect += i.val;
                    break;
                }
                case RECOVER_EFFECT: {
                    res.recoverEffect += i.val;
                    break;
                }
                case SKILL_ACCELERATE: {
                    res.skillAccelerate += i.val;
                    break;
                }
                case PULSE_RESISTANCE: {
                    res.pulseResistance += i.val;
                    break;
                }

            }
        }
        if (buffStack == null) buffStack = new HashMap<>();
        for (BuffStack.StackType i : buffStack.keySet()) {
            int num = buffStack.get(i);
            switch (i) {
                case STRIKER: {
                    res.finalDamage = res.finalDamage + num * 0.01;
                    break;
                }
                case PRESSURE: {
                    res.critRate = res.critRate + num * 10;
                    break;
                }
                case PULSE_AFFECT: {
                    res.avoid *= 0.9;
                    res.damageReceive += 0.05;
                    break;
                }
                case SUPPRESS: {
                    res.hp += 2 * num;
                    res.physicalResistance += 0.2 * num;
                    if (res.hasType("霰弹枪")) res.critChance += 0.4 * num;
                    if (res.hasType("精准射手步枪")) res.critRate += 0.7 * num;
                    if (res.activeTalent.contains(TalentType.BARRIER_ANALYSIS))
                        res.hpRegen += (1.0 * (num - num % 20) / 20) * 2;
                    break;
                }
                case UNSTOPPABLE: {
                    res.finalDamage += 5 * num;
                }
                default:
                    break;
            }
        }
        for (TalentType i : res.activeTalent) {
            switch (i) {
                case COURAGE: {
                    if (res.activeTalent.contains(TalentType.COURAGE_ARMOR)) res.debuffResistance += 20;
                    if (res.activeTalent.contains(TalentType.COURAGE_RECOVER)) res.onKillRegen += 5;
                    if (res.activeTalent.contains(TalentType.COURAGE_REFINE)) res.hpRegen += 3;
                    break;
                }
                case RISK: {
                    if (res.hasTalent(TalentType.RISK_PROTECT)) res.debuffEffect += 20;
                    if (res.hasTalent(TalentType.RISK_GHOST)) res.avoid += 20;
                    if (res.hasTalent(TalentType.RISK_UPGRADE)) res.hp += 100;
                    break;
                }
                case PROTECT: {
                    if (entity.getHealth() == entity.getMaxHealth()) res.recoverEffect += 1;
                    break;
                }
                case ALERT: {
                    if (System.currentTimeMillis() - EntityInf.getLastDamaged(entity) > 4000) res.finalDamage += 25;
                    break;
                }
                case GLASS_CANNON: {
                    res.finalDamage += 25;
                    res.skillDamage += 0.25;
                    res.damageReceive += 0.5;
                    break;
                }
                case SILENT_KILLING: {
                    if (buffStack.containsKey(BuffStack.StackType.SILENT_KILLING)) {
                        res.finalDamage += 100;
                        res.critChance += 100;
                        res.physicalPiercing *= 2;
                        res.hit *= 10;
                        break;
                    }
                    break;
                }
                case CHAMELEON: {
                    if (buffStack.containsKey(BuffStack.StackType.CHAMELEON_BODY_BUFF)) {
                        res.finalDamage += 90;
                    }
                    if (buffStack.containsKey(BuffStack.StackType.CHAMELEON_HEAD_BUFF)) {
                        res.critChance += 20;
                        res.critRate += 50;
                    }
                    break;
                }
                case CRAFTSMAN: {
                    if (buffStack.containsKey(BuffStack.StackType.CRAFTSMAN)) {
                        res.finalDamage += 200;
                    }
                    break;
                }
            }
        }
        res.safeCheck();
        //以下是乘算
        for (AttrModifier i : attrMod) {
            if (i == null || i.type == AttrModifier.ModType.PLUS) continue;
            switch (i.tar) {
                case CRIT: {
                    res.critChance *= i.val;
                    break;
                }
                case CRIT_RATE: {
                    res.critRate *= i.val;
                    break;
                }
                case HEADSHOT_RATE: {
                    res.headshotRate *= i.val;
                    break;
                }
                case DAMAGE: {
                    res.damage *= i.val;
                    break;
                }
                case FINAL_DAMAGE: {
                    res.finalDamage *= i.val;
                    break;
                }
                case TRUE_DAMAGE: {
                    res.trueDamage *= i.val;
                    break;
                }
                case HP: {
                    res.hp *= i.val;
                    break;
                }
                case HP_REGEN: {
                    res.hpRegen *= i.val;
                    break;
                }
                case PERCENT_HP: {
                    res.percentHp *= i.val;
                    break;
                }
                case PHYSICAL_RESISTANCE: {
                    res.physicalResistance *= i.val;
                    break;
                }
                case SPECIAL_RESISTANCE: {
                    res.specialResistance *= i.val;
                    break;
                }
                case PHYSICAL_PIERCING: {
                    res.physicalPiercing *= i.val;
                    break;
                }
                case SPEED: {
                    res.speed *= i.val;
                    break;
                }
                case PERCENT_DAMAGE: {
                    res.percentDamage *= i.val;
                    break;
                }
                case HIT: {
                    res.hit *= i.val;
                    break;
                }
                case AVOID: {
                    res.avoid *= i.val;
                    break;
                }
                case EXP_BOUNCE: {
                    res.expBounce *= i.val;
                    break;
                }
                case ON_KILL_REGEN: {
                    res.onKillRegen *= i.val;
                    break;
                }
                case NF_ABILITY: {
                    res.nfAbility *= i.val;
                    break;
                }
                case DEBUFF_RESISTANCE: {
                    res.debuffResistance *= i.val;
                    break;
                }
                case DAMAGE_RECEIVE: {
                    res.damageReceive *= i.val;
                    break;
                }
                case SKILL_LEVEL: {
                    res.skillLevel *= i.val;
                    break;
                }
                case SKILL_DAMAGE: {
                    res.skillDamage *= i.val;
                    break;
                }
                case DEBUFF_EFFECT: {
                    res.debuffEffect *= i.val;
                    break;
                }
                case RECOVER_EFFECT: {
                    res.recoverEffect *= i.val;
                    break;
                }
                case SKILL_ACCELERATE: {
                    res.skillAccelerate *= i.val;
                    break;
                }
                case PULSE_RESISTANCE: {
                    res.pulseResistance *= i.val;
                    break;
                }

            }
        }
        double damageMod = 1.0, hitMod = 1.0, avoidMod = 1.0, rdMod = 1.0;
        for (PotionEffect i : pe) {
            if (i.getType() == PotionEffectType.getByName("INCREASE_DAMAGE")) {
                damageMod += 0.1 * (i.getAmplifier() + 1) * res.recoverEffect;
            } else if (i.getType() == PotionEffectType.getByName("WEAKNESS")) {
                damageMod -= 0.15 * (i.getAmplifier() + 1) * (1 - res.debuffResistance / 100);
            } else if (i.getType() == PotionEffectType.getById(9)) {//反胃
                hitMod -= 0.2 * (i.getAmplifier() + 1) * (1 - res.debuffResistance / 100);
            } else if (i.getType() == PotionEffectType.getById(15))//失明
            {
                hitMod -= 0.8 * (1 - res.debuffResistance / 100);
            } else if (i.getType() == PotionEffectType.getById(16))//夜视
            {
                hitMod += 0.15 * (i.getAmplifier() + 1);
                res.nfAbility = 100;
            } else if (i.getType() == PotionEffectType.getById(66))//电击
            {
                hitMod -= 0.3 * (i.getAmplifier() + 1) * (1 - res.debuffResistance / 100);
                avoidMod -= 0.3 * (i.getAmplifier() + 1) * (1 - res.debuffResistance / 100);

            } else if (i.getType() == PotionEffectType.getById(32)) {//破防
                rdMod += 0.15 * (i.getAmplifier() + 1) * (1 - res.debuffResistance / 100);
            } else if (i.getType() == PotionEffectType.getById(2))//缓慢
            {
                avoidMod -= 0.15 * (i.getAmplifier() + 1) * (1 - res.debuffResistance / 100);
            } else if (i.getType() == PotionEffectType.getById(14))//隐身
            {
                avoidMod += (i.getAmplifier() + 1);
            }
        }

        res.damage *= Math.max(damageMod, 0);
        res.hit *= Math.max(hitMod, 0);
        res.damageReceive *= Math.max(rdMod, 0);
        res.avoid *= Math.max(avoidMod, 0);
        if (entity instanceof Player && ConstantEffect.usingShield.contains(entity.getUniqueId())) {
            res.speed -= 60;
            res.avoid *= 0.5;
//            res.damage*=0.65;
        }
        res.safeCheck();
        res.maxCheck();
        return res;
    }

    public String getAttrDes(AttrModifier.Attr type) {
        String res = "§f§l";
        String suffix = "§6";
        switch (type) {
            case DAMAGE: {
                res += riseA.damageS;
                suffix += String.format("%.2f", damage);
                break;
            }
            case CRIT: {
                res += riseA.critChanceS;
                suffix += String.format("%.2f", critChance);
                suffix += "%";
                break;
            }
            case CRIT_RATE: {
                res += riseA.critRateS;
                suffix += String.format("%.2f", critRate);
                suffix += "%";
                break;
            }
            case HEADSHOT_RATE: {
                res += riseA.headshotRateS;
                suffix += String.format("%.2f", headshotRate);
                suffix += "%";
                break;
            }
            case FINAL_DAMAGE: {
                res += riseA.finalDamageS;
                suffix += String.format("%.2f", finalDamage);
                suffix += "%";
                break;
            }
            case TRUE_DAMAGE: {
                res += riseA.trueDamageS;
                suffix += String.format("%.2f", trueDamage);
                break;
            }
            case HP: {
                res += riseA.hpS;
                suffix += String.format("%.2f", hp);
                break;
            }
            case HP_REGEN: {
                res += riseA.hpRegenS;
                suffix += String.format("%.2f", hpRegen);
                break;
            }
            case PERCENT_DAMAGE: {
                res += riseA.percentDamageS;
                suffix += String.format("%.2f", percentDamage);
                suffix += "%";
                break;
            }
            case PERCENT_HP: {
                res += riseA.percentHpS;
                suffix += String.format("%.2f", percentHp);
                suffix += "%";
                break;
            }
            case PHYSICAL_RESISTANCE: {
                res += riseA.physicalResistanceS;
                suffix += String.format("%.2f", physicalResistance);
                break;
            }
            case SPECIAL_RESISTANCE: {
                res += riseA.specialResistanceS;
                suffix += String.format("%.2f", specialResistance);
                break;
            }
            case PHYSICAL_PIERCING: {
                res += riseA.physicalPiercingS;
                suffix += String.format("%.2f", physicalPiercing);
                break;
            }
            case SPEED: {
                res += riseA.speedS;
                suffix += String.format("%.2f", speed);
                suffix += "%";
                break;
            }
            case HIT: {
                res += riseA.hitRateS;
                suffix += String.format("%.2f", hit);
                break;
            }
            case AVOID: {
                res += riseA.avoidRateS;
                suffix += String.format("%.2f", avoid);
                break;
            }
            case EXP_BOUNCE: {
                res += riseA.expBounceS;
                suffix += String.format("%.2f", expBounce);
                suffix += "%";
                break;
            }
            case ON_KILL_REGEN: {
                res += riseA.onKillRegenS;
                suffix += String.format("%.2f", onKillRegen);
                suffix += "%";
                break;
            }
            case NF_ABILITY: {
                res += riseA.nfAbilityS;
                suffix += String.format("%.2f", nfAbility);
                suffix += "%";
                break;
            }
            case DEBUFF_RESISTANCE: {
                res += riseA.debuffResistanceS;
                suffix += String.format("%.2f", debuffResistance);
                suffix += "%";
                break;
            }
            case DAMAGE_RECEIVE: {
                res += "受到伤害比例";
                suffix += String.format("%.2f", damageReceive);
                break;
            }
            case SKILL_LEVEL: {
                res += riseA.skillLevelS;
                suffix += String.format("%.2f", skillLevel);
                break;
            }
            case SKILL_DAMAGE: {
                res += riseA.skillDamageS;
                suffix += String.format("%.2f", skillDamage);
                break;
            }
            case DEBUFF_EFFECT: {
                res += riseA.debuffEffectS;
                suffix += String.format("%.2f", debuffEffect);
                break;
            }
            case RECOVER_EFFECT: {
                res += riseA.recoverEffectS;
                suffix += String.format("%.2f", recoverEffect);
                break;
            }
            case SKILL_ACCELERATE: {
                res += riseA.skillAccelerateS;
                suffix += String.format("%.2f", skillAccelerate);
                break;
            }
            case PULSE_RESISTANCE: {
                res += riseA.pulseResistanceS;
                suffix += String.format("%.2f", pulseResistance);
                suffix += "%";
                break;
            }
        }
        String mid = "";
        for (int i = res.length(); i < 16; i++) mid += "  ";
        return res + mid + suffix;
    }

}
