package de.hglabor.attackonvillager;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public final class VillageManager implements PlayerBlockBreakEvents.After {
    private VillageManager() {
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final VillageManager INSTANCE = new VillageManager();
    public static final Map<ChunkPos, Set<BlockPos>> VILLAGE_BLOCKS = new HashMap<>();

    public void init() {
    }

    public ChunkPos getNearestVillage(ServerWorld world, Entity entity, int radius) {
        Registry<Structure> registry = world.getRegistryManager().get(Registry.STRUCTURE_KEY);

        List<RegistryEntry<Structure>> villages = new ArrayList<>();

        for (RegistryEntry<Structure> indexedEntry : registry.getIndexedEntries()) {
            indexedEntry.getKey().ifPresent(structureRegistryKey -> {
                if (structureRegistryKey.getValue().getPath().contains("village")) {
                    villages.add(indexedEntry);
                }
            });
        }

        RegistryEntryList<Structure> villagesEntryList = RegistryEntryList.of(villages);

        Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator().locateStructure(
                world,
                villagesEntryList,
                entity.getBlockPos(),
                radius,
                false
        );

        return pair != null ? world.getChunk(pair.getFirst()).getPos() : null;
    }

    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        ChunkPos nearestVillage = getNearestVillage((ServerWorld) world, player, 100);
        if (nearestVillage == null) {
            LOGGER.info("No Village");
        } else {
            Set<BlockPos> villageBlocks = VILLAGE_BLOCKS.getOrDefault(nearestVillage, new HashSet<>());
            if (villageBlocks.contains(pos)) {
                player.sendMessage(Text.of("Yes"));
            } else {
                player.sendMessage(Text.of("No"));
            }
        }
    }
}
