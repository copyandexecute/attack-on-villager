package de.hglabor.attackonvillager.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;

public interface VillagerDamageEvent {
    Event<VillagerDamageEvent> EVENT = EventFactory.createArrayBacked(VillagerDamageEvent.class,
            listeners -> (entity, source) -> {
                for (VillagerDamageEvent listener : listeners) {
                    listener.onVillagerDamage(entity, source);
                }
            }
    );

    void onVillagerDamage(VillagerEntity entity, DamageSource source);
}
