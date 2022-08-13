package de.hglabor.attackonvillager;

import de.hglabor.attackonvillager.raid.RaidManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AttackOnVillagerClient implements ClientModInitializer {
    public static final String MOD_ID = "attackonvillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        VillageManager.INSTANCE.init();
        RaidManager.INSTANCE.init();
        PlayerBlockBreakEvents.AFTER.register(VillageManager.INSTANCE);
        ServerTickEvents.START_WORLD_TICK.register(VillageManager.INSTANCE);
    }
}
