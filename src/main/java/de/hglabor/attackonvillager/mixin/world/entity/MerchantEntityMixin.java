package de.hglabor.attackonvillager.mixin.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin {
    @Mutable
    @Shadow
    @Final
    private SimpleInventory inventory;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectConstructor(EntityType<?> entityType, World world, CallbackInfo ci) {
        this.inventory = new SimpleInventory(36);
    }
}
