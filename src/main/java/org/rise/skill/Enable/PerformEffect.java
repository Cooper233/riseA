package org.rise.skill.Enable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PerformEffect implements Runnable {
    @Override
    public void run() {
        if (entity instanceof Player) {
            entity.sendMessage(message);
        }
        if (entity != null) {
            if (!Objects.equals(sound, "")) {
                entity.getWorld().playSound(loc, sound, 16, 1);
            }
        }
        if (type != null)
            switch (type) {
                case TACTICAL_LINK: {
                    MaterialData data = new MaterialData(Material.STAINED_GLASS);
                    data.setData((byte) Integer.parseInt(colors.get(0)));
                    world.spawnParticle(Particle.FLAME, loc, 10, 1, 1, 1, 0.3);
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        world.spawnParticle(Particle.FALLING_DUST, loc, 16, 1, 1, 1, 3, data);
                        world.spawnParticle(Particle.BLOCK_CRACK, loc, 6, 1, 1, 1, 3, data);
                    }
                    break;
                }
                case PULSE: {
                    MaterialData data = new MaterialData(Material.STAINED_GLASS);
                    for (double r = 1; r <= 15; r += 1) {
                        data.setData((byte) Integer.parseInt(colors.get((int) (Math.ceil(r) % colors.size()))));
                        for (double a = 0; a < 360; a += 30 / r) {
                            double rad = Math.toRadians(a);
                            loc.add(r * Math.cos(rad), 0, r * Math.sin(rad));
                            world.spawnParticle(Particle.FALLING_DUST, loc, 2, 0, 0, 0, 0, data);
                            world.spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0, 0, 0, 0, data);
                            loc.subtract(r * Math.cos(rad), 0, r * Math.sin(rad));
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case DIRECT_PULSE: {
                    MaterialData data = new MaterialData(Material.STAINED_GLASS);
                    for (double pos = 1; pos <= 15; pos += 1) {
                        data.setData((byte) Integer.parseInt(colors.get((int) (Math.ceil(pos) % colors.size()))));
                        for (double a = 0; a < 360; a += 10) {
                            double r = 1 + pos * 0.3;
                            double rad = Math.toRadians(a);
                            Vector dir = loc.getDirection();
                            loc.add(pos * dir.getX() + r * Math.cos(rad) * dir.getZ(), r * Math.sin(rad), pos * dir.getZ() - r * Math.cos(rad) * dir.getX());
                            world.spawnParticle(Particle.FALLING_DUST, loc, 2, 0, 0, 0, 0, data);
                            world.spawnParticle(Particle.BLOCK_CRACK, loc, 2, 0, 0, 0, 0, data);
                            loc.subtract(pos * dir.getX() + r * Math.cos(rad) * dir.getZ(), r * Math.sin(rad), pos * dir.getZ() - r * Math.cos(rad) * dir.getX());
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case PLATFORM: {
                    MaterialData data = new MaterialData(Material.STAINED_GLASS);
                    data.setData((byte) Integer.parseInt(colors.get(0)));
                    for (int i = 0; i < 3; i++) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        world.spawnParticle(Particle.FALLING_DUST, loc, 10, 3, 2, 3, 0.1, data);
                        world.spawnParticle(Particle.BLOCK_CRACK, loc, 3, 3, 2, 3, 0.1, data);
                    }
                    break;
                }
            }
    }

    public EnableEffectBase.ParticleType type;
    public List<String> colors = new LinkedList<>();
    public World world;
    public Location loc;
    public LivingEntity entity;
    public String message;
    public String sound;

    public PerformEffect(EnableEffectBase base, World w, Location l, LivingEntity e) {
        type = base.type;
        colors = base.colors;
        message = base.message;
        world = w;
        loc = l;
        entity = e;
        sound = base.sound;
    }
}
