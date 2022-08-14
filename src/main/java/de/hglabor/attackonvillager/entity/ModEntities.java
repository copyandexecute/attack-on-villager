package de.hglabor.attackonvillager.entity;

import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntity;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public class ModEntities {
    public static final EntityType<RideableRavagerEntity> RIDEABLE_RAVAGER;

    public ModEntities() {
    }

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "rideable_ravager"), RIDEABLE_RAVAGER);
        FabricDefaultAttributeRegistry.register(RIDEABLE_RAVAGER, RideableRavagerEntity.createBigChungusAttributes());
        EntityRendererRegistry.register(ModEntities.RIDEABLE_RAVAGER, RideableRavagerEntityRenderer::new);
    }

    static {
        RIDEABLE_RAVAGER = FabricEntityTypeBuilder.create(SpawnGroup.MISC, RideableRavagerEntity::new).dimensions(EntityDimensions.fixed(1.95f, 2.2f)).trackRangeChunks(8).build();
    }
}
