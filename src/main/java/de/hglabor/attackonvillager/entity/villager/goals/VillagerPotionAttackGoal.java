package de.hglabor.attackonvillager.entity.villager.goals;

import de.hglabor.attackonvillager.entity.villager.AttackedVillager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;

import java.util.EnumSet;

public class VillagerPotionAttackGoal<T extends VillagerEntity> extends Goal {
    private final T actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int targetSeeingTicker;
    private boolean movingToLeft;
    private boolean backward;
    private int combatTicks = -1;

    public VillagerPotionAttackGoal(T actor, double speed, int attackInterval, float range) {
        this.actor = actor;
        this.speed = speed;
        this.attackInterval = attackInterval;
        this.squaredRange = range * range;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public void setAttackInterval(int attackInterval) {
        this.attackInterval = attackInterval;
    }

    @Override
    public boolean canStart() {
        if (this.actor.getTarget() == null) {
            return false;
        }
        return this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return (this.canStart() || !this.actor.getNavigation().isIdle()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        this.actor.setAttacking(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.actor.setAttacking(false);
        this.targetSeeingTicker = 0;
        this.cooldown = -1;
        this.actor.clearActiveItem();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        boolean bl2;
        LivingEntity livingEntity = this.actor.getTarget();
        if (livingEntity == null) {
            return;
        }
        double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
        boolean bl3 = bl2 = this.targetSeeingTicker > 0;
        if (bl != bl2) {
            this.targetSeeingTicker = 0;
        }
        if (bl) {
            ++this.targetSeeingTicker;
        } else {
            --this.targetSeeingTicker;
        }
        if (d > (double) this.squaredRange || this.targetSeeingTicker < 20) {
            this.actor.getNavigation().startMovingTo(livingEntity, this.speed);
            this.combatTicks = -1;
        } else {
            this.actor.getNavigation().stop();
            ++this.combatTicks;
        }
        if (this.combatTicks >= 20) {
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                boolean bl4 = this.movingToLeft = !this.movingToLeft;
            }
            if ((double) this.actor.getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;
            }
            this.combatTicks = 0;
        }
        if (this.combatTicks > -1) {
            if (d > (double) (this.squaredRange * 0.75f)) {
                this.backward = false;
            } else if (d < (double) (this.squaredRange * 0.25f)) {
                this.backward = true;
            }
            this.actor.getMoveControl().strafeTo(this.backward ? -0.5f : 0.5f, this.movingToLeft ? 0.5f : -0.5f);
            this.actor.lookAtEntity(livingEntity, 30.0f, 30.0f);
        } else {
            this.actor.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
        }
        if (!bl && this.targetSeeingTicker < -60) {
            this.actor.clearActiveItem();
        } else if (bl && cooldown > attackInterval) {
            this.actor.clearActiveItem();
            ((AttackedVillager) this.actor).throwPotion(livingEntity);
            this.cooldown = 0;
        }
        ++cooldown;
    }
}
