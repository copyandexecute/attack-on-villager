package de.hglabor.attackonvillager.raid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.util.Pair;
import de.hglabor.attackonvillager.AttackOnVillagerServer;
import de.hglabor.attackonvillager.VillageManager;
import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntity;
import de.hglabor.attackonvillager.events.*;
import de.hglabor.attackonvillager.raid.wave.DestroyHousesWave;
import de.hglabor.attackonvillager.raid.wave.KillVillagersWave;
import de.hglabor.attackonvillager.raid.wave.RobVillagersWave;
import de.hglabor.attackonvillager.raid.wave.WinWave;
import de.hglabor.attackonvillager.utils.RuntimeTypeAdapterFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
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
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public final class RaidManager implements EntityDeathEvent, ServerTickEvents.StartWorldTick, PlayerBlockBreakEvents.After, GoatHornEvent, InteractEntityEvent, VillagerDamageEvent, AdvancementDoneEvent {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RaidManager INSTANCE = new RaidManager();
    private static final String OMINOUS_BANNER_TRANSLATION_KEY = "block.minecraft.ominous_banner";
    private final Map<ChunkPos, Raid> raids = new HashMap<>();
    private File raidDirectory;

    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(AbstractWave.class)
                    .registerSubtype(DestroyHousesWave.class)
                    .registerSubtype(KillVillagersWave.class)
                    .registerSubtype(WinWave.class)
                    .registerSubtype(RobVillagersWave.class)
            )
            .setPrettyPrinting()
            .create();

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
            Raid savedRaid = findSavedRaid(chunkPos);
            if (savedRaid != null) {
                LOGGER.info("current wave " + savedRaid.getCurrentWave().getWaveType());
                raids.put(chunkPos, savedRaid);
                return savedRaid;
            } else {
                Raid raid = new Raid((ServerWorld) leader.getWorld(), leader.getUuid(), chunkPos, blockPos, VillageManager.INSTANCE.getVillageBlocks(chunk));
                raids.put(chunkPos, raid);
                raid.start();
                return raid;
            }
        }
    }

    public void saveRaids() {
        raids.values().forEach(Raid::save);
        raids.clear();
    }

    private Raid findSavedRaid(ChunkPos chunkPos) {
        File[] files = raidDirectory.listFiles();
        if (files == null) return null;
        for (File file : files) {
            if (file.getName().startsWith(chunkPosToString(chunkPos))) {
                return fromFile(file);
            }
        }
        return null;
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
            player.sendMessage(Text.translatable("advancement.raid_captain.ravagerSpawned"));
        }
    }


    @Override
    public void onGoatHorn(World world, PlayerEntity user, Hand hand) {
        ServerWorld serverWorld = AttackOnVillagerServer.SERVER.getWorld(user.getWorld().getRegistryKey());
        Pair<ChunkPos, BlockPos> nearestVillage = VillageManager.INSTANCE.getNearestVillage(serverWorld, user, (int) Raid.getSearchRadius());
        if (nearestVillage != null) {
            Raid raid = raids.get(nearestVillage.getFirst());
            if (raid != null) {
                raid.onGoatHorn(serverWorld, user, hand);
            }
        }
    }

    public String chunkPosToString(ChunkPos chunkPos) {
        return String.format("%s_%s", chunkPos.x, chunkPos.z);
    }

    public Raid fromFile(File file) {
        ServerWorld overworld = AttackOnVillagerServer.SERVER.getOverworld();
        try {
            Raid.Config config = RaidManager.GSON.fromJson(new JsonReader(new FileReader(file)), Raid.Config.class);
            return new Raid(overworld, config);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setRaidDirectory(File raidDirectory) {
        this.raidDirectory = raidDirectory;
    }

    public File getRaidDirectory() {
        return raidDirectory;
    }
}
