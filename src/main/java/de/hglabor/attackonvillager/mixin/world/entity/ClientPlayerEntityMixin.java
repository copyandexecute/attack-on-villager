package de.hglabor.attackonvillager.mixin.world.entity;

import com.mojang.authlib.GameProfile;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.JumpingMount;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    private float mountJumpStrength;

    @Shadow
    private int field_3938; //jumpRidingTicks

    @Shadow
    public abstract float getMountJumpStrength();

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;mountJumpStrength:F", opcode = Opcodes.PUTFIELD, ordinal = 2))
    private void modifyJumpStrength(ClientPlayerEntity instance, float value) {
        if (this.getVehicle() instanceof RideableRavagerEntity ravager) {
            this.field_3938 = 5;
            this.mountJumpStrength += 0.01;
            ravager.setCanonStrength(Math.min(100, MathHelper.floor(this.getMountJumpStrength() * 100.0f)));
        } else {
            this.mountJumpStrength = this.field_3938 < 10 ? (float) this.field_3938 * 0.1f : 0.8f + 2.0f / (float) (this.field_3938 - 9) * 0.1f;
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;mountJumpStrength:F", shift = At.Shift.AFTER, ordinal = 0))
    private void clearJumpStrength(CallbackInfo ci) {
        if (this.getVehicle() instanceof RideableRavagerEntity ravager) {
            ravager.setCanonStrength(Math.min(100, MathHelper.floor(this.getMountJumpStrength() * 100.0f)));
        }
    }


    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;mountJumpStrength:F", shift = At.Shift.AFTER, ordinal = 1))
    private void clearJumpStrength2(CallbackInfo ci) {
        if (this.getVehicle() instanceof RideableRavagerEntity ravager) {
            ravager.setCanonStrength(Math.min(100, MathHelper.floor(this.getMountJumpStrength() * 100.0f)));
        }
    }
}
