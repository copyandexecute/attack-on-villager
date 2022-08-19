package de.hglabor.attackonvillager.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public final class VillagerUtils {
    private VillagerUtils() {
    }

    /**
     * todo I dont know what this does but it does something.
     */
    public static List<ItemStack> createItemPoolFromProfession(Entity entity, @Nullable VillagerProfession profession) {
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

    //ey heightmap hat irgendwie nicht funktioniert
    public static int getHighestY(World world, int x, int z) {
        BlockPos.Mutable pos = new BlockPos.Mutable(x, world.getHeight(), z);
        for (int i = world.getDimension().height(); i > world.getDimension().minY(); i--) {
            pos.setY(i);
            System.out.println(i);
            BlockState blockState = world.getBlockState(pos);
            if (blockState.isAir()) continue;
            if (blockState.getBlock() instanceof LeavesBlock) continue;
            return i + 1;
        }
        return 64;
    }

    public static <E> Stack<E> toStack(List<E> list) {
        Stack<E> stack = new Stack<>();
        stack.addAll(list);
        return stack;
    }
}
