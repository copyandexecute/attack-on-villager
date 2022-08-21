package de.hglabor.attackonvillager.entity;

import de.hglabor.attackonvillager.entity.canon.CanonEntity;
import de.hglabor.attackonvillager.entity.canon.CanonEntityModel;
import de.hglabor.attackonvillager.entity.canon.CanonEntityRenderer;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntity;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public class ModEntities {
    public static final EntityType<RideableRavagerEntity> RIDEABLE_RAVAGER;
    public static final EntityType<CanonEntity> CANON;
    public static final EntityModelLayer CANON_MODEL_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "canon"), "main");

    public ModEntities() {
    }

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "rideable_ravager"), RIDEABLE_RAVAGER);
        Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "canon"), CANON);
        FabricDefaultAttributeRegistry.register(RIDEABLE_RAVAGER, RideableRavagerEntity.createRidableRavagerAttributes());
        FabricDefaultAttributeRegistry.register(EntityType.CAT, CanonEntity.createCanonAttributes());
        EntityRendererRegistry.register(ModEntities.RIDEABLE_RAVAGER, RideableRavagerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.CANON, CanonEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CANON_MODEL_LAYER, CanonEntityModel::getTexturedModelData);
    }

    static {
        RIDEABLE_RAVAGER = FabricEntityTypeBuilder.create(SpawnGroup.MISC, RideableRavagerEntity::new).dimensions(EntityDimensions.fixed(1.95f, 2.2f)).trackRangeChunks(8).build();
        CANON = FabricEntityTypeBuilder.create(SpawnGroup.MISC, CanonEntity::new).dimensions(EntityDimensions.fixed(1, 1)).trackRangeChunks(8).build();
    }
}
