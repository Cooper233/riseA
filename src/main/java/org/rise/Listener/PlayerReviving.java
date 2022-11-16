package org.rise.Listener;

import lk.vexview.event.KeyBoardPressEvent;
import lk.vexview.event.MinecraftKeys;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.rise.EntityInf;
import org.rise.State.RAState;
import org.rise.activeSkills.ConstantEffect;
import org.rise.riseAPI;

import java.util.List;

public class PlayerReviving implements Listener {
    @EventHandler
    public void process(KeyBoardPressEvent event) {
        Player player = event.getPlayer();
        if (!MinecraftKeys.KEY_F.isTheKey(event.getKey())) return;
        List<Entity> list = (List<Entity>) player.getWorld().getNearbyEntities(player.getLocation(), 2, 2, 2);
        Player res = null;
        for (Entity i : list) {
            if (i instanceof Player) {
                RAState state = EntityInf.getPlayerState((Player) i);
                if (!state.downed) continue;
                if (EntityInf.revivingMapReflect.containsKey(i.getUniqueId())) continue;
                res = (Player) i;
                break;
            }
        }
        if (res == player) return;
        if (res == null) {
            if (EntityInf.revivingMap.containsKey(player.getUniqueId())) {
                EntityInf.reviveProgress.put(EntityInf.revivingMap.get(player.getUniqueId()), 0);
                EntityInf.revivingMapReflect.remove(EntityInf.revivingMap.get(player.getUniqueId()));
                EntityInf.revivingMap.remove(player.getUniqueId());
            }
            return;
        }
        player.sendMessage("§f[§6ISAAC§f]正在救起 " + res.getName());
        if (event.getEventKeyState()) {
            riseAPI.setPlayerReviving(res, player);
        } else {
            EntityInf.revivingMap.remove(res.getUniqueId());
            EntityInf.revivingMapReflect.remove(player.getUniqueId());
            EntityInf.reviveProgress.put(res.getUniqueId(), 0);
        }

    }

    @EventHandler
    public void preventHurt(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (System.currentTimeMillis() - EntityInf.getLastProtect(player) <= 5000) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void preventJump(PlayerMoveEvent event) {
        Player res = (Player) event.getPlayer();
        RAState state = EntityInf.getPlayerState(res);
        if (state == null || !state.downed) return;
        Location tmp = event.getTo();
        if (tmp.getY() > event.getFrom().getY()) {
            tmp.setY(event.getFrom().getY());
        }
        event.setTo(tmp);
    }

    @EventHandler
    public void preventShieldJump(PlayerMoveEvent event) {
        Player res = (Player) event.getPlayer();
        if (!ConstantEffect.usingShield.contains(res.getUniqueId())) return;
        Location tmp = event.getTo();
        if (tmp.getY() > event.getFrom().getY()) {
            tmp.setY(event.getFrom().getY());
        }
        event.setTo(tmp);
    }
}
