package de.hglabor.attackonvillager;

import com.mojang.brigadier.context.CommandContext;
import de.hglabor.attackonvillager.effect.VillainOfTheVillageEffect;
import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntity;
import de.hglabor.attackonvillager.entity.villager.goals.VillagerBowAttackGoal;
import de.hglabor.attackonvillager.mixin.world.entity.MobEntityAccessor;
import de.hglabor.attackonvillager.raid.RaidManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public final class AttackOnVillagerClient implements ClientModInitializer, ModInitializer {
    public static final String MOD_ID = "attackonvillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final StatusEffect VILLAIN_OF_THE_VILLAGE_EFFECT = new VillainOfTheVillageEffect();

    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("foo")
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(Text.literal("Executed"), false);
                        test(ctx);
                        return 1;
                    }));
        });
        Registry.register(Registry.STATUS_EFFECT, new Identifier(MOD_ID, "villain_of_the_village_effect"), VILLAIN_OF_THE_VILLAGE_EFFECT);
        ModEntities.init();
        VillageManager.INSTANCE.init();
        RaidManager.INSTANCE.init();
        ServerTickEvents.START_WORLD_TICK.register(VillageManager.INSTANCE);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> AttackOnVillagerServer.SERVER = server);
    }

    private void test(CommandContext<ServerCommandSource> ctx) {
    }

    @Override
    public void onInitialize() {

    }
}
