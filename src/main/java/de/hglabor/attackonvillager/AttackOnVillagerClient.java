package de.hglabor.attackonvillager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackOnVillagerClient implements ClientModInitializer {
    public static final String MOD_ID = "attackonvillager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        VillageManager.INSTANCE.init();
        PlayerBlockBreakEvents.AFTER.register(VillageManager.INSTANCE);
    }
}
