package de.hglabor.attackonvillager.entity.canon;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class CanonEntity extends ExplosiveProjectileEntity {
    private int power = 2;

    public CanonEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createCanonAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.world.isClient) {
            boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
            this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), getPower(), bl, bl ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE);
            this.discard();
        }
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}
