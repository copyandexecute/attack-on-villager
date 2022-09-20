package de.hglabor.attackonvillager;

import de.hglabor.attackonvillager.entity.ModEntities;
import de.hglabor.attackonvillager.raid.RaidManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class AttackOnVillagerServer implements DedicatedServerModInitializer { // just for compatibility with server-side
    public static MinecraftServer SERVER;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> RaidManager.INSTANCE.saveRaids());
        ModEntities.init();
    }
}
