package de.hglabor.attackonvillager.entity.golem;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3f;

public class GuardianGolemBlockFeatureRenderer extends FeatureRenderer<GuardianGolemEntity, GuardianGolemEntityModel<GuardianGolemEntity>> {
    private final BlockRenderManager blockRenderManager;

    public GuardianGolemBlockFeatureRenderer(FeatureRendererContext<GuardianGolemEntity, GuardianGolemEntityModel<GuardianGolemEntity>> context, BlockRenderManager blockRenderManager) {
        super(context);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, GuardianGolemEntity ironGolemEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack equippedStack = ironGolemEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        BlockState blockState = null;
        if (equippedStack.getItem() instanceof BlockItem blockItem) {
            blockState = blockItem.getBlock().getDefaultState();
        }
        if (blockState == null) {
            return;
        }
        matrixStack.push();
        ModelPart modelPart = this.getContextModel().getRightArm();
        modelPart.rotate(matrixStack);
        matrixStack.translate(-1.1875, 1.0625, -0.9375);
        matrixStack.translate(0.5, 0.5, 0.5);
        float m = 0.9f;
        matrixStack.scale(m, m, m);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
        matrixStack.translate(-0.5, -0.5, -0.5);
        this.blockRenderManager.renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}