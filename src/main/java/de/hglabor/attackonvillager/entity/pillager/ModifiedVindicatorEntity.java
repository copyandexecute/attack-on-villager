package de.hglabor.attackonvillager.entity.pillager;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;

public class ModifiedVindicatorEntity extends VindicatorEntity {
    public ModifiedVindicatorEntity(EntityType<? extends VindicatorEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        //this.goalSelector.add(1, new BreakDoorGoal(this));
        this.goalSelector.add(2, new IllagerEntity.LongDoorInteractGoal(this));
        this.goalSelector.add(3, new RaiderEntity.PatrolApproachGoal(this, 10.0f));
        //this.goalSelector.add(4, new AttackGoal(this));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(3, new ActiveTargetGoal<MerchantEntity>((MobEntity) this, MerchantEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<IronGolemEntity>((MobEntity) this, IronGolemEntity.class, true));
        //this.targetSelector.add(4, new TargetGoal(this));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }
}
