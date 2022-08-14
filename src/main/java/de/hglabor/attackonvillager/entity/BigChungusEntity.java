package de.hglabor.attackonvillager.entity;

import de.hglabor.attackonvillager.mixin.world.entity.EntityAccessor;
import de.hglabor.attackonvillager.mixin.world.entity.LivingEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.StructureTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BigChungusEntity extends RabbitEntity implements ItemSteerable {
    private static final TrackedData<Boolean> SADDLED;
    private static final TrackedData<Integer> BOOST_TIME;
    private final SaddledComponent saddledComponent;
    public boolean highJump = false;

    public BigChungusEntity(EntityType<? extends BigChungusEntity> thisType, World world) {
        super(thisType, world);
        this.saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
    }

    public static DefaultAttributeContainer.Builder createBigChungusAttributes() {
        return RabbitEntity.createRabbitAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SADDLED, false);
        this.dataTracker.startTracking(BOOST_TIME, 0);
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
        SADDLED = DataTracker.registerData(BigChungusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        BOOST_TIME = DataTracker.registerData(BigChungusEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
