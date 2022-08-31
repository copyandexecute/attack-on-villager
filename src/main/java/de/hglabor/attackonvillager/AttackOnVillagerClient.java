package de.hglabor.attackonvillager;

import de.hglabor.attackonvillager.effect.VillainOfTheVillageEffect;
import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.raid.RaidManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    }

    @Override
    public void onInitialize() {
    }
}
