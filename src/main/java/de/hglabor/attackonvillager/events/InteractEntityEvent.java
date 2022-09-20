package de.hglabor.attackonvillager.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface InteractEntityEvent {
    Event<InteractEntityEvent> EVENT = EventFactory.createArrayBacked(InteractEntityEvent.class,
            (listeners) -> (player, world, hand, entity, hitResult) -> {
                for (InteractEntityEvent event : listeners) {
                    ActionResult result = event.interact(player, world, hand, entity, hitResult);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult);
}
