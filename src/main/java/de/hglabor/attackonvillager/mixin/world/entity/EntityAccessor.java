package de.hglabor.attackonvillager.mixin.world.entity;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("setRotation")
    void invokeSetRotation(float yaw, float pitch);

    @Invoker("tryCheckBlockCollision")
    void invokeTryCheckBlockCollision();
}
