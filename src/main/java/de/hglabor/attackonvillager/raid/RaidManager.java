package de.hglabor.attackonvillager.raid;

import com.mojang.datafixers.util.Pair;
import de.hglabor.attackonvillager.AttackOnVillagerServer;
import de.hglabor.attackonvillager.VillageManager;
import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntity;
import de.hglabor.attackonvillager.events.AdvancementDoneEvent;
import de.hglabor.attackonvillager.events.EntityDeathEvent;
import de.hglabor.attackonvillager.events.GoatHornEvent;
import de.hglabor.attackonvillager.events.InteractEntityEvent;
import de.hglabor.attackonvillager.events.VillagerDamageEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public final class RaidManager implements EntityDeathEvent, ServerTickEvents.StartWorldTick, PlayerBlockBreakEvents.After, GoatHornEvent, InteractEntityEvent, VillagerDamageEvent, AdvancementDoneEvent {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RaidManager INSTANCE = new RaidManager();
    private final Map<ChunkPos, Raid> raids = new HashMap<>();
    private static final String OMINOUS_BANNER_TRANSLATION_KEY = "block.minecraft.ominous_banner";

    private RaidManager() {
    }

    public void init() {
        EntityDeathEvent.EVENT.register(this);
        ServerTickEvents.START_WORLD_TICK.register(this);
        InteractEntityEvent.EVENT.register(this);
        VillagerDamageEvent.EVENT.register(this);
        AdvancementDoneEvent.EVENT.register(this);
        GoatHornEvent.EVENT.register(this);
    }

    public void removeRaid(ChunkPos pos) {
        raids.remove(pos);
    }

    public Raid getOrCreateRaid(ChunkPos chunkPos, BlockPos blockPos, PlayerEntity leader, Chunk chunk) {
        if (raids.containsKey(chunkPos)) {
            return raids.get(chunkPos);
        } else {
            Raid raid = new Raid((ServerWorld) leader.getWorld(), leader.getUuid(), chunkPos, blockPos, VillageManager.INSTANCE.getVillageBlocks(chunk));
            raids.put(chunkPos, raid);
            raid.start();
            return raid;
        }
    }

    @Override
    public void onEntityDeath(LivingEntity entity) {
        Pair<ChunkPos, BlockPos> nearestVillage = VillageManager.INSTANCE.getNearestVillage((ServerWorld) entity.getWorld(), entity, (int) Raid.getSearchRadius());
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
                raid.onBlockBreak(pos, player);
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

    /**
     * kinda scuffed
     */
    public boolean isOmniousBanner(ItemStack itemStack) {
        if (!itemStack.isOf(Items.WHITE_BANNER)) return false;
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) return false;
        NbtCompound display = (NbtCompound) nbt.get("display");
        if (display == null) return false;
        NbtString name = (NbtString) display.get("Name");
        if (name == null) return false;
        return name.toString().contains(OMINOUS_BANNER_TRANSLATION_KEY);
    }

    @Override
    public void onVillagerDamage(VillagerEntity entity, DamageSource source) {
        var attacker = source.getAttacker();
        if (attacker == null) return;
        if (attacker.getType() != EntityType.PLAYER) return;
        var player = (PlayerEntity) attacker;
        Pair<ChunkPos, BlockPos> nearestVillage = VillageManager.INSTANCE.getNearestVillage(AttackOnVillagerServer.SERVER.getWorld(player.getWorld().getRegistryKey()), entity, 100);
        if (nearestVillage != null) {
            Raid raid = raids.get(nearestVillage.getFirst());
            if (raid != null) {
                raid.addParticipant(player);
            }
        }
    }

    @Override
    public void onAdvancementDone(PlayerEntity player, Advancement advancement, String criterionName) {
        if ("raid_captain".equalsIgnoreCase(criterionName)) {
            RideableRavagerEntity ravagerEntity = ModEntities.RIDEABLE_RAVAGER.create(player.getWorld());
            ravagerEntity.setPosition(player.getPos());
            player.getWorld().spawnEntity(ravagerEntity);
            player.sendMessage(Text.of("Du hast einen Ravager bekommen!"));
        }
    }



    @Override
    public void onGoatHorn(World world, PlayerEntity user, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            user.sendMessage(Text.of("hiy"));
        }
    }
}
