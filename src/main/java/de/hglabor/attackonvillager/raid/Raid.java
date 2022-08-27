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
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public class Raid {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final double SEARCH_RADIUS = 100;
    private final UUID leader;
    private final ChunkPos chunkPos;
    private final BlockPos center;
    private final Set<BlockPos> blocks;
    private final ServerWorld world;
    private final ServerBossBar bossBar = new ServerBossBar(Text.empty(), BossBar.Color.RED, BossBar.Style.PROGRESS);
    private final Random random = new Random();
    private AbstractWave currentWave;
    private boolean isActive;
    private boolean isWon;
    private final Set<UUID> participants = new HashSet<>();
    private final List<ItemStack> winLoot = new ArrayList<>();

    public Raid(ServerWorld world, UUID leader, ChunkPos chunkPos, BlockPos center, Set<BlockPos> blocks) {
        this.leader = leader;
        this.chunkPos = chunkPos;
        this.center = center;
        this.blocks = blocks;
        this.world = world;
        this.participants.add(leader);
        this.currentWave = random.nextBoolean() ? new RobVillagersWave(this) : new KillVillagersWave(this);
    }

    public void start() {
        isActive = true;
        strikeLightning(center, true);
        generateVillagerLoot();
        currentWave.start();
    }

    public void tick() {
        if (isActive) {
            currentWave.tick();
            trackBossBar();
        }
    }

    public void end() {
        isActive = false;
        bossBar.clearPlayers();
        isWon = true;
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
      return Optional.ofNullable(world.getPlayerByUuid(leader));
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
            if (player.getPos().distanceTo(Vec3d.of(center)) <= 100) {
                bossBar.addPlayer(player);
            } else {
                bossBar.removePlayer(player);
            }
        }
    }

    public void onInteractEntity(PlayerEntity player, LivingEntity entity) {
        currentWave.onInteractEntity(player, entity);
    }

    public void onEntityDeath(LivingEntity entity) {
        currentWave.onEntityDeath(entity);
    }

    public void onBlockBreak(BlockPos pos, PlayerEntity player) {
        currentWave.onBlockBreak(pos, player);
    }

    public void onGoatHorn(World world, PlayerEntity user, Hand hand) {
        currentWave.onGoatHorn( world,  user,  hand);
    }

    public AbstractWave getCurrentWave() {
        return currentWave;
    }

    protected void setCurrentWave(AbstractWave currentWave) {
        this.currentWave = currentWave;
    }

    public UUID getLeaderUUID() {
        return leader;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public BlockPos getCenter() {
        return center;
    }

    public Set<BlockPos> getBlocks() {
        return blocks;
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
        return isWon;
    }
}
