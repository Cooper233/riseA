package noppes.npcs.entity;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.IProjectile;
import net.minecraft.server.v1_12_R1.World;

public abstract class EntityProjectile extends Entity implements IProjectile {
    private EntityLiving thrower;

    public EntityProjectile(World world) {
        super(world);
    }

    public EntityLiving func_85052_h() {
        return thrower;
    }
}