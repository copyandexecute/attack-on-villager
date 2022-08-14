package de.hglabor.attackonvillager.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.RabbitEntity;

public class BigChungusEntityRenderer extends RabbitEntityRenderer {
    public BigChungusEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    protected void scale(RabbitEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(3.0F, 3.0F, 3.0F);
        super.scale(entity, matrices, amount);
    }
}
