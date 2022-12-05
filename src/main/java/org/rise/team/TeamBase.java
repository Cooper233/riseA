package org.rise.team;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.rise.utils.RandNumUtils;

import java.util.*;
//TODO: 这玩意有bug

public class TeamBase {
    public static Map<UUID, Integer> belonging = new HashMap<>();
    public static Map<Integer, List<UUID>> teamInfo = new HashMap<>();
    public static Map<Integer, UUID> teamLeader = new HashMap<>();

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

    public static int getNowTeam(Player player) {
        return belonging.getOrDefault(player.getUniqueId(), -1);
    }

    public static void leaveTeam(Player player) {
        int t = getNowTeam(player);
        if (t == -1) return;
        List<UUID> tmp = teamInfo.get(t);
        if (teamLeader.get(t) == player.getUniqueId()) {
            List<UUID> tt = new LinkedList<>(tmp);
            for (UUID i : tt) {
                if (i == player.getUniqueId()) continue;
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
        Bukkit.getPlayer(teamLeader.get(t)).sendMessage("§6[§fISAAC§6]§f特工 " + player.getDisplayName() + " §f已离开你的小队。");
        player.sendMessage("§6[§fISAAC§6]§f已离开 " + Bukkit.getPlayer(teamLeader.get(t)).getDisplayName() + " §f的小队。");
        belonging.remove(player.getUniqueId());
    }

    public static void joinTeam(Player self, Player tar) {
        int t = getNowTeam(self);
        if (t != -1) {
            self.sendMessage("§6[§fISAAC§6]§c当前已在小队中，无法加入!");
            return;
        }
        t = getNowTeam(tar);
        List<UUID> tmp;
        if (t == -1) {
            t = RandNumUtils.getRand(1, 20000);
            while (teamInfo.containsKey(t)) t = RandNumUtils.getRand(1, 20000);
            tmp = new LinkedList<>();
            tmp.add(tar.getUniqueId());
            belonging.put(tar.getUniqueId(), t);
        } else tmp = teamInfo.get(t);
        teamInfo.remove(t);
        tmp.add(self.getUniqueId());
        teamInfo.put(t, tmp);
        belonging.put(self.getUniqueId(), t);
        self.sendMessage("§6[§fISAAC§6]§f已加入 " + tar.getDisplayName() + " §f的小队。");
    }

    public static void sendJoinRequest(Player self, Player tar) {
        String ori = tar.getDisplayName();
        int t = belonging.get(tar.getUniqueId());
        if (t != -1) {
            List<UUID> tmp = teamInfo.get(t);
            tar = Bukkit.getPlayer(teamLeader.get(t));
            if (tmp.size() == 4) {
                self.sendMessage("§6[§fISAAC§6]§c请求失败！当前申请的小队人数已达到上限");
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
        int t = belonging.get(self.getUniqueId());
        List<UUID> tmp;
        if (t != -1) {
            tmp = teamInfo.get(t);
            if (tmp.size() == 4) {
                self.sendMessage("§6[§fISAAC§6]§c请求失败！当前小队人数已达到上限！");
            }
        }
        if (belonging.get(tar.getUniqueId()) != -1) {
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
