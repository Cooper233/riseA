package org.rise.Listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.rise.EntityInf;
import org.rise.State.RAState;

import java.util.UUID;

public class EntityDeathProcess implements Listener {
    @EventHandler
    public void process(EntityDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();
        EntityInf.entityModifier.remove(uuid);
        if (Bukkit.getPlayer(uuid) != null) {
            RAState tmp = new RAState();
            EntityInf.playersAttr.put(uuid, tmp);
        }
        EntityInf.entityStack.remove(uuid);
        EntityInf.entityModifier.remove(uuid);
    }
}
