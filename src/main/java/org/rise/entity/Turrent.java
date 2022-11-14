package org.rise.entity;

import net.minecraft.server.v1_12_R1.*;

public class Turrent extends EntitySkeleton {

    public Turrent(World world) {
        super(world);
    }

    @Override
    protected void r() {

    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public ItemStack getEquipment(EnumItemSlot enumItemSlot) {
        return null;
    }

    @Override
    public void setSlot(EnumItemSlot enumItemSlot, ItemStack itemStack) {

    }

    @Override
    public EnumMainHand getMainHand() {
        return null;
    }
}
