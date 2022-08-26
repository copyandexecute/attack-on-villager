package de.hglabor.attackonvillager.entity.villager;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3f;

import java.util.Arrays;
import java.util.List;

public class VillagerHeldWeaponFeatureRenderer<T extends VillagerEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private final HeldItemRenderer heldItemRenderer;
    private static final List<Item> SWORDS = Arrays.asList(Items.DIAMOND_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.WOODEN_SWORD);

    public VillagerHeldWeaponFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack weapon = ((AttackedVillager) livingEntity).getWeapon();
        if (!weapon.isOf(Items.AIR)) {
            matrixStack.push();
            if (SWORDS.contains(weapon.getItem())) {
                matrixStack.translate(0.0, -0.15f, -0.4f);
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-45));
                this.heldItemRenderer.renderItem(livingEntity, weapon, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, false, matrixStack, vertexConsumerProvider, i);
            } else {
                matrixStack.translate(0.0, 0.1f, -0.6f);
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
                this.heldItemRenderer.renderItem(livingEntity, weapon, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, false, matrixStack, vertexConsumerProvider, i);
            }
            matrixStack.pop();
        }
    }
}
