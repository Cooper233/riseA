package org.rise.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.rise.EntityInf;
import org.rise.State.RAstate;

import java.util.UUID;

public class PlayerEarnExpProcess implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void process(PlayerExpChangeEvent event) {
        int amount = event.getAmount();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        RAstate state = EntityInf.playersAttr.get(uuid);
        amount = (int) Math.ceil(1.0 * amount * (1 + state.expBounce / 100.0));
        event.setAmount(amount);
    }
}
