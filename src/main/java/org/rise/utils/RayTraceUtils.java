package org.rise.utils;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.MovingObjectPosition;
import net.minecraft.server.v1_12_R1.Vec3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.List;

/***
 * 射线追踪模块
 * 用就完事了
 */
public class RayTraceUtils {
    public static Location rayTraceBlock(Location start, Vector direction, double maxDistance) {
        World world = start.getWorld();
        if (world != null) {
            Vector dir = direction.clone().normalize().multiply(maxDistance);
            Vec3D startPos = new Vec3D(start.getX(), start.getY(), start.getZ());
            Vec3D endPos = new Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
            //boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock
            MovingObjectPosition result = ((CraftWorld) world).getHandle().rayTrace(startPos, endPos, false, true, false);
            if (result != null) {
                return new Location(world, result.pos.x, result.pos.y, result.pos.z);
            }
        }
        return null;
    }

    public static Location CurrentHitLoc(Location start, Vector direction, double maxDistance, List<Entity> ignoreEntities) {
        Vector dir = direction.clone().normalize().multiply(maxDistance);
        Vec3D startPos = new Vec3D(start.getX(), start.getY(), start.getZ());
        Vec3D endPos = new Vec3D(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
        for (Entity i : start.getWorld().getNearbyEntities(start, maxDistance, maxDistance, maxDistance)) {
            if (ignoreEntities.contains(i)) continue;
            net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) i).getHandle();
            AxisAlignedBB bd = nmsEntity.getBoundingBox();
            MovingObjectPosition intercept = bd.b(startPos, endPos);
            if (intercept != null) {
                return new Location(start.getWorld(), intercept.pos.x, intercept.pos.y, intercept.pos.z);
            }
        }
        return rayTraceBlock(start, direction, maxDistance);
    }
}
