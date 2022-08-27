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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RobVillagersWave extends AbstractWave {
    private final Set<UUID> robbed = new HashSet<>();
    private int villagersToRob;

    public RobVillagersWave(Raid raid) {
        super(raid);
    }

    @Override
    public void tick() {
        super.tick();
        if (robbed.size() >= villagersToRob) {
            startNextWave();
        }
    }

    @Override
    public void initDefenseMethods() {
        this.defenseMethods
                .add(15, DefenseMethod.IRON_GOLEM_RIDING)
                .add(30, DefenseMethod.PANICK)
                .add(55, DefenseMethod.ATTACK);
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
    public void start() {
        super.start();
        villagersToRob = ((random.nextInt(40, 80) * villagers.size()) / 100);
        spawnPillagers(random.nextInt(2, 10));
    }

    @Override
    public AbstractWave nextWave() {
        return new KillVillagersWave(raid);
    }

    @Override
    public WaveType getWaveType() {
        return WaveType.ROB_VILLAGERS;
    }

    @Override
    public void onInteractEntity(PlayerEntity player, LivingEntity entity) {
        if (entity instanceof InventoryOwner invEntity) {
            //Ich weiß... LOL
            runTaskLater(() -> {
                player.openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public Text getDisplayName() {
                        return Text.of("Villager Inventory");
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, inv, invEntity.getInventory(), 4);
                    }
                });
                robbed.add(entity.getUuid());

            }, 1, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void updateBossBar() {
        raid.getBossBar().setName(Text.of("Raid - Rob Villagers: " + (villagersToRob - robbed.size())));
        raid.getBossBar().setPercent(1f - (float) robbed.size() / villagersToRob);
    }
}
