package org.rise;

import org.bukkit.Particle;
import org.bukkit.event.Listener;

public class Particles implements Listener {
    public static Particle getMissParticle() {
        return Particle.CLOUD;
    }

    public static Particle getCritParticle() {
        return Particle.EXPLOSION_LARGE;

    }

    public static Particle getRecoverParticle() {
        return Particle.HEART;
    }
}
