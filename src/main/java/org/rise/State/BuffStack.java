package org.rise.State;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.rise.talent.TalentType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BuffStack {

    public enum StackType {
        STRIKER, PRESSURE, PULSE_AFFECT, SUPPRESS, UNSTOPPABLE, OBLITERATIVE, SILENT_KILLING, CHAMELEON_HEAD, CHAMELEON_BODY, CHAMELEON_HEAD_BUFF, CHAMELEON_BODY_BUFF,
        CRAFTSMAN, RISK
    }

    public StackType type;

    public BuffStack(StackType type) {
        this.type = type;
    }

    public static int getMaxStack(StackType type, RAState state)//若只需获取基础最大值，state直接new一个就行
    {
        switch (type) {
            case STRIKER: {
                return 50;
            }
            case PRESSURE:
            case UNSTOPPABLE: {
                return 5;
            }
            case PULSE_AFFECT: {
                return 100;
            }
            case SUPPRESS: {
                if (state.hasTalent(TalentType.BARRIER)) {
                    if (state.hasTalent(TalentType.BARRIER_EXPANSION)) {
                        return 100;
                    } else return 50;
                }
                return 0;
            }
            case RISK: {
                return 20;
            }
            case OBLITERATIVE: {
                return 25;
            }
            case CHAMELEON_BODY_BUFF:
            case CHAMELEON_HEAD_BUFF:
            case CRAFTSMAN:
            case SILENT_KILLING: {
                return 1;
            }
            case CHAMELEON_BODY:
            case CHAMELEON_HEAD: {
                return 30;
            }
            default:
                return 0;
        }
    }

    public static void stackCheck(Map<StackType, Integer> map, RAState state) {
        Player tp = Bukkit.getPlayer("Tech635");
        if (map == null || map.isEmpty()) return;
        List<BuffStack.StackType> list = new LinkedList<>(map.keySet());
        for (BuffStack.StackType i : list) {
            int red = 0, len = -1;
            int a = map.get(i);
            switch (i) {
                case STRIKER:
                case SUPPRESS: {
                    red = 2;
                    len = 1000;
                    break;
                }
                case PRESSURE:
                case PULSE_AFFECT: {
                    red = 1;
                    len = 1000;
                    break;
                }
                case UNSTOPPABLE: {
                    red = 1;
                    len = 15000;
                    break;
                }
                case SILENT_KILLING: {
                    red = 1;
                    len = 9000;
                    break;
                }
                case OBLITERATIVE: {
                    red = 1;
                    len = 5000;
                    break;
                }
                case CHAMELEON_BODY_BUFF:
                case CHAMELEON_HEAD_BUFF: {
                    red = 1;
                    len = 45000;
                    break;
                }
            }
            if (len == -1) continue;
            if (!state.lastBuffReduce.containsKey(i)) state.lastBuffReduce.put(i, System.currentTimeMillis());
            if (System.currentTimeMillis() - state.lastBuffReduce.get(i) > len) {
                state.lastBuffReduce.put(i, System.currentTimeMillis());
                state.addBuffStack(i, -red);
            }
        }
    }

    public static void stackEffect(Map<StackType, Integer> map, LivingEntity entity) {
        if (entity == null) return;
        for (BuffStack.StackType i : map.keySet()) {
            if (map.get(i) > 0) {
                switch (i) {
                    case PULSE_AFFECT: {
                        World world = entity.getWorld();
                        MaterialData data = new MaterialData(Material.STAINED_GLASS);
                        data.setData((byte) 1);
                        for (int j = 1; j <= 10; j++) {
                            world.spawnParticle(Particle.FALLING_DUST, entity.getEyeLocation().add(0, 0.5, 0), 0, 0, 1, 0, 2, data);
                        }
                        break;
                    }
                }
            }
        }
    }
}
