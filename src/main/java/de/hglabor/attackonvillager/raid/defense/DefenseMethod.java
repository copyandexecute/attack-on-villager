package de.hglabor.attackonvillager.raid.defense;

import de.hglabor.attackonvillager.mixin.world.entity.MobEntityAccessor;
import de.hglabor.attackonvillager.raid.Raid;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;

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
