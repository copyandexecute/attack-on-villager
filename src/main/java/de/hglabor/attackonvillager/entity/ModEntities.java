package de.hglabor.attackonvillager.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
    public static final EntityType<BigChungusEntity> BIG_CHUNGUS;

    public ModEntities() {
    }

    public static void init() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier("biggercrafts", "big_chungus"), BIG_CHUNGUS);
        FabricDefaultAttributeRegistry.register(BIG_CHUNGUS, BigChungusEntity.createBigChungusAttributes());
    }

    static {
        BIG_CHUNGUS = FabricEntityTypeBuilder.create(SpawnGroup.MISC, BigChungusEntity::new).dimensions(EntityDimensions.fixed(1.2F, 1.5F)).trackRangeChunks(8).build();
    }
}
