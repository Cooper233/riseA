package org.rise.team;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamBase {
    public static Map<UUID, UUID> belonging = new HashMap<>();
    public static Map<UUID, List<UUID>> teamInfo = new HashMap<>();

    public static class request {
        public int type;//0：加入小队  1：邀请加入
        public long time;
        public UUID id;

        public request(int type, UUID id) {
            this.id = id;
            this.type = type;
            time = System.currentTimeMillis();
        }
    }

    public static Map<UUID, request> teamRequest = new HashMap<>();

    public static UUID getNowTeam(Player player) {
        return belonging.getOrDefault(player.getUniqueId(), null);
    }

    public static void leaveTeam(Player player) {
        UUID t = getNowTeam(player);
        if (t == null) return;
        List<UUID> tmp = teamInfo.get(t);
        if (t == player.getUniqueId()) {
            List<UUID> tt = new LinkedList<>(tmp);
            for (UUID i : tt) {
                if (i == t) continue;
                leaveTeam(Bukkit.getPlayer(i));
            }
            player.sendMessage("§6[§fISAAC§6]§f你已不在小队中。");
            belonging.remove(player.getUniqueId());
            return;
        }
        tmp.remove(player.getUniqueId());
        if (tmp.size() > 1) {
            teamInfo.put(t, tmp);
        } else teamInfo.remove(t);
        Bukkit.getPlayer(t).sendMessage("§6[§fISAAC§6]§f特工 " + player.getDisplayName() + " §f已离开你的小队。");
        player.sendMessage("§6[§fISAAC§6]§f已离开 " + player.getDisplayName() + " §f的小队。");
        belonging.remove(player.getUniqueId());
    }

    public static void joinTeam(Player self, Player tar) {
        List<UUID> tmp = teamInfo.get(tar.getUniqueId());
        if (tmp == null) {
            tmp = new LinkedList<>();
            tmp.add(tar.getUniqueId());
            belonging.put(tar.getUniqueId(), tar.getUniqueId());
        }
        teamInfo.remove(self.getUniqueId());
        tmp.add(self.getUniqueId());
        teamInfo.put(tar.getUniqueId(), tmp);
        belonging.put(self.getUniqueId(), tar.getUniqueId());
        self.sendMessage("§6[§fISAAC§6]§f已加入 " + tar.getDisplayName() + " §f的小队。");
    }

    public static void sendJoinRequest(Player self, Player tar) {
        String ori = tar.getDisplayName();
        if (!teamInfo.containsKey(tar.getUniqueId())) {
            for (UUID i : teamInfo.keySet()) {
                List<UUID> tmp = teamInfo.get(i);
                if (tmp.contains(tar.getUniqueId())) {
                    tar = Bukkit.getPlayer(i);
                    if (tmp.size() == 4) {
                        self.sendMessage("§6[§fISAAC§6]§c请求失败！当前申请的小队人数已达到上限");
                    }
                    break;
                }
            }
        }
        if (teamRequest.containsKey(tar.getUniqueId())) {
            request pair = teamRequest.get(tar.getUniqueId());
            if (System.currentTimeMillis() - pair.time <= 30000) {
                self.sendMessage("§6[§fISAAC§6]§c请求失败！当前申请的小队有待处理的请求！");
                return;
            }
        }
        tar.sendMessage("§6[§fISAAC§6]§f特工 " + self.getDisplayName() + " §f想要加入你的小队。30秒内输入§6/rsa team ac§f同意申请。§c/rsa team de§f可拒绝申请。");
        self.sendMessage("§6[§fISAAC§6]§f已向特工 " + tar.getDisplayName() + "（特工" + ori + "所在小队的队长） §f发送请求");
        teamRequest.put(tar.getUniqueId(), new request(0, self.getUniqueId()));
    }

    public static void sendInvite(Player self, Player tar) {
        if (belonging.containsKey(tar.getUniqueId())) {
            tar.sendMessage("§6[§fISAAC§6]§f特工 " + self.getDisplayName() + " §f已在一个小队中，无法进行邀请。");
            return;
        }
        List<UUID> tmp = teamInfo.get(self.getUniqueId());
        if (tmp == null) tmp = new LinkedList<>();
        if (tmp.size() == 4) {
            self.sendMessage("§6[§fISAAC§6]§c请求失败！当前小队人数已达到上限！");
        }
        if (tmp.contains(tar.getUniqueId())) {
            self.sendMessage("§6[§fISAAC§6]§c请求失败！当前特工已在小队中！");
        }
        if (teamRequest.containsKey(tar.getUniqueId())) {
            request pair = teamRequest.get(tar.getUniqueId());
            if (System.currentTimeMillis() - pair.time <= 30000) {
                self.sendMessage("§6[§fISAAC§6]§c请求失败！被邀请人有待处理的请求！");
                return;
            }
        }
        tar.sendMessage("§6[§fISAAC§6]§f特工 " + self.getDisplayName() + " §f想要你加入他所在的小队。30秒内输入§6/rsa team ac§f同意申请。§c/rsa team de§f可拒绝申请。");
        self.sendMessage("§6[§fISAAC§6]§f已向特工 " + tar.getDisplayName() + " §f发送请求");
        teamRequest.put(tar.getUniqueId(), new request(1, self.getUniqueId()));
    }
}
