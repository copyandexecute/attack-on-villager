package de.hglabor.attackonvillager.entity.golem;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GuardianGolemEntity extends IronGolemEntity {
    public GuardianGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public void throwBlock() {
        FallingBlockEntity block = FallingBlockEntity.spawnFromBlock(getWorld(), getBlockPos().add(0,3,0), Blocks.COBBLESTONE.getDefaultState());
        block.addVelocity(0,3,0);
    }
}
