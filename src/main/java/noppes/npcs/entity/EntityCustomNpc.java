package noppes.npcs.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.v1_12_R1.*;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class EntityCustomNpc extends EntityCreature implements IEntityAdditionalSpawnData {
    public EntityCustomNpc(World world) {
        super(world);
    }

    private static MinecraftKey minecraftKey;

    static {
        minecraftKey = new MinecraftKey("Entity_Custom_Npc");
        EntityTypes.d.add(minecraftKey);
        EntityTypes.b.a(101, minecraftKey, EntityCustomNpc.class);
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

    @Override
    public void writeSpawnData(ByteBuf byteBuf) {

    }

    @Override
    public void readSpawnData(ByteBuf byteBuf) {

    }
}
