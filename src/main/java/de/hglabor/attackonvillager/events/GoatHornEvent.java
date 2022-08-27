package de.hglabor.attackonvillager.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface GoatHornEvent {
    Event<GoatHornEvent> EVENT = EventFactory.createArrayBacked(GoatHornEvent.class,
            listeners -> ((world, user, hand) -> {
                for (GoatHornEvent listener : listeners) {
                    listener.onGoatHorn(world, user, hand);
                }
            })
    );

    void onGoatHorn(World world, PlayerEntity user, Hand hand);
}
