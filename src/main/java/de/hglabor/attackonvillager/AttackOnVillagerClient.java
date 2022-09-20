package de.hglabor.attackonvillager;

import com.mojang.brigadier.context.CommandContext;
import de.hglabor.attackonvillager.effect.VillainOfTheVillageEffect;
import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.entity.golem.GuardianGolemEntity;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.RaidManager;
import de.hglabor.attackonvillager.raid.wave.RobVillagersWave;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;

public final class AttackOnVillagerClient implements ClientModInitializer, ModInitializer {
    public static final String MOD_ID = "attackonvillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final StatusEffect VILLAIN_OF_THE_VILLAGE_EFFECT = new VillainOfTheVillageEffect();

    @Override
    public void onInitializeClient() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(MOD_ID, "villain_of_the_village_effect"), VILLAIN_OF_THE_VILLAGE_EFFECT);
        ModEntities.init();
        VillageManager.INSTANCE.init();
        RaidManager.INSTANCE.init();
        ServerTickEvents.START_WORLD_TICK.register(VillageManager.INSTANCE);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> AttackOnVillagerServer.SERVER = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> RaidManager.INSTANCE.saveRaids());
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("test")
                    .executes(ctx -> {
                        testCommand(ctx);
                        return 1;
                    }));
            dispatcher.register(literal("test2")
                    .executes(ctx -> {
                        testCommand2(ctx);
                        return 1;
                    }));
        });
    }

    public void testCommand(CommandContext<ServerCommandSource> ctx) {
        GuardianGolemEntity ironGolemEntity = new GuardianGolemEntity(ModEntities.GUARDIAN_GOLEM, ctx.getSource().getWorld());
        boolean b = ctx.getSource().getWorld().spawnEntity(ironGolemEntity);
        ironGolemEntity.teleport(ctx.getSource().getEntity().getX(),ctx.getSource().getEntity().getY(),ctx.getSource().getEntity().getZ());
        ironGolemEntity.equipStack(EquipmentSlot.MAINHAND, Blocks.COBBLESTONE.asItem().getDefaultStack());
    }

    public void testCommand2(CommandContext<ServerCommandSource> ctx) {
        for (GuardianGolemEntity guardianGolemEntity : ctx.getSource().getWorld().getEntitiesByType(ModEntities.GUARDIAN_GOLEM, Objects::nonNull)) {
            guardianGolemEntity.throwBlock();
        }
    }

    public class Dummy {
        private String test;
        private int yo;

        public Dummy(String test, int yo) {
            this.test = test;
            this.yo = yo;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public int getYo() {
            return yo;
        }

        public void setYo(int yo) {
            this.yo = yo;
        }
    }
}
