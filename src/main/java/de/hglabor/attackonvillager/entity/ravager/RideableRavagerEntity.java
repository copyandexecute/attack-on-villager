package de.hglabor.attackonvillager.entity.ravager;

import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.entity.canon.CanonEntity;
import de.hglabor.attackonvillager.mixin.world.entity.EntityAccessor;
import de.hglabor.attackonvillager.mixin.world.entity.LivingEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RideableRavagerEntity extends RavagerEntity implements ItemSteerable, JumpingMount {
    private static final TrackedData<Boolean> SADDLED;
    private static final TrackedData<Integer> BOOST_TIME;
    private static final TrackedData<Integer> CANON_STRENGTH;
    private final SaddledComponent saddledComponent;

    public RideableRavagerEntity(EntityType<? extends RideableRavagerEntity> thisType, World world) {
        super(thisType, world);
        this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.4));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(2, new RevengeGoal(this, RaiderEntity.class, PlayerEntity.class).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createRidableRavagerAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5).add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SADDLED, false);
        this.dataTracker.startTracking(BOOST_TIME, 0);
        this.dataTracker.startTracking(CANON_STRENGTH, 0);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.hasPassengers() && !player.shouldCancelInteraction()) {
            if (!this.world.isClient) {
                player.startRiding(this);
            }
            return ActionResult.success(this.world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }


    public void travel(Vec3d movementInput) {
        this.travel(this, this.saddledComponent, movementInput);
    }

    @Override
    public boolean travel(MobEntity entity, SaddledComponent saddledEntity, Vec3d movementInput) {
        if (!entity.isAlive()) {
            return false;
        }
        Entity entity2 = entity.getPrimaryPassenger();
        if (!entity.hasPassengers() || !(entity2 instanceof PlayerEntity)) {
            entity.stepHeight = 0.5f;
            entity.airStrafingSpeed = 0.02f;
            this.setMovementInput(movementInput);
            return false;
        }
        entity.setYaw(entity2.getYaw());
        entity.prevYaw = entity.getYaw();
        entity.setPitch(entity2.getPitch() * 0.5f);
        ((EntityAccessor) entity).invokeSetRotation(entity.getYaw(), entity.getPitch());
        entity.bodyYaw = entity.getYaw();
        entity.headYaw = entity.getYaw();
        entity.stepHeight = 1.0f;
        entity.airStrafingSpeed = entity.getMovementSpeed() * 0.1f;
        if (saddledEntity.boosted && saddledEntity.boostedTime++ > saddledEntity.currentBoostTime) {
            saddledEntity.boosted = false;
        }
        if (entity.isLogicalSideForUpdatingMovement()) {
            float f = this.getSaddledSpeed();
            if (saddledEntity.boosted) {
                f += f * 1.15f * MathHelper.sin((float) saddledEntity.boostedTime / (float) saddledEntity.currentBoostTime * (float) Math.PI);
            }
            entity.setMovementSpeed(f);
            this.setMovementInput(new Vec3d(((PlayerEntity) entity2).sidewaysSpeed * 0.5f, movementInput.y, ((PlayerEntity) entity2).forwardSpeed));
            ((LivingEntityAccessor) entity).setBodyTrackingIncrements(0);
            if (getCanonStrength() > 0) {
                entity.setVelocity(Vec3d.ZERO);
            }
        } else {
            entity.updateLimbs(entity, false);
            entity.setVelocity(Vec3d.ZERO);
        }
        ((EntityAccessor) entity).invokeTryCheckBlockCollision();
        return true;
    }

    public boolean consumeOnAStickItem() {
        return false;
    }

    public void setMovementInput(Vec3d movementInput) {
        super.travel(movementInput);
    }

    public @Nullable Entity getPrimaryPassenger() {
        return this.getFirstPassenger();
    }

    public float getSaddledSpeed() {
        return 0.3F;
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    static {
        SADDLED = DataTracker.registerData(RideableRavagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BOOST_TIME = DataTracker.registerData(RideableRavagerEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CANON_STRENGTH = DataTracker.registerData(RideableRavagerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    @Override
    public void setJumpStrength(int strength) {
    }

    public void setCanonStrength(int strength) {
        this.dataTracker.set(CANON_STRENGTH, strength);
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public void startJumping(int height) {
        double rotX = this.getYaw();
        double rotY = this.getPitch();
        double xz = Math.cos(Math.toRadians(rotY));
        Vec3d vector = new Vec3d(-xz * Math.sin(Math.toRadians(rotX)), -Math.sin(Math.toRadians(rotY)), xz * Math.cos(Math.toRadians(rotX)));

        CanonEntity canonEntity = new CanonEntity(ModEntities.CANON, this.getWorld());
        canonEntity.setPosition(getEyePos());
        canonEntity.setVelocity(vector.multiply(3));
        canonEntity.setPower(height);

        //TODO ravager head yaw/pitch towards player
        this.tryAttack(canonEntity);
        this.getWorld().spawnEntity(canonEntity);
    }

    public int getCanonStrength() {
        return this.dataTracker.get(CANON_STRENGTH);
    }


    @Override
    public void stopJumping() {
    }
}
