package org.rise.effect;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.rise.activeSkills.ConstantEffect;
import org.rise.riseAPI;
import org.rise.skill.NpcType;

import java.util.List;

public class CustomEffectBase {
    public CustomEffect type;
    public int level;
    public double modifier;
    public long length;
    public long start;

    public CustomEffectBase(CustomEffect type, int level, double modifier, long length) {
        start = System.currentTimeMillis();
        this.length = length;
        this.modifier = modifier;
        this.level = level;
        this.type = type;
    }

    public static void secondlyCheck(LivingEntity entity, CustomEffectBase base) {
        List<NpcType> npc = riseAPI.getEntityType(entity);
        switch (base.type) {
            case BLEEDING: {
                double damage = 1;
                switch (base.level) {
                    case 1: {
                        damage = 5;
                        break;
                    }
                    case 2: {
                        damage = 15;
                        break;
                    }
                    case 3: {
                        damage = 40;
                        break;
                    }
                    case 4: {
                        damage = 60;
                        break;
                    }
                    case 5: {
                        damage = 95;
                        break;
                    }
                }
                damage *= base.modifier;
                entity.damage(damage);
                break;
            }
            case DISTURBED: {
                double damage = 1;
                switch (base.level) {
                    case 1: {
                        damage = 15;
                        break;
                    }
                    case 2: {
                        damage = 25;
                        break;
                    }
                    case 3: {
                        damage = 55;
                        break;
                    }
                    case 4: {
                        damage = 100;
                        break;
                    }
                    case 5: {
                        damage = 140;
                        break;
                    }
                }
                if (npc.contains(NpcType.ELC_NPC)) {
                    entity.damage(damage);
                }
                if (entity instanceof Player) {
                    if (ConstantEffect.usingShield.contains(entity.getUniqueId())) {
                        ConstantEffect.lastUseShield.put(entity.getUniqueId(), System.currentTimeMillis());
                        entity.getWorld().playSound(entity.getEyeLocation(), "modularwarfare:effect.shield_break", 4, 1);
                        ConstantEffect.usingShield.remove(entity.getUniqueId());
                    }
                }
                break;
            }
        }
    }
}
