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
    private final Set<UUID> villagers = new HashSet<>();
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
        for (Entity entity : raid.getWorld().getOtherEntities(null, Box.from(Vec3d.ofCenter(raid.getCenter())).expand(300))) {
            if (entity instanceof VillagerEntity villager) {
                if (villagers.add(villager.getUuid())) {
                    List<ItemStack> professionItems = createItemPoolFromProfessions(entity, villager.getVillagerData().getProfession());
                    SimpleInventory villagerInventory = villager.getInventory();
                    for (int i = 0; i < random.nextInt(villagerInventory.size()); i++) {
                        villagerInventory.setStack(random.nextInt(villagerInventory.size()), professionItems.get(random.nextInt(professionItems.size())));
                    }
                }
            }
        }
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

    /**
     * todo I dont know what this does but it does something.
     */
    private List<ItemStack> createItemPoolFromProfessions(Entity entity, @Nullable VillagerProfession profession) {
        List<ItemStack> pool = new ArrayList<>();
        if (profession == null || profession.id().equals(VillagerProfession.NONE.id()) || profession.id().equals(VillagerProfession.NITWIT.id())) {
            profession = getRandomVillagerProfession();
        }
        Int2ObjectMap<TradeOffers.Factory[]> items = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession);
        items.forEach((integer, factories) -> {
            for (TradeOffers.Factory factory : factories) {
                TradeOffer tradeOffer = factory.create(entity, Random.create());
                if (tradeOffer != null) {
                    pool.add(tradeOffer.getSellItem());
                }
            }
        });
        return pool;
    }

    private VillagerProfession getRandomVillagerProfession() {
        Optional<RegistryEntry<VillagerProfession>> profession = Registry.VILLAGER_PROFESSION.getRandom(Random.create());
        if (profession.isPresent()) {
            if (profession.get().value().id().equals(VillagerProfession.NONE.id()) || profession.get().value().id().equals(VillagerProfession.NITWIT.id())) {
                return getRandomVillagerProfession();
            } else {
                return profession.get().value();
            }
        } else {
            return getRandomVillagerProfession();
        }
    }

    @Override
    public void onInteractEntity(PlayerEntity player, LivingEntity entity) {
        if (entity instanceof InventoryOwner invEntity) {
            player.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return Text.of("Inventory");
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
