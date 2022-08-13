package de.hglabor.attackonvillager;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

public class AttackOnVillager implements ModInitializer {
    public static final String MOD_ID = "attackonvillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("foo")
                    .requires(source -> source.hasPermissionLevel(4))
                    .executes(ctx -> {
                        ServerWorld world = ctx.getSource().getWorld();

                        Registry<Structure> registry = world.getRegistryManager().get(Registry.STRUCTURE_KEY);
                        RegistryEntryList<?> registryEntryList = RegistryEntryList.of(registry.getEntry)
                        Pair<BlockPos, RegistryEntry<Structure>> pair = world.getChunkManager().getChunkGenerator().locateStructure(
                                world,
                                registryEntryList,
                                blockPos,
                                100,
                                false
                        );

                        world.getStructureAccessor().getStructureAt()
                        ctx.getSource().sendFeedback(Text.literal("You are an operator"), false);
                        return 1;
                    }));
        });
    }

    private static Optional<? extends RegistryEntryList.ListBacked<Structure>> getStructureListForPredicate(RegistryPredicateArgumentType.RegistryPredicate<Structure> predicate, Registry<Structure> structureRegistry) {
        return predicate.getKey().map(key -> structureRegistry.getEntry(key).map(RegistryEntryList::of), structureRegistry::getEntryList);
    }
}
