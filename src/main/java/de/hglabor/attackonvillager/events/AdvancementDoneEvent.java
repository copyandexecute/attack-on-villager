package de.hglabor.attackonvillager.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;

public interface AdvancementDoneEvent {
    Event<AdvancementDoneEvent> EVENT = EventFactory.createArrayBacked(AdvancementDoneEvent.class,
            listeners -> ((player, advancement, criterionName) -> {
                for (AdvancementDoneEvent listener : listeners) {
                    listener.onAdvancementDone(player, advancement, criterionName);
                }
            })
    );

    void onAdvancementDone(PlayerEntity player, Advancement advancement, String criterionName);
}
