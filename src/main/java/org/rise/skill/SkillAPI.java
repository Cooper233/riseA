package org.rise.skill;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.rise.EntityInf;
import org.rise.State.Attr;
import org.rise.State.RAState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillAPI {
    public static boolean performSkill(LivingEntity entity, SkillBase skill, boolean force) {
        return performSkill(entity, entity.getEyeLocation(), skill, force);
    }

    public static boolean performSkill(LivingEntity entity, Location loc, SkillBase skill, boolean force) {
        if (skill == null) return false;
        if (!force) {
            if (entity instanceof Player) {
                RAState state = EntityInf.getPlayerState((Player) entity);
                if (EntityInf.cdProgress.containsKey(skill.cd_type)) {
                    Map<UUID, Long> t = EntityInf.cdProgress.get(skill.cd_type);
                    if (t.containsKey(entity.getUniqueId()) && t.get(entity.getUniqueId()) > 0) {
                        entity.sendMessage("§f[§6ISAAC§f]§4技能冷却中!剩余时间: §f" + t.get(entity.getUniqueId()) / (1.0 + state.getAttr(Attr.SKILL_ACCELERATE) / 100) / 1000 + "§4 秒！");
                        return false;
                    }
                }
                if (state.downed) return false;
            }
            if (System.currentTimeMillis() < EntityInf.getLastSkillAffect(entity, skill.resist_Type) + skill.resist * 1000L) {
                return false;
            }
        }
        skill.perform(entity.getWorld(), loc, entity);
        if (!EntityInf.cdProgress.containsKey(skill.cd_type)) EntityInf.cdProgress.put(skill.cd_type, new HashMap<>());
        Map<UUID, Long> t = EntityInf.cdProgress.get(skill.cd_type);
        t.put(entity.getUniqueId(), (long) (skill.cd * 1000 * (1.0 - skill.cd_decrease / 100.0)));
        EntityInf.cdProgress.put(skill.cd_type, t);
        if (!EntityInf.lastSkillAffect.containsKey(skill.resist_Type))
            EntityInf.lastSkillAffect.put(skill.resist_Type, new HashMap<>());
        t = EntityInf.lastSkillAffect.get(skill.resist_Type);
        t.put(entity.getUniqueId(), System.currentTimeMillis());
        EntityInf.lastSkillAffect.put(skill.resist_Type, t);
        return true;
    }
}
