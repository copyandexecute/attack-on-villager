package de.hglabor.attackonvillager.mixin.world.entity;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("bodyTrackingIncrements")
    void setBodyTrackingIncrements(int bodyTrackingIncrements);
}
