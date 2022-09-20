package de.hglabor.attackonvillager.entity.golem;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class GuardianGolemEntityRenderer extends MobEntityRenderer<GuardianGolemEntity, GuardianGolemEntityModel<GuardianGolemEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/iron_golem/iron_golem.png");

    public GuardianGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new GuardianGolemEntityModel<>(context.getPart(EntityModelLayers.IRON_GOLEM)), 0.7f);
        this.addFeature(new GuardianGolemBlockFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(GuardianGolemEntity ironGolemEntity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(GuardianGolemEntity guardianGolem, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(guardianGolem, matrixStack, f, g, h);
        if ((double) guardianGolem.limbDistance < 0.01) {
            return;
        }
        float i = 13.0f;
        float j = guardianGolem.limbAngle - guardianGolem.limbDistance * (1.0f - h) + 6.0f;
        float k = (Math.abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f;
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(6.5f * k));
    }
}