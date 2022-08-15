package de.hglabor.attackonvillager.mixin.world.entity;

import de.hglabor.attackonvillager.entity.villager.AttackedVillager;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin implements AttackedVillager {
}
