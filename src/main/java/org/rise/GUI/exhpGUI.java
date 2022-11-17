package org.rise.GUI;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.rise.EntityInf;
import org.rise.State.RAState;
import org.rise.riseA;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class exhpGUI implements Listener {
    public static Map<UUID, BossBar> exhpBar = new HashMap<>();

    public static void barInit(Player player) {
        RAState state = EntityInf.getPlayerState(player);
        if (state == null) return;
        state.overdueCheck();
        BossBar bar = Bukkit.createBossBar("额外生命值", BarColor.BLUE, BarStyle.SEGMENTED_10);
        bar.setProgress(state.getTotalExHp() / riseA.extraHpMax);
        if (bar.getProgress() > 0) bar.addPlayer(player);
        else bar.removePlayer(player);
        exhpBar.put(player.getUniqueId(), bar);
    }

    public static void barCheck(Player player) {
        if (!exhpBar.containsKey(player.getUniqueId())) barInit(player);
        RAState state = EntityInf.getPlayerState(player);
        if (state == null) return;
        state.overdueCheck();
        BossBar bar = exhpBar.get(player.getUniqueId());
        bar.setProgress(state.getTotalExHp() / riseA.extraHpMax);
        if (bar.getProgress() > 0) bar.addPlayer(player);
        else bar.removePlayer(player);
        exhpBar.put(player.getUniqueId(), bar);
    }

}
