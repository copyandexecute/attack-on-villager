package de.hglabor.attackonvillager;

import de.hglabor.attackonvillager.entity.ModEntities;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class AttackOnVillagerServer implements DedicatedServerModInitializer { // just for compatibility with server-side
    public static MinecraftServer SERVER;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER = server);
        ModEntities.init();
    }
}
