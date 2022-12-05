package org.rise;

import org.bukkit.Particle;
import org.bukkit.event.Listener;

/***
 * 本来是想把所有效果都写进这个库里，现在感觉并没有什么卵用
 */
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
