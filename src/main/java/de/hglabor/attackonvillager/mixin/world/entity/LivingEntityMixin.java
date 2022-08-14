package de.hglabor.attackonvillager.mixin.world.entity;

import de.hglabor.attackonvillager.events.EntityDeathEvent;
import de.hglabor.attackonvillager.raid.RaidManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"))
    private void injectOnDeath(DamageSource damageSource, CallbackInfo ci) {
        EntityDeathEvent.EVENT.invoker().onEntityDeath((LivingEntity) (Object) this);
    }

    @Inject(method = "getPreferredEquipmentSlot", at = @At("RETURN"), cancellable = true)
    private static void injectGetPreferredEquipmentSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (RaidManager.INSTANCE.isOmniousBanner(stack)) {
            cir.setReturnValue(EquipmentSlot.HEAD);
        } else {
            cir.cancel();
        }
    }
}
