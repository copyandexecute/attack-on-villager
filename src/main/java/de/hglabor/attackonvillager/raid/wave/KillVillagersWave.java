package de.hglabor.attackonvillager.raid.wave;

import de.hglabor.attackonvillager.raid.AbstractWave;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.WaveType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class KillVillagersWave extends AbstractWave {
    private final Set<UUID> villagers = new HashSet<>();

    public KillVillagersWave(Raid raid) {
        super(raid);
    }

    @Override
    public void start() {
        for (Entity entity : raid.getWorld().getOtherEntities(null, Box.from(Vec3d.ofCenter(raid.getCenter())).expand(300))) {
            if (entity instanceof VillagerEntity villager) {
                villagers.add(villager.getUuid());
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (checkIfAllVillagersAreDead()) {
            startNextWave();
        }
    }

    private int getDeadVillagerCount() {
        var counter = 0;
        for (UUID villager : villagers) {
            Entity entity = raid.getWorld().getEntity(villager);
            if (entity == null || !entity.isAlive()) counter++;
        }
        return counter;
    }

    private boolean checkIfAllVillagersAreDead() {
        for (UUID villager : villagers) {
            Entity entity = raid.getWorld().getEntity(villager);
            if (entity == null) continue;
            if (entity.isAlive()) return false;
        }
        return true;
    }

    @Override
    public void onEntityDeath(LivingEntity entity) {
        if (villagers.contains(entity.getUuid())) {
            if (entity instanceof VillagerEntity villager) {
                for (int i = 0; i < random.nextInt(villager.getInventory().size()); i++) {
                    entity.dropStack(villager.getInventory().getStack(random.nextInt(villager.getInventory().size())));
                }
            }
        }
    }

    @Override
    public AbstractWave nextWave() {
        return new DestroyHousesWave(raid);
    }

    @Override
    public WaveType getWaveType() {
        return WaveType.KILL_VILLAGERS;
    }

    @Override
    public void updateBossBar() {
        raid.getBossBar().setName(Text.of("Raid - Villagers übrig: " + (villagers.size() - getDeadVillagerCount())));
        raid.getBossBar().setPercent(1f - (float) getDeadVillagerCount() / villagers.size());
    }
}
