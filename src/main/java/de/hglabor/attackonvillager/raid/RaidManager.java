package de.hglabor.attackonvillager.raid;

import com.mojang.datafixers.util.Pair;
import de.hglabor.attackonvillager.VillageManager;
import de.hglabor.attackonvillager.events.EntityDeathEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public final class RaidManager implements EntityDeathEvent {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RaidManager INSTANCE = new RaidManager();
    private final Map<ChunkPos, Raid> raids = new HashMap<>();

    private RaidManager() {
    }

    public void init() {
        EntityDeathEvent.EVENT.register(this);
    }

    public Raid getOrCreateRaid(ChunkPos chunkPos, BlockPos blockPos, PlayerEntity leader, Set<BlockPos> blocks) {
        if (raids.containsKey(chunkPos)) {
            return raids.get(chunkPos);
        } else {
            Raid raid = new Raid((ServerWorld) leader.getWorld(), leader.getUuid(), chunkPos, blockPos, blocks);
            raids.put(chunkPos, raid);
            raid.start();
            return raid;
        }
    }

    @Override
    public void onEntityDeath(LivingEntity entity) {
        Pair<ChunkPos, BlockPos> nearestVillage = VillageManager.INSTANCE.getNearestVillage((ServerWorld) entity.getWorld(), entity, 100);
        if (nearestVillage != null) {
            Raid raid = raids.get(nearestVillage.getFirst());
            if (raid != null) {
                raid.onEntityDeath(entity);
            }
        }
    }
}
