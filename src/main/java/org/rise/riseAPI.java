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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/***
 * 包含了大量常用的方法
 */
public class riseAPI implements Listener {


    /***
     * 获取物品的标签
     * @param item
     * @return
     */
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

    /***
     * 完全重载玩家的属性
     * @param player
     */
    public static void resetPlayerAttr(Player player) {
        RAState state;
        if (EntityInf.playersAttr.containsKey(player.getUniqueId()))
            state = EntityInf.playersAttr.get(player.getUniqueId());
        else state = new RAState();
        state.setDefault();
        state.initAll(player);
        EntityInf.playersAttr.put(player.getUniqueId(), state.analyze(1, player));
    }

    /***
     * 与上一个函数相同，当前版本区别仅在于倒地
     * @param player
     * @param alldefault
     */
    public static void resetPlayerAttr(Player player, boolean alldefault) {
        RAState state;
        if (EntityInf.playersAttr.containsKey(player.getUniqueId()))
            state = EntityInf.playersAttr.get(player.getUniqueId());
        else state = new RAState();
        if (alldefault) state.AllDefault();
        else state.setDefault();
        state.initAll(player);
        EntityInf.playersAttr.put(player.getUniqueId(), state.analyze(1, player));
    }

    /***
     * 为一个实体增加数值修改器
     * @param entity
     * @param modifier
     */
    public static void addAttrMod(LivingEntity entity, AttrModifier modifier) {
        if (entity.isDead()) return;
        List<AttrModifier> tmp = EntityInf.getEntityModifier(entity);
        int l = 0, r = tmp.size() - 1, ans = -1;
        while (l <= r) {
            int mid = ((l + r) >> 1);
            if (tmp.get(mid).disappear < modifier.disappear) {
                ans = mid;
                l = mid + 1;
            } else r = mid - 1;
        }
        tmp.add(ans + 1, modifier);
        EntityInf.setEntityModifier(entity, tmp);
    }

    /***
     *  为一个实体增加额外生命值
     * @param entity
     * @param hp
     * @param length 以毫秒为单位
     */
    public static void addExHp(LivingEntity entity, double hp, long length)//
    {
        List<ExtraHp> list = EntityInf.getEntityExtraHp(entity);
        ExtraHp.addExHp(list, new ExtraHp(hp, length), entity);
    }

    /***
     * 为一个实体增加叠层
     * @param entity
     * @param tar
     * @param val
     */
    public static void addBuffStack(LivingEntity entity, BuffStack.StackType tar, int val) {
        Map<BuffStack.StackType, Integer> map = EntityInf.getEntityStack(entity);
        int bef = BuffStack.getStackNum(map, tar);
        BuffStack.addBuffStack(map, tar, val, EntityInf.getEntityState(entity).analyze(2, entity));
        int aft = BuffStack.getStackNum(map, tar);
        EntityInf.setEntityStack(entity, map);
        switch (tar) {
            case RISK: {
                if (aft > bef) {
                    addExHp(entity, 30, 40000);
                }
                break;
            }
        }
    }

    /***
     * 使某个玩家处于倒地状态
     * @param player
     */
    public static void setPlayerDowned(Player player) {
        resetPlayerAttr(player, true);
        RAState state = EntityInf.getPlayerState(player);
        state.downed = true;
        EntityInf.playersAttr.put(player.getUniqueId(), state);
    }

    /***
     * 设置倒地玩家正在被谁扶起
     * @param player
     * @param reviver
     */
    public static void setPlayerReviving(Player player, LivingEntity reviver) {
        EntityInf.revivingMap.put(reviver.getUniqueId(), player.getUniqueId());
        EntityInf.revivingMapReflect.put(player.getUniqueId(), reviver.getUniqueId());
    }

    /***
     * 推进倒地玩家的扶起进度
     * @param res
     * @param reviver
     */
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

    /***
     * 设置玩家被扶起
     * @param player
     * @param reviver
     */
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

    /***
     * 同上，但不显示被谁扶起
     * @param player
     */
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

    /***
     * 获取实体的标签
     * @param player
     * @param entity
     * @return
     */
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
            if (entity.getWorld().getName().toLowerCase().startsWith("pvp")) {
                if (TeamBase.getNowTeam(player) != -1 && TeamBase.getNowTeam(player) == TeamBase.getNowTeam((Player) entity)) {
                    types.add(NpcType.PLAYER);
                } else types.add(NpcType.PLAYER_ENEMY);
            } else types.add(NpcType.PLAYER);
        } else if (entity instanceof Player) types.add(NpcType.PLAYER);
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

    /***
     * 为实体增加自定义效果
     * @param tar
     * @param type
     * @param modifier 效果增幅
     * @param level 效果等级
     * @param length 持续时间
     */
    public static void addEffect(LivingEntity tar, CustomEffect type, double modifier, int level, double length) {
        List<CustomEffectBase> list = EntityInf.entityEffect.get(tar.getUniqueId());
        if (list == null) list = new LinkedList<>();
        for (CustomEffectBase base : list) {
            if (base.type == type) return;
        }
        CustomEffectBase now = new CustomEffectBase(type, level, modifier, (long) (length * 1000));
        list.add(now);
        EntityInf.entityEffect.put(tar.getUniqueId(), list);
    }

    /***
     * 检查实体是否拥有某效果
     * @param entity
     * @param type
     * @return
     */
    public static boolean checkEffect(LivingEntity entity, CustomEffect type) {
        List<CustomEffectBase> list = EntityInf.entityEffect.get(entity.getUniqueId());
        if (list == null) return false;
        for (CustomEffectBase base : list) {
            if (base.type == type && base.start + base.length > System.currentTimeMillis()) return true;
        }
        return false;
    }
}
