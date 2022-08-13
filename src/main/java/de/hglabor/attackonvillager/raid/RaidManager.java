package de.hglabor.attackonvillager.raid;

import com.mojang.datafixers.util.Pair;
import de.hglabor.attackonvillager.VillageManager;
import de.hglabor.attackonvillager.events.EntityDeathEvent;
import de.hglabor.attackonvillager.events.InteractEntityEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public final class RaidManager implements EntityDeathEvent, ServerTickEvents.StartWorldTick, PlayerBlockBreakEvents.After, InteractEntityEvent {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RaidManager INSTANCE = new RaidManager();
    private final Map<ChunkPos, Raid> raids = new HashMap<>();

    private RaidManager() {
    }

    public void init() {
        EntityDeathEvent.EVENT.register(this);
        ServerTickEvents.START_WORLD_TICK.register(this);
        InteractEntityEvent.EVENT.register(this);
    }

    public void removeRaid(ChunkPos pos) {
        raids.remove(pos);
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

    @Override
    public void onStartTick(ServerWorld world) {
        if (world.equals(world.getServer().getOverworld())) {
            raids.values().forEach(Raid::tick);
        }
    }

    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        Pair<ChunkPos, BlockPos> nearestVillage = VillageManager.INSTANCE.getNearestVillage((ServerWorld) player.getWorld(), player, 100);
        if (nearestVillage != null) {
            Raid raid = raids.get(nearestVillage.getFirst());
            if (raid != null) {
                raid.onBlockBreak(pos);
            }
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (entity instanceof LivingEntity livingEntity) {
            Pair<ChunkPos, BlockPos> nearestVillage = VillageManager.INSTANCE.getNearestVillage(entity.getCommandSource().getWorld(), entity, 100);
            if (nearestVillage != null) {
                Raid raid = raids.get(nearestVillage.getFirst());
                if (raid != null) {
                    raid.onInteractEntity(player, livingEntity);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.SUCCESS;
    }
}