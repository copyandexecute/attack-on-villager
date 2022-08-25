package de.hglabor.attackonvillager.mixin.advancement;

import de.hglabor.attackonvillager.events.AdvancementDoneEvent;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER))
    private void injected(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        AdvancementDoneEvent.EVENT.invoker().onAdvancementDone(this.owner, advancement, criterionName);
    }
}
