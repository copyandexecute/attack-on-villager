package de.hglabor.attackonvillager.raid;

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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.hglabor.attackonvillager.AttackOnVillagerClient.MOD_ID;

public class Raid {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final double SEARCH_RADIUS = 300;
    private final UUID leader;
    private final ChunkPos chunkPos;
    private final BlockPos center;
    private final Set<BlockPos> blocks;
    private final ServerWorld world;
    private final ServerBossBar bossBar = new ServerBossBar(Text.empty(), BossBar.Color.RED, BossBar.Style.PROGRESS);
    private final Random random = new Random();
    private AbstractWave currentWave = new RobVillagersWave(this);
    private boolean isActive;
    private final Set<PlayerEntity> participants = new HashSet<>();

    public Raid(ServerWorld world, UUID leader, ChunkPos chunkPos, BlockPos center, Set<BlockPos> blocks) {
        this.leader = leader;
        this.chunkPos = chunkPos;
        this.center = center;
        this.blocks = blocks;
        this.world = world;
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
        //RaidManager.INSTANCE.removeRaid(chunkPos);
    }

    public void addParticipant(PlayerEntity participant) {
        participants.add(participant);
    }

    public Set<PlayerEntity> getParticipants() {
        return participants;
    }

    private void generateVillagerLoot() {
        for (Entity entity : getWorld().getOtherEntities(null, Box.from(Vec3d.ofCenter(getCenter())).expand(100))) {
            if (entity instanceof VillagerEntity villager) {
                List<ItemStack> professionItems = VillagerUtils.createItemPoolFromProfessions(entity, villager.getVillagerData().getProfession());
                SimpleInventory villagerInventory = villager.getInventory();
                for (int i = 0; i < random.nextInt(villagerInventory.size()); i++) {
                    villagerInventory.setStack(random.nextInt(villagerInventory.size()), professionItems.get(random.nextInt(professionItems.size())));
                }
            }
        }
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

    public void onBlockBreak(BlockPos pos, ServerPlayerEntity player) {
        currentWave.onBlockBreak(pos);
        participants.add(player);
        System.out.println(player.getGameProfile().getName() + " is now a participant");
    }

    public AbstractWave getCurrentWave() {
        return currentWave;
    }

    protected void setCurrentWave(AbstractWave currentWave) {
        this.currentWave = currentWave;
    }

    public UUID getLeader() {
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
}
