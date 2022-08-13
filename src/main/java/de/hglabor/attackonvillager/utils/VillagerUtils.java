package de.hglabor.attackonvillager.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class VillagerUtils {
    private VillagerUtils() {
    }

    /**
     * todo I dont know what this does but it does something.
     */
    public static List<ItemStack> createItemPoolFromProfessions(Entity entity, @Nullable VillagerProfession profession) {
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

    private static VillagerProfession getRandomVillagerProfession() {
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
}
