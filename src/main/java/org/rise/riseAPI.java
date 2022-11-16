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
import org.rise.State.ExtraHp;
import org.rise.State.RAState;
import org.rise.activeSkills.ConstantEffect;
import org.rise.effect.CustomEffect;
import org.rise.effect.CustomEffectBase;
import org.rise.skill.NpcType;
import org.rise.team.TeamBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class riseAPI implements Listener {



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
        RAState state;
        if (EntityInf.playersAttr.containsKey(player.getUniqueId()))
            state = EntityInf.playersAttr.get(player.getUniqueId());
        else state = new RAState();
        state.setDefault();
        state.initAll(player);
        EntityInf.playersAttr.put(player.getUniqueId(), state.analyze(1, player));
    }

    public static void resetPlayerAttr(Player player, boolean alldefault) {
        RAState state;
        if (EntityInf.playersAttr.containsKey(player.getUniqueId()))
            state = EntityInf.playersAttr.get(player.getUniqueId());
        else state = new RAState();
        if (alldefault) state.AllDefault();
        state.setDefault();
        state.initAll(player);
        EntityInf.playersAttr.put(player.getUniqueId(), state.analyze(1, player));
    }

    public static void addAttrMod(LivingEntity entity, AttrModifier modifier) {
        if (entity.isDead()) return;
        if (entity instanceof Player) {
            RAState tmp = EntityInf.getPlayerState(entity.getUniqueId());
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
        RAState state = EntityInf.getPlayerState(player);
        state.addExHp(new ExtraHp(hp, length));
        EntityInf.playersAttr.put(player.getUniqueId(), state);
    }

    public static void addBuffStack(LivingEntity entity, BuffStack.StackType tar, int val) {
        if (entity instanceof Player) {
            RAState state = EntityInf.getPlayerState((Player) entity);
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
            RAState s = new RAState();
            s.AllDefault();
            s.buffStack = buffStack;
            s.addBuffStack(tar, val);
            EntityInf.entityStack.put(entity.getUniqueId(), s.buffStack);
        }
    }

    public static void setPlayerDowned(Player player) {
        resetPlayerAttr(player, true);
        RAState state = EntityInf.getPlayerState(player);
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
        RAState state = EntityInf.getPlayerState(player);
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
        RAState state = EntityInf.getPlayerState(player);
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

    public static void addEffect(RAState self, LivingEntity tar, CustomEffect type, double modifier, int level, double length) {
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
