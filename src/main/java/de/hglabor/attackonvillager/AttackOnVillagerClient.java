package de.hglabor.attackonvillager;

import com.mojang.brigadier.context.CommandContext;
import de.hglabor.attackonvillager.entity.ravager.RideableRavagerEntity;
import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.raid.RaidManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public final class AttackOnVillagerClient implements ClientModInitializer {
    public static final String MOD_ID = "attackonvillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
        ModEntities.init();
        VillageManager.INSTANCE.init();
        RaidManager.INSTANCE.init();
        ServerTickEvents.START_WORLD_TICK.register(VillageManager.INSTANCE);
    }

    private void test(CommandContext<ServerCommandSource> ctx) {
        RideableRavagerEntity rideableRavager = new RideableRavagerEntity(ModEntities.RIDEABLE_RAVAGER, ctx.getSource().getWorld());
        rideableRavager.teleport(ctx.getSource().getPosition().getX(), ctx.getSource().getPosition().getY(), ctx.getSource().getPosition().getZ());
        ctx.getSource().sendMessage(Text.of(String.valueOf(ctx.getSource().getWorld().spawnEntity(rideableRavager))));
    }
}
