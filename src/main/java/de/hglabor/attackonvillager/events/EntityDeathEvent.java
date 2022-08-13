package de.hglabor.attackonvillager.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

public interface EntityDeathEvent {
    Event<EntityDeathEvent> EVENT = EventFactory.createArrayBacked(EntityDeathEvent.class,
            listeners -> (entity) -> {
                for (EntityDeathEvent listener : listeners) {
                    listener.onEntityDeath(entity);
                }
            }
    );

    void onEntityDeath(LivingEntity entity);
}
