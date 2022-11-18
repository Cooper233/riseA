package org.rise.skill;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedList;
import java.util.List;

public class TargetBase {
    public enum Type {
        TEAM, SELF, AROUND
    }

    public Type type;
    public List<Pair<Integer, Integer>> angle = new LinkedList<>();
    public double length;
    public int num;
    public List<NpcType> blackList = new LinkedList<>();
    public List<NpcType> whitelist = new LinkedList<>();//如果不为空则不判定黑名单

    public TargetBase(ConfigurationSection config) {
        type = Type.valueOf(config.getString("target", "SELF"));
        if (type != Type.AROUND) return;
        ConfigurationSection data = config.getConfigurationSection("target-data");
        if (data.contains("angle"))
            for (String i : data.getStringList("angle")) {
                String l = i.substring(0, i.indexOf('-')), r = i.substring(i.indexOf('-') + 1);
                angle.add(Pair.of(Integer.parseInt(l), Integer.parseInt(r)));
            }
        length = data.getDouble("length");
        num = data.getInt("num");
        for (String i : data.getStringList("blacklist")) {
            blackList.add(NpcType.valueOf(i));
        }
        for (String i : data.getStringList("whitelist")) {
            whitelist.add(NpcType.valueOf(i));
        }
    }

    public TargetBase(Type ty) {// 小队/个人
        type = ty;
    }

    /***
     * 只读入黑名单
     * @param ty
     * @param l
     * @param n
     * @param bl 黑名单
     */
    public TargetBase(Type ty, double l, int n, List<NpcType> bl) {
        type = ty;
        length = l;
        num = n;
        blackList = bl;
    }

    /***
     * 读入白名单和黑名单
     * @param ty
     * @param l
     * @param n
     * @param bl 黑名单
     * @param wl 白名单
     */
    public TargetBase(Type ty, double l, int n, List<NpcType> bl, List<NpcType> wl) {
        type = ty;
        length = l;
        num = n;
        whitelist = wl;
        blackList = bl;
    }

    public static TargetBase SELF;
    public static TargetBase TEAM;
}
