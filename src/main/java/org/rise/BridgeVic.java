package org.rise;

import com.vicmatskiv.weaponlib.EntityProjectile;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BridgeVic {
    public static Player getShooter(org.bukkit.entity.Entity a) throws InvocationTargetException, IllegalAccessException {
        Object obj = null;
        if ((a instanceof CraftEntity)) {
            Method[] method = ((CraftEntity) a).getClass().getMethods();
            for (Method i : method) {
                if (i.getName().equals("getHandle")) {
                    obj = i.invoke(a);
                }
            }
            if (obj instanceof EntityProjectile && ((EntityProjectile) (obj = (EntityProjectile) obj)).getThrower() != null && ((obj = ((EntityProjectile) (obj)).getThrower().getBukkitEntity()) instanceof Player))
                return (Player) obj;
        } else
            return null;
        return null;
    }
}
