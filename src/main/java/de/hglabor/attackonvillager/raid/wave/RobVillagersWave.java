package de.hglabor.attackonvillager.raid.wave;

import de.hglabor.attackonvillager.raid.AbstractWave;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.WaveType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    public void start() {
        super.start();
        villagersToRob = ((random.nextInt(40, 80) * villagers.size()) / 100);
    }

    @Override
    public AbstractWave nextWave() {
        return random.nextBoolean() ? new KillVillagersWave(raid) : new DestroyHousesWave(raid);
    }

    @Override
    public WaveType getWaveType() {
        return WaveType.ROB_VILLAGERS;
    }

    @Override
    public void onInteractEntity(PlayerEntity player, LivingEntity entity) {
        if (entity instanceof InventoryOwner invEntity) {
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
        }
    }

    @Override
    public void updateBossBar() {
        raid.getBossBar().setName(Text.of("Raid - Rob Villagers: " + (villagersToRob - robbed.size())));
        raid.getBossBar().setPercent(1f - (float) robbed.size() / villagersToRob);
    }
}
