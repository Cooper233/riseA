package org.rise.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.rise.EntityInf;
import org.rise.GUI.exhpGUI;
import org.rise.Inventory.ModuleGui;
import org.rise.State.AttrModifier;
import org.rise.State.BuffStack;
import org.rise.State.RAstate;
import org.rise.effect.CustomEffectBase;
import org.rise.riseA;
import org.rise.riseAPI;
import org.rise.skill.Effect.EffectExHp;
import org.rise.skill.NpcType;
import org.rise.skill.SkillAPI;
import org.rise.skill.SkillBase;
import org.rise.skill.TargetBase;
import org.rise.talent.TalentType;

import java.util.*;

public class EntityUpdate {
    public static void setPlayerState(Player player) {
        Player tp = Bukkit.getPlayer("Tech635");

        RAstate state = EntityInf.getPlayerState(player);
        state = state.applyModifier(player);
        if (state.downed) {
            state.hp = state.hp * 2 + 20;
            state.hpRegen = Math.min(20, (state.hp * 2 + 40) / 40);
            state.speed = -90;
            player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, player.getEyeLocation(), 5, 0, 0, 0, 1);
            player.getWorld().spawnParticle(Particle.REDSTONE, player.getEyeLocation(), 5, 1, 1, 1, 2);
        }
        if (state.hp != 0) {
            double res = state.hp + 20;
            if (state.percentHp != 0) res = res * (100 + state.percentHp) / 100.0;
            AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            instance.setBaseValue(Math.max(res, 20));
        } else {
            AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            instance.setBaseValue(20);
        }
        player.setWalkSpeed((float) (0.2 * (100.0 + state.speed) / 100.0));
        try {
            for (PotionEffectType i : state.potions.keySet()) {
                PotionEffect p = new PotionEffect(i, 100, state.potions.get(i) - 1);
                player.addPotionEffect(p, true);
            }
        } catch (Throwable e) {
            tp.sendMessage("添加状态:" + e);
        }
    }

    public static void bagCheck(Player player) {
        PlayerInventory inv = player.getInventory();
        if (player.isOp()) return;
        for (int i = 0; i < 36; i++) {
            org.bukkit.inventory.ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            if (!item.hasItemMeta()) continue;
            if (!item.getItemMeta().hasLore()) continue;
            for (String s : item.getItemMeta().getLore()) {
                if (s.contains(riseA.bindingS)) {
                    String name = s.replaceAll(riseA.bindingS, "");
                    if (name.equals("#NULL")) {
                        ItemMeta itemMeta = item.getItemMeta();
                        List<String> lore = new LinkedList<>();
                        for (String ss : item.getItemMeta().getLore()) {
                            if (ss.contains(riseA.bindingS)) {
                                lore.add(riseA.bindingS + player.getName());
                            } else lore.add(ss);
                        }
                        itemMeta.setLore(lore);
                        item.setItemMeta(itemMeta);
                        inv.setItem(i, item);
                    } else if (!Objects.equals(player.getName(), name)) {
                        player.sendMessage(riseA.notBelongS);
                        player.getWorld().dropItem(player.getLocation(), item);
                        inv.setItem(i, new org.bukkit.inventory.ItemStack(Material.AIR));
                        return;
                    }
                }
            }
        }
    }

    public static Runnable playerTicklyCheck = new Runnable() {//每tick一次（50毫秒）
        @Override
        public void run() {
            for (String i : EntityInf.cdProgress.keySet()) {
                Map<UUID, Long> cds = EntityInf.cdProgress.get(i);
                for (UUID uuid : cds.keySet()) {
                    RAstate state = EntityInf.getPlayerState(uuid);
                    long left = cds.get(uuid);
                    if (state == null) continue;
                    left -= 50 * state.skillAccelerate;
                    cds.put(uuid, Math.max(0, left));
                }
                EntityInf.cdProgress.put(i, cds);
            }
            for (UUID res : EntityInf.revivingMap.keySet()) {

                Location loc1 = Bukkit.getPlayer(EntityInf.revivingMap.get(res)).getLocation(), loc2 = Bukkit.getEntity(res).getLocation();
                if (loc1.distance(loc2) > 2) {
                    EntityInf.reviveProgress.put(EntityInf.revivingMap.get(res), 0);
                    EntityInf.revivingMapReflect.remove(EntityInf.revivingMap.get(Bukkit.getEntity(res).getUniqueId()));
                    EntityInf.revivingMap.remove(Bukkit.getEntity(res).getUniqueId());
                } else
                    riseAPI.pushRevivingProgress(Bukkit.getPlayer(EntityInf.revivingMap.get(res)), (LivingEntity) Bukkit.getEntity(res));
            }
            Collection<? extends Player> data = Bukkit.getServer().getOnlinePlayers();
            for (Player tmp : data) {
                UUID uuid = tmp.getUniqueId();
                RAstate state = EntityInf.getPlayerState(tmp);
                exhpGUI.barCheck(tmp);
                ModuleGui.guiList.get(uuid).setItem(8, ModuleGui.getInfo(tmp));
                BuffStack.stackEffect(state.buffStack, tmp);
            }
        }
    };
    public static Runnable playerSecondlyCheck = new Runnable() {
        @Override
        public void run() {
            Collection<? extends Player> data = Bukkit.getServer().getOnlinePlayers();
            for (Player tmp : data) {
                UUID uuid = tmp.getUniqueId();
                riseAPI.resetPlayerAttr(tmp);
                RAstate state = EntityInf.getPlayerState(tmp);
                bagCheck(tmp);
                state = state.applyModifier(Bukkit.getPlayer(uuid));
                state.secondlyCheck();
                for (TalentType i : state.activeTalent) {
                    switch (i) {
                        case ADRENALINE: {
                            int num = 0;
                            for (Entity j : tmp.getNearbyEntities(5, 5, 5)) {
                                if (j instanceof LivingEntity) {
                                    List<NpcType> types = riseAPI.getEntityType(j);
                                    if (types.contains(NpcType.OTHER) || types.contains(NpcType.NPC_ENEMY)) num += 1;
                                }
                                if (num == 3) break;
                            }
                            SkillBase skill = new SkillBase("肾上腺素", 0, 1, 5, "ADRENALINE", Arrays.asList(new EffectExHp(5, tmp.getMaxHealth() * 0.2 * num, TargetBase.SELF)));
                            SkillAPI.performSkill(tmp, skill, false);
                            break;
                        }
                        case RISK: {
                            if (System.currentTimeMillis() - EntityInf.getLastDamaged(tmp) >= 30000) {
                                riseAPI.addBuffStack(tmp, BuffStack.StackType.RISK, 1);
                            }
                            break;
                        }
                    }
                }
                if (!tmp.isDead()) {
                    if (state.downed) state.hpRegen = -Math.min(20, (state.hp * 2 + 40) / 40);
                    tmp.setHealth(Math.min(Math.max(0.0, tmp.getHealth() + state.hpRegen), tmp.getMaxHealth()));
                }
                setPlayerState(tmp);
            }
        }
    };
    public static Runnable EntityTicklyCheck = new Runnable() {
        @Override
        public void run() {
            for (UUID uuid : EntityInf.entityModifier.keySet()) {
                List<AttrModifier> tmp = EntityInf.entityModifier.get(uuid);
                if (tmp == null) continue;
                while (!tmp.isEmpty() && tmp.get(0).disappear <= System.currentTimeMillis()) {
                    tmp.remove(0);
                }
                EntityInf.entityModifier.put(uuid, tmp);
            }
            for (UUID uuid : EntityInf.entityEffect.keySet()) {
                List<CustomEffectBase> tmp = EntityInf.entityEffect.get(uuid);
                if (tmp == null) continue;
                while (!tmp.isEmpty() && tmp.get(0).start + tmp.get(0).length <= System.currentTimeMillis()) {
                    tmp.remove(0);
                }
                EntityInf.entityEffect.put(uuid, tmp);
            }
            for (UUID uuid : EntityInf.entityStack.keySet()) {
                Map<BuffStack.StackType, Integer> tmp = EntityInf.entityStack.get(uuid);
                if (tmp == null) continue;
                RAstate state = new RAstate();
                state.AllDefault();
                state.lastBuffReduce = EntityInf.entityLastStackReduce.get(uuid);
                if (state.lastBuffReduce == null) state.lastBuffReduce = new HashMap<>();
                BuffStack.stackCheck(tmp, state);
                EntityInf.entityStack.put(uuid, tmp);
                BuffStack.stackEffect(tmp, (LivingEntity) Bukkit.getEntity(uuid));
                EntityInf.entityLastStackReduce.put(uuid, state.lastBuffReduce);
            }
        }
    };
    public static Runnable EntitySecondlyCheck = new Runnable() {
        @Override
        public void run() {
            for (UUID uuid : EntityInf.entityEffect.keySet()) {
                List<CustomEffectBase> tmp = EntityInf.entityEffect.get(uuid);
                for (CustomEffectBase base : tmp) {
                    CustomEffectBase.secondlyCheck((LivingEntity) Bukkit.getEntity(uuid), base);
                }
            }
            for (UUID uuid : EntityInf.entityStack.keySet()) {
                Map<BuffStack.StackType, Integer> tmp = EntityInf.entityStack.get(uuid);
                if (tmp == null) continue;
                BuffStack.stackEffect(tmp, (LivingEntity) Bukkit.getEntity(uuid));
            }
        }
    };
}
