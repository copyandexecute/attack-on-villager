package de.hglabor.attackonvillager.entity.pillager;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;

public class ModifiedPillagerEntity extends PillagerEntity {
    public ModifiedPillagerEntity(EntityType<? extends PillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(3, new MoveToRaidCenterGoal<RaiderEntity>(this));
        //this.goalSelector.add(5, new CelebrateGoal(this)); //TODO mit access widener
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new RaiderEntity.PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(3, new CrossbowAttackGoal<PillagerEntity>(this, 1.0, 8.0f));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0f));
        this.targetSelector.add(1, new RevengeGoal(this,  RaiderEntity.class, PlayerEntity.class).setGroupRevenge());
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }
}
