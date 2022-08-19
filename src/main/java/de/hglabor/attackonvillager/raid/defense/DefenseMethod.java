package de.hglabor.attackonvillager.raid.defense;

import de.hglabor.attackonvillager.entity.villager.goals.VillagerBowAttackGoal;
import de.hglabor.attackonvillager.mixin.world.entity.MobEntityAccessor;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.utils.VillagerUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;

import java.util.function.BiConsumer;

public enum DefenseMethod {
    IRON_GOLEM_RIDING((raid, villager) -> {
        IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.create(raid.getWorld());
        ironGolemEntity.teleport(villager.getPos().getX(), villager.getPos().getY(), villager.getPos().getZ());
        ((MobEntityAccessor) ironGolemEntity).getTargetSelector().add(2, new ActiveTargetGoal<>(ironGolemEntity, PlayerEntity.class, false));
        ((MobEntityAccessor) ironGolemEntity).getTargetSelector().add(2, new ActiveTargetGoal<>(ironGolemEntity, RaiderEntity.class, false));
        raid.getWorld().spawnEntity(ironGolemEntity);
        villager.startRiding(ironGolemEntity);
    }),

    PANICK((raid, villager) -> {
        ((MobEntityAccessor) villager).getGoalSelector().add(3, new MoveThroughVillageGoal(villager, 1.5, false, 8, () -> true));
    }),

    SPAWN_IRON_GOLEMS((raid, villager) -> {
        BlockPos center = raid.getCenter();
        int x = raid.getRandom().nextBoolean() ? raid.getRandom().nextInt((int) Raid.getSearchRadius()) : -raid.getRandom().nextInt((int) Raid.getSearchRadius());
        int z = raid.getRandom().nextBoolean() ? raid.getRandom().nextInt((int) Raid.getSearchRadius()) : -raid.getRandom().nextInt((int) Raid.getSearchRadius());
        BlockPos.Mutable blockPos = center.add(x, 0, z).mutableCopy();
        int y = VillagerUtils.getHighestY(raid.getWorld(), x, z);
        blockPos.setY(y);

        IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.create(raid.getWorld());
        ironGolemEntity.teleport(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        ((MobEntityAccessor) ironGolemEntity).getTargetSelector().add(2, new ActiveTargetGoal<>(ironGolemEntity, PlayerEntity.class, false));
        ((MobEntityAccessor) ironGolemEntity).getTargetSelector().add(2, new ActiveTargetGoal<>(ironGolemEntity, RaiderEntity.class, false));
        raid.getWorld().spawnEntity(ironGolemEntity);
    }),

    ATTACK((raid, villager) -> {
        VillagerProfession profession = villager.getVillagerData().getProfession();
        if (VillagerProfession.FLETCHER.equals(profession)) {
            ((MobEntityAccessor) villager).getGoalSelector().add(0, new VillagerBowAttackGoal<>(villager, 1.0, 20, 15.0f));
        } else {
            ((MobEntityAccessor) villager).getGoalSelector().add(0, new MeleeAttackGoal(villager, 1.0, false));
        }
        ((MobEntityAccessor) villager).getTargetSelector().add(0, new ActiveTargetGoal<>(villager, PlayerEntity.class, false));
        ((MobEntityAccessor) villager).getTargetSelector().add(1, new ActiveTargetGoal<>(villager, RaiderEntity.class, false));
    }),
    NOTHING((raid, villager) -> {

    });

    private final BiConsumer<Raid, VillagerEntity> villagerEntityBiConsumer;

    DefenseMethod(BiConsumer<Raid, VillagerEntity> villagerEntityBiConsumer) {
        this.villagerEntityBiConsumer = villagerEntityBiConsumer;
    }

    public void defend(Raid raid, VillagerEntity villager) {
        villagerEntityBiConsumer.accept(raid, villager);
    }
}
