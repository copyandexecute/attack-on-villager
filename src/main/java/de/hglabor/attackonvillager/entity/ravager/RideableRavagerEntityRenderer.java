package de.hglabor.attackonvillager.entity.ravager;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.RavagerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.RavagerEntity;

public class RideableRavagerEntityRenderer extends RavagerEntityRenderer {
    public RideableRavagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    protected void scale(RavagerEntity entity, MatrixStack matrices, float amount) {
        super.scale(entity, matrices, amount);
    }
}
