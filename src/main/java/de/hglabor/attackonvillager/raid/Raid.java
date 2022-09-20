package de.hglabor.attackonvillager.raid;

import de.hglabor.attackonvillager.raid.wave.KillVillagersWave;
import de.hglabor.attackonvillager.raid.wave.RobVillagersWave;
import de.hglabor.attackonvillager.utils.VillagerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public class Raid {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final double SEARCH_RADIUS = 100;
    private final ServerWorld world;
    private final ServerBossBar bossBar = new ServerBossBar(Text.empty(), BossBar.Color.RED, BossBar.Style.PROGRESS);
    private final Random random = new Random();
    private final Set<UUID> participants = new HashSet<>();
    private final List<ItemStack> winLoot = new ArrayList<>();
    private final Config config;

    public static class Config {
        public UUID leader;
        public ChunkPos chunkPos;
        public BlockPos center;
        public Set<BlockPos> blocks;
        public boolean isActive;
        public boolean isWon;
        public AbstractWave currentWave;
    }

    public Raid(ServerWorld world, Config config) {
        this.config = config;
        this.config.currentWave.raid = this;
        this.world = world;
    }

    public Raid(ServerWorld world, UUID leader, ChunkPos chunkPos, BlockPos center, Set<BlockPos> blocks) {
        this.config = new Config();
        this.config.leader = leader;
        this.config.chunkPos = chunkPos;
        this.config.center = center;
        this.config.blocks = blocks;
        this.config.currentWave = random.nextBoolean() ? new RobVillagersWave(this) : new KillVillagersWave(this);
        this.world = world;
        this.participants.add(leader);
    }

    public void start() {
        this.config.isActive = true;
        strikeLightning(this.config.center, true);
        generateVillagerLoot();
        this.config.currentWave.start();
    }

    public void tick() {
        if (this.config.isActive) {
            this.config.currentWave.tick();
            trackBossBar();
        }
    }

    public void end() {
        this.config.isActive = false;
        bossBar.clearPlayers();
        this.config.isWon = true;
        save();
        //RaidManager.INSTANCE.removeRaid(chunkPos);
    }

    public boolean addParticipant(PlayerEntity participant) {
        return participants.add(participant.getUuid());
    }

    public Set<UUID> getParticipants() {
        return participants;
    }

    public Random getRandom() {
        return random;
    }

    public static double getSearchRadius() {
        return SEARCH_RADIUS;
    }

    public Optional<PlayerEntity> getLeader() {
        return Optional.ofNullable(world.getPlayerByUuid(this.config.leader));
    }

    public List<PlayerEntity> getOnlineParticipants() {
        return participants.stream().map(world::getEntity).filter(Objects::nonNull).map(entity -> (PlayerEntity) entity).collect(Collectors.toList());
    }

    private void generateVillagerLoot() {
        for (Entity entity : getWorld().getOtherEntities(null, Box.from(Vec3d.ofCenter(getCenter())).expand(Raid.getSearchRadius()))) {
            if (entity instanceof VillagerEntity villager) {
                List<ItemStack> professionItems = VillagerUtils.createItemPoolFromProfession(entity, villager.getVillagerData().getProfession());
                SimpleInventory villagerInventory = villager.getInventory();
                for (int i = 0; i < random.nextInt(villagerInventory.size()); i++) {
                    villagerInventory.setStack(random.nextInt(villagerInventory.size()), professionItems.get(random.nextInt(professionItems.size())));
                    winLoot.add(professionItems.get(random.nextInt(professionItems.size())));
                }
            }
        }
        winLoot.add(Items.GOAT_HORN.getDefaultStack());
    }

    public void strikeLightning(BlockPos pos, Boolean cosmetic) {
        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
        lightningEntity.refreshPositionAfterTeleport(pos.getX(), pos.getY(), pos.getZ());
        lightningEntity.setCosmetic(cosmetic);
        world.spawnEntity(lightningEntity);
    }

    public void trackBossBar() {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.getPos().distanceTo(Vec3d.of(this.config.center)) <= 100) {
                bossBar.addPlayer(player);
            } else {
                bossBar.removePlayer(player);
            }
        }
    }

    public void onInteractEntity(PlayerEntity player, LivingEntity entity) {
        this.config.currentWave.onInteractEntity(player, entity);
    }

    public void onEntityDeath(LivingEntity entity) {
        this.config.currentWave.onEntityDeath(entity);
    }

    public void onBlockBreak(BlockPos pos, PlayerEntity player) {
        this.config.currentWave.onBlockBreak(pos, player);
    }

    public void onGoatHorn(ServerWorld world, PlayerEntity user, Hand hand) {
        this.config.currentWave.onGoatHorn(world, user, hand);
    }

    public AbstractWave getCurrentWave() {
        return this.config.currentWave;
    }

    protected void setCurrentWave(AbstractWave currentWave) {
        this.config.currentWave = currentWave;
    }

    public UUID getLeaderUUID() {
        return this.config.leader;
    }

    public ChunkPos getChunkPos() {
        return this.config.chunkPos;
    }

    public BlockPos getCenter() {
        return this.config.center;
    }

    public Set<BlockPos> getBlocks() {
        return this.config.blocks;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public ServerBossBar getBossBar() {
        return bossBar;
    }

    public List<ItemStack> getWinLoot() {
        return winLoot;
    }

    public boolean isWon() {
        return this.config.isWon;
    }

    public void setActive(boolean active) {
        this.config.isActive = active;
    }

    public boolean isActive() {
        return this.config.isActive;
    }

    public void setWon(boolean won) {
        this.config.isWon = won;
    }

    public void save() {
        LOGGER.info("Trying to save raid " + this.config.chunkPos);
        String json = RaidManager.GSON.toJson(config);
        try {
            Files.writeString(new File(RaidManager.INSTANCE.getRaidDirectory() + "/" + RaidManager.INSTANCE.chunkPosToString(this.config.chunkPos) + ".json").toPath(), json, StandardCharsets.UTF_8);
            LOGGER.info("Successfully saved raid " + this.config.chunkPos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
