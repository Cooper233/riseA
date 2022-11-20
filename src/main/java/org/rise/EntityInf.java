package org.rise;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.rise.State.AttrModifier;
import org.rise.State.BuffStack;
import org.rise.State.ExtraHp;
import org.rise.State.RAState;
import org.rise.effect.CustomEffectBase;

import java.util.*;

public class EntityInf {
    public static Map<UUID, UUID> revivingMap = new HashMap<>();//救助者-被救助者
    public static Map<UUID, UUID> revivingMapReflect = new HashMap<>();//被救助者-救助者
    public static Map<UUID, Integer> reviveProgress = new HashMap<>();
    public static Map<String, Map<UUID, Long>> lastSkillAffect = new HashMap<>();
    public static Map<String, Map<UUID, Long>> cdProgress = new HashMap<>();
    public static Map<UUID, RAState> playersAttr = new HashMap<>();//玩家的属性表，UUID-RAstate
    public static Map<UUID, List<AttrModifier>> entityModifier = new HashMap<>();//实体的属性修改（包括玩家）
    public static Map<UUID, List<CustomEffectBase>> entityEffect = new HashMap<>();//实体的状态效果
    public static Map<UUID, Map<BuffStack.StackType, Integer>> entityStack = new HashMap<>();//实体的叠层
    public static Map<UUID, Map<BuffStack.StackType, Long>> entityLastStackReduce = new HashMap<>();//实体的每个上一次叠层减少
    public static Map<UUID, List<ExtraHp>> entityExtraHp = new HashMap<>();
    public static Map<UUID, UUID> indirectDamage = new HashMap<>();//临时创建实体来进行攻击的对照表（临时实体-攻击源）
    public static Map<UUID, Long> lastAttack = new HashMap<>();//实体的上一次攻击时间（没有的话默认为0）
    public static Map<UUID, Double> lastAttackAmount = new HashMap<>();//实体的上一次攻击伤害（没有的话默认为0）
    public static Map<UUID, Long> lastDamaged = new HashMap<>();//实体的上一次被攻击时间（没有的话默认为0）
    public static Map<UUID, Long> lastDodge = new HashMap<>();//实体的上一次闪避时间（没有的话默认为0）
    public static Map<UUID, Long> lastCrit = new HashMap<>();//实体的上一次暴击时间（没有的话默认为0）
    public static Map<UUID, Long> lastHeadshot = new HashMap<>();//实体的上一次爆头时间（没有的话默认为0）
    public static Map<UUID, Long> lastKilled = new HashMap<>();//实体的上一次击杀时间（没有的话默认为0）
    public static Map<UUID, Long> lastSkillAttack = new HashMap<>();//实体的上一次技能伤害时间（没有的话默认为0）
    public static Map<UUID, Integer> killCount = new HashMap<>();//手中武器的击杀数（玩家切换武器时重置为0，怪物不重置）
    public static Map<UUID, Long> lastRevive = new HashMap<>();//上次倒地时间
    public static Map<UUID, Long> lastProtect = new HashMap<>();//上次重创保护时间
    public static Map<UUID, RAState> entityAttr = new HashMap<>();

    public static void allInfReset() {
        cdProgress.clear();
        lastSkillAffect.clear();
        playersAttr.clear();
        entityModifier.clear();
        entityEffect.clear();
        entityStack.clear();
        indirectDamage.clear();
        lastProtect.clear();
        lastSkillAttack.clear();
        lastKilled.clear();
        entityExtraHp.clear();
        lastCrit.clear();
        lastDodge.clear();
        lastAttack.clear();
        lastDamaged.clear();
        lastSkillAffect.clear();
        lastRevive.clear();
    }

    public static List<ExtraHp> getEntityExtraHp(Entity entity) {
        return entityExtraHp.getOrDefault(entity.getUniqueId(), new LinkedList<>());
    }

    public static void setEntityExtraHp(Entity entity, List<ExtraHp> list) {
        if (list == null) list = new LinkedList<>();
        entityExtraHp.put(entity.getUniqueId(), list);
    }

    public static Map<BuffStack.StackType, Integer> getEntityStack(Entity entity) {
        if (entityStack.containsKey(entity.getUniqueId())) return entityStack.get(entity.getUniqueId());
        return new HashMap<>();
    }

    public static void setEntityStack(Entity entity, Map<BuffStack.StackType, Integer> map) {
        if (map == null) map = new HashMap<>();
        entityStack.put(entity.getUniqueId(), map);
    }

    public static Map<BuffStack.StackType, Long> getEntityLastStackReduce(Entity entity) {
        if (entityLastStackReduce.containsKey(entity.getUniqueId()))
            return entityLastStackReduce.get(entity.getUniqueId());
        return new HashMap<>();
    }

    public static void setEntityLastStackReduce(Entity entity, Map<BuffStack.StackType, Long> map) {
        if (map == null) map = new HashMap<>();
        entityLastStackReduce.put(entity.getUniqueId(), map);
    }

    public static List<AttrModifier> getEntityModifier(Entity entity) {
        if (entity == null) return new LinkedList<>();
        if (entityModifier.containsKey(entity.getUniqueId()) && entityModifier.get(entity.getUniqueId()) != null)
            return new LinkedList<>(entityModifier.get(entity.getUniqueId()));
        return new LinkedList<>();
    }

    public static void setEntityModifier(Entity entity, List<AttrModifier> list) {
        if (list == null) list = new LinkedList<>();
        entityModifier.put(entity.getUniqueId(), list);
    }

    public static RAState getPlayerState(UUID id) {
        if (Bukkit.getPlayer(id) != null) return playersAttr.get(id).clone();
        else return null;
    }

    public static RAState getPlayerState(Player player) {
        RAState state = playersAttr.get(player.getUniqueId()).clone();
        state.init(player);
        return state;
    }

    public static RAState getPlayerState(String name) {
        Player a = Bukkit.getPlayer(name);
        return getPlayerState(a.getUniqueId());
    }

    public static RAState getEntityState(LivingEntity entity) {
        if (entity instanceof Player) return getPlayerState((Player) entity);
        if (entityAttr.containsKey(entity.getUniqueId()))
            return entityAttr.get(entity.getUniqueId()).clone();
        RAState state = new RAState();
        state.setDefault();
        state.initAll(entity);
        return state.analyze(1, entity).clone();
    }

    public static void resetEntityState(LivingEntity entity) {
        RAState state = new RAState();
        state.AllDefault();
        state.initAll(entity);
        entityAttr.put(entity.getUniqueId(), state.analyze(1, entity));
    }

    public static void putPlayerState(Player player, RAState state) {
        playersAttr.put(player.getUniqueId(), state);
    }

    public static LivingEntity getOriginAttacker(UUID id) {
        if (indirectDamage.containsKey(id)) return (LivingEntity) Bukkit.getEntity(indirectDamage.get(id));
        else return null;
    }

    public static void createIndirectDamage(LivingEntity messenger, LivingEntity origin) {
        indirectDamage.put(messenger.getUniqueId(), origin.getUniqueId());
    }

    public static long getLastSkillAffect(LivingEntity entity, String type) {
        if (lastSkillAffect.containsKey(type)) {
            Map<UUID, Long> t = lastSkillAffect.get(type);
            if (t.containsKey(entity.getUniqueId())) return t.get(entity.getUniqueId());
            else return 0;
        }
        return 0;
    }

    public static long getLastAttack(LivingEntity entity) {
        return lastAttack.containsKey(entity.getUniqueId()) ? lastAttack.get(entity.getUniqueId()) : 0;
    }

    public static void setLastAttack(LivingEntity entity) {
        lastAttack.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static double getLastAttackAmount(LivingEntity entity) {
        return lastAttackAmount.containsKey(entity.getUniqueId()) ? lastAttackAmount.get(entity.getUniqueId()) : 0;
    }

    public static void setLastAttackAmount(LivingEntity entity, double amount) {
        lastAttackAmount.put(entity.getUniqueId(), amount);
    }

    public static long getLastDodge(LivingEntity entity) {
        return lastDodge.containsKey(entity.getUniqueId()) ? lastDodge.get(entity.getUniqueId()) : 0;
    }

    public static void setLastDodge(LivingEntity entity) {
        lastDodge.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static long getLastDamaged(LivingEntity entity) {
        return lastDamaged.containsKey(entity.getUniqueId()) ? lastDamaged.get(entity.getUniqueId()) : 0;
    }

    public static void setLastDamaged(LivingEntity entity) {
        lastDamaged.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static long getLastSkillAttack(LivingEntity entity) {
        return lastSkillAttack.containsKey(entity.getUniqueId()) ? lastSkillAttack.get(entity.getUniqueId()) : 0;
    }

    public static void setLastSkillAttack(LivingEntity entity) {
        lastSkillAttack.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static long getLastCrit(LivingEntity entity) {
        return lastCrit.containsKey(entity.getUniqueId()) ? lastCrit.get(entity.getUniqueId()) : 0;
    }

    public static void setLastCrit(LivingEntity entity) {
        lastCrit.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static long getLastHeadshot(LivingEntity entity) {
        return lastHeadshot.containsKey(entity.getUniqueId()) ? lastHeadshot.get(entity.getUniqueId()) : 0;
    }

    public static void setLastHeadshot(LivingEntity entity) {
        lastHeadshot.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static long getLastKilled(LivingEntity entity) {
        return lastKilled.containsKey(entity.getUniqueId()) ? lastKilled.get(entity.getUniqueId()) : 0;
    }

    public static void setLastKilled(LivingEntity entity) {
        lastKilled.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static long getLastRevive(LivingEntity entity) {
        return lastRevive.containsKey(entity.getUniqueId()) ? lastRevive.get(entity.getUniqueId()) : 0;
    }

    public static void setLastRevive(LivingEntity entity) {
        lastRevive.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static long getLastProtect(LivingEntity entity) {
        return lastProtect.containsKey(entity.getUniqueId()) ? lastProtect.get(entity.getUniqueId()) : 0;
    }

    public static void setLastProtect(LivingEntity entity) {
        lastProtect.put(entity.getUniqueId(), System.currentTimeMillis());
    }

    public static void killEvent(LivingEntity entity) {
        if (killCount.containsKey(entity.getUniqueId()))
            killCount.put(entity.getUniqueId(), killCount.get(entity.getUniqueId()) + 1);
        else killCount.put(entity.getUniqueId(), 1);
    }

}
