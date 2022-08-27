package de.hglabor.attackonvillager.raid;

import de.hglabor.attackonvillager.entity.pillager.ModifiedPillagerEntity;
import de.hglabor.attackonvillager.raid.defense.DefenseMethod;
import de.hglabor.attackonvillager.utils.RandomCollection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractWave {
    protected final Raid raid;
    protected final Random random = new Random();
    protected final Set<UUID> villagers = new HashSet<>();

    protected AbstractWave(Raid raid) {
        this.raid = raid;
    }

    protected final RandomCollection<DefenseMethod> defenseMethods = new RandomCollection<>();
    protected final RandomCollection<Supplier<RaiderEntity>> raiders = new RandomCollection<>();

    protected final void startNextWave() {
        raid.getBossBar().setPercent(1f);
        raid.setCurrentWave(nextWave());
        raid.getCurrentWave().start();
    }

    public void tick() {
        updateBossBar();
    }

    public void start() {
        initDefenseMethods();
        detectVillagers();
        defendVillagers();
    }

    public abstract void initDefenseMethods();

    public abstract void initRaiders();

    public abstract AbstractWave nextWave();

    public abstract WaveType getWaveType();

    public abstract void updateBossBar();

    private void detectVillagers() {
        for (Entity entity : raid.getWorld().getOtherEntities(null, Box.from(Vec3d.ofCenter(raid.getCenter())).expand(Raid.getSearchRadius()))) {
            if (entity instanceof VillagerEntity villager) {
                villagers.add(villager.getUuid());
                //villager.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 30 * 20));
            }
        }
    }

    protected void defendVillagers() {
        for (UUID villager : villagers) {
            Entity entity = raid.getWorld().getEntity(villager);
            if (entity instanceof VillagerEntity villagerEntity) {
                defenseMethods.next().defend(raid, villagerEntity);
            }
        }
    }

    protected void spawnPillagers(int amount) {
        int delay = 0;
        for (int i = 0; i < amount; i++) {
            runTaskLater(() -> {
                ModifiedPillagerEntity pillager = new ModifiedPillagerEntity(EntityType.PILLAGER, raid.getWorld());
                Vec3d position = raid.getLeader().get().getPos().add(0, 2, 0);
                pillager.setPosition(position);
                pillager.initialize(raid.getWorld(), raid.getWorld().getLocalDifficulty(new BlockPos(position)), SpawnReason.NATURAL, null, null);
                raid.getWorld().spawnEntity(pillager);
                raid.getWorld().playSound(null, new BlockPos(position), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1f, 1f);
            }, delay, TimeUnit.MILLISECONDS);
            delay += 200;
        }
    }

    public RandomCollection<DefenseMethod> getDefenseMethods() {
        return defenseMethods;
    }

    public void onEntityDeath(LivingEntity entity) {
    }

    public void onBlockBreak(BlockPos pos, PlayerEntity player) {
    }

    public void onInteractEntity(PlayerEntity player, LivingEntity entity) {
    }

    public void runTaskLater(Runnable runnable, int delay, TimeUnit unit) {
        Executors.newScheduledThreadPool(1).schedule(() -> raid.getWorld().getServer().execute(runnable), delay, unit);
    }

    public void runTaskTimer(Task runnable, int period, TimeUnit unit) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(() -> raid.getWorld().getServer().execute(runnable), 0, period, unit);
        runnable.setFuture(scheduledFuture);
    }

    public void onGoatHorn(ServerWorld world, PlayerEntity user, Hand hand) {
        for (UUID uuid : villagers) {
            Entity entity = world.getEntity(uuid);
            if (entity instanceof VillagerEntity villager) {
                villager.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 3 * 20));
            }
        }
    }

    //Lasst mich
    protected abstract static class Task implements Runnable {
        private ScheduledFuture<?> future;

        protected final void cancel() {
            future.cancel(true);
        }

        private void setFuture(ScheduledFuture<?> future) {
            this.future = future;
        }
    }
}
