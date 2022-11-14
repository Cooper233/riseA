package org.rise.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.rise.EntityInf;
import org.rise.activeSkills.ConstantEffect;
import org.rise.riseAPI;
import org.rise.team.TeamBase;

import java.util.UUID;

public class PlayerResetAttrListener implements Listener {
    @EventHandler
    public void invClose(InventoryCloseEvent event) {
        riseAPI.resetPlayerAttr((Player) event.getPlayer());
    }

    @EventHandler
    public void changeItem(PlayerItemHeldEvent event) {
        riseAPI.resetPlayerAttr(event.getPlayer());
        EntityInf.killCount.put(event.getPlayer().getUniqueId(), 0);
    }

    @EventHandler
    public void joinSever(PlayerJoinEvent event) {
        riseAPI.resetPlayerAttr(event.getPlayer(), true);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event) {
        riseAPI.resetPlayerAttr(event.getPlayer(), true);
        riseAPI.setPlayerRevived(event.getPlayer());
        ConstantEffect.removeSkill(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void logout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID i = player.getUniqueId();
        ConstantEffect.removeSkill(event.getPlayer().getUniqueId());
        EntityInf.playersAttr.remove(i);
        TeamBase.leaveTeam(player);
    }
}
