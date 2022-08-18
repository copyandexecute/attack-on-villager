package de.hglabor.attackonvillager.raid;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractWave {
    protected final Raid raid;
    protected final Random random = new Random();
    protected final Set<UUID> villagers = new HashSet<>();

    protected AbstractWave(Raid raid) {
        this.raid = raid;
    }

    protected final void startNextWave() {
        raid.getBossBar().setPercent(1f);
        raid.setCurrentWave(nextWave());
        raid.getCurrentWave().start();
    }

    public void tick() {
        updateBossBar();
    }

    public void start() {
        detectVillagers();
    }

    public abstract AbstractWave nextWave();

    public abstract WaveType getWaveType();

    public abstract void updateBossBar();

    private void detectVillagers() {
        for (Entity entity : raid.getWorld().getOtherEntities(null, Box.from(Vec3d.ofCenter(raid.getCenter())).expand(300))) {
            if (entity instanceof VillagerEntity villager) {
                villagers.add(villager.getUuid());
            }
        }
    }

    public void onEntityDeath(LivingEntity entity) {
    }

    public void onBlockBreak(BlockPos pos) {
    }

    public void onInteractEntity(PlayerEntity player, LivingEntity entity) {
    }

    public void runTaskLater(Runnable runnable, int delay, TimeUnit unit) {
        Executors.newScheduledThreadPool(1).schedule(() -> raid.getWorld().getServer().execute(runnable), delay, unit);
    }
}
