package org.rise.skill;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.rise.skill.Effect.*;
import org.rise.skill.Enable.EnableEffectBase;

import java.util.LinkedList;
import java.util.List;

public class SkillBase {
    public String name;
    public double cd;
    public String cd_type;
    public double cd_decrease;
    public int delay;
    public int times;
    public double resist;
    public String resist_Type;
    public List<EffectBase> effect = new LinkedList<>();
    public EnableEffectBase enableEffect;

    public SkillBase(ConfigurationSection config) {
        name = config.getString("name");
        cd = config.getDouble("cd", 0);//秒
        cd_type = config.getString("cd-type", "NON_CD_SKILL");
        cd_decrease = config.getDouble("cd-decrease", 0);
        ConfigurationSection eff = config.getConfigurationSection("effect");
        for (String i : eff.getKeys(false)) {
            ConfigurationSection tmp = eff.getConfigurationSection(i);
            switch (EffectBase.Type.valueOf(tmp.getString("type"))) {
                case POTION: {
                    effect.add(new EffectPotion(tmp));
                    break;
                }
                case ATTR: {
                    effect.add(new EffectAttr(tmp));
                    break;
                }
                case DAMAGE: {
                    effect.add(new EffectDamage(tmp));
                    break;
                }
                case EXHP: {
                    effect.add(new EffectExHp(tmp));
                    break;
                }
                case RECOVER: {
                    effect.add(new EffectRecover(tmp));
                    break;
                }
                case SKILL: {
                    effect.add(new EffectSkill(tmp));
                    break;
                }
                case STACK: {
                    effect.add(new EffectStack(tmp));
                    break;
                }
                case SUMMON: {
                    effect.add(new EffectSummon(tmp));
                    break;
                }
                case REVIVE: {
                    effect.add(new EffectRevive(tmp));
                    break;
                }
            }
        }
        delay = config.getInt("delay", 0);
        times = config.getInt("times", 1);
        if (config.contains("resist")) {
            ConfigurationSection c = config.getConfigurationSection("resist");
            resist = c.getDouble("time");
            resist_Type = c.getString("type");
        } else resist = 0;
        if (config.contains("onEnable"))
            enableEffect = new EnableEffectBase(config.getConfigurationSection("onEnable"));
    }

    public SkillBase(String n, int d, int t, double r, String rT, List<EffectBase> eff) {
        name = n;
        delay = d;
        times = t;
        resist = r;
        resist_Type = rT;
        effect = eff;
    }

    public SkillBase(String n, double c, String ct, int d, int t, double r, String rT, List<EffectBase> eff, EnableEffectBase ee) {
        name = n;
        cd = c;
        cd_type = ct;
        delay = d;
        times = t;
        resist = r;
        resist_Type = rT;
        effect = eff;
        enableEffect = ee;
    }

    public void perform(World world, Location loc, LivingEntity entity)//entity为使用技能者
    {
        Thread p = new Thread(new PerformSkill(this, world, loc, entity));
        p.start();
        if (enableEffect != null)
            enableEffect.perform(world, loc, entity);
    }
}
