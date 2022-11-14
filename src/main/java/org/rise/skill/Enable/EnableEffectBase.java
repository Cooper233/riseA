package org.rise.skill.Enable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;

public class EnableEffectBase {
    public String message;

    public enum ParticleType {
        NULL, TACTICAL_LINK, PULSE, DIRECT_PULSE, PLATFORM
    }

    public ParticleType type;
    public List<String> colors = new LinkedList<>();
    public String sound;

    public EnableEffectBase(ConfigurationSection config) {
        message = config.getString("message", null);
        type = ParticleType.valueOf(config.getString("particle", "NULL"));
        if (config.contains("color"))
            colors.addAll(config.getStringList("color"));
        sound = config.getString("sound", "");
    }

    public EnableEffectBase(String msg, ParticleType ty, List<String> cl, String sd) {
        message = msg;
        type = ty;
        colors = cl;
        sound = sd;
    }

    public void perform(World world, Location loc, LivingEntity entity) {
        Thread thread = new Thread(new PerformEffect(this, world, loc, entity));
        thread.start();
    }
}
