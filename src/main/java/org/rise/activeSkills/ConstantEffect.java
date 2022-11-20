package org.rise.activeSkills;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.rise.activeSkills.effect.ActiveBase;
import org.rise.activeSkills.effect.ShieldBase;

import java.util.*;

public class ConstantEffect {
    public static Map<UUID, List<ActiveBase>> constant = new HashMap<>();
    public static Map<UUID, Map<ActiveBase, Long>> lastActive = new HashMap<>();
    public static Map<UUID, UUID> platformId = new HashMap<>();//玩家-支持平台
    public static List<UUID> usingGel = new LinkedList<>();//正在使用化学物质发射器的玩家
    public static List<UUID> usingPulse = new LinkedList<>();//正在蓄力脉冲的玩家
    public static List<UUID> usingShield = new LinkedList<>();//展开了护盾的玩家
    public static Map<UUID, Double> ShieldHealth = new HashMap<>();//护盾血量
    public static Map<UUID, Long> lastShieldDamaged = new HashMap<>();//护盾上次受到攻击
    public static Map<UUID, Long> lastUseShield = new HashMap<>();//护盾上次进行操作
    public static Map<UUID, BossBar> shieldGUI = new HashMap<>();

    public static List<ActiveBase> getAffectingSkill(Player player) {
        if (constant.containsKey(player.getUniqueId())) return constant.get(player.getUniqueId());
        else return new LinkedList<>();
    }

    public static boolean isActiveTypeAffecting(Player player, ActiveType type) {
        List<ActiveBase> tmp = getAffectingSkill(player);
        for (ActiveBase base : tmp) {
            if (base.type == type) return true;
        }
        return false;
    }

    public static void removeSkill(UUID i) {
        constant.remove(i);
        platformId.remove(i);
    }

    public static Runnable secondlyCheck = new Runnable() {
        @Override
        public void run() {
            for (UUID i : constant.keySet()) {
                List<ActiveBase> list = constant.get(i);
                if (list == null) continue;
                List<ActiveBase> tmp = new LinkedList<>(list);
                for (ActiveBase base : tmp) {
                    base.secondlyCheck(Bukkit.getPlayer(i));
                }
            }
        }
    };
    public static Runnable ticklyCheck = new Runnable() {
        @Override
        public void run() {
            for (UUID i : constant.keySet()) {
                List<ActiveBase> list = constant.get(i);
                if (list == null) continue;
                List<ActiveBase> tmp = new LinkedList<>(list);
                for (ActiveBase base : tmp) {
                    base.ticklyCheck(Bukkit.getPlayer(i));
                }
            }
            List<UUID> res = new LinkedList<>(usingShield);
            for (UUID i : usingShield) {
                if (!constant.containsKey(i)) res.remove(i);
                List<ActiveBase> list = constant.get(i);
                boolean find = false;
                for (ActiveBase base : list) {
                    if (base instanceof ShieldBase) {
                        find = true;
                        break;
                    }
                }
                if (!find) res.remove(i);
            }
            usingShield = new LinkedList<>(res);
        }
    };

}
