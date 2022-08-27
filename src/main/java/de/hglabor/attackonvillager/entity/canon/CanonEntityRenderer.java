package de.hglabor.attackonvillager.entity.canon;

import de.hglabor.attackonvillager.entity.ModEntities;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class CanonEntityRenderer extends EntityRenderer<CanonEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/ravager.png");
    private final CanonEntityModel model;

    public CanonEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new CanonEntityModel(context.getPart(ModEntities.CANON_MODEL_LAYER));
    }

    @Override
    protected int getBlockLight(CanonEntity canonEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(CanonEntity canonEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        float h = MathHelper.lerpAngle(canonEntity.prevYaw, canonEntity.getYaw(), g);
        float j = MathHelper.lerp(g, canonEntity.prevPitch, canonEntity.getPitch());
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(canonEntity)));
        //this.model.setHeadRotation(0.0f, h, j);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(canonEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(CanonEntity canonEntity) {
        return TEXTURE;
    }
}

