package de.hglabor.attackonvillager.raid.wave;

import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.entity.pillager.ModifiedIllusionerEntity;
import de.hglabor.attackonvillager.entity.pillager.ModifiedPillagerEntity;
import de.hglabor.attackonvillager.entity.pillager.ModifiedVindicatorEntity;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntity;
import de.hglabor.attackonvillager.raid.AbstractWave;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.WaveType;
import de.hglabor.attackonvillager.raid.defense.DefenseMethod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class KillVillagersWave extends AbstractWave {
    public KillVillagersWave(Raid raid) {
        super(raid);
    }

    @Override
    public void start() {
        super.start();
        spawnPillagers(random.nextInt(3, 10));
    }

    @Override
    public void initDefenseMethods() {
        this.defenseMethods
                .add(10, DefenseMethod.IRON_GOLEM_RIDING)
                .add(30, DefenseMethod.PANICK)
                .add(60, DefenseMethod.ATTACK);
    }

    @Override
    public void initRaiders() {
        this.raiders
                .add(15, () -> new ModifiedIllusionerEntity(EntityType.ILLUSIONER, raid.getWorld()))
                .add(5, () -> new RideableRavagerEntity(ModEntities.RIDEABLE_RAVAGER, raid.getWorld()))
                .add(60, () -> new ModifiedPillagerEntity(EntityType.PILLAGER, raid.getWorld()))
                .add(20, () -> new ModifiedVindicatorEntity(EntityType.VINDICATOR, raid.getWorld()));
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
