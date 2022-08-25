package de.hglabor.attackonvillager;

import com.mojang.datafixers.util.Pair;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.RaidManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
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

public final class VillageManager implements ServerTickEvents.StartWorldTick {
    private VillageManager() {
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final VillageManager INSTANCE = new VillageManager();
    public static final Map<ChunkPos, Set<BlockPos>> VILLAGE_BLOCKS = new HashMap<>();

    public void init() {
    }

    public Pair<ChunkPos, BlockPos> getNearestVillage(ServerWorld world, Entity entity, int radius) {
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

        return pair != null ? Pair.of(world.getChunk(pair.getFirst()).getPos(), pair.getFirst()) : null;
    }

    @Override
    public void onStartTick(ServerWorld world) {
        if (world.equals(world.getServer().getOverworld())) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (!RaidManager.INSTANCE.isOmniousBanner(player.getEquippedStack(EquipmentSlot.HEAD))) continue;
                Pair<ChunkPos, BlockPos> nearestVillage = getNearestVillage(world, player, 100);
                if (nearestVillage == null) continue;
                Raid raid = RaidManager.INSTANCE.getOrCreateRaid(
                        nearestVillage.getFirst(),
                        nearestVillage.getSecond(),
                        player,
                        VILLAGE_BLOCKS.getOrDefault(nearestVillage.getFirst(), new HashSet<>())
                );
            }
        }
    }
}
