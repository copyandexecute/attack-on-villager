package de.hglabor.attackonvillager.entity.canon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

@Environment(value = EnvType.CLIENT)
public class CanonEntityModel extends SinglePartEntityModel<CanonEntity> {
    private final ModelPart root;
    private final ModelPart head;

    public CanonEntityModel(ModelPart root) {
        this.root = root;
        this.head = this.root.getChild(EntityModelPartNames.HEAD);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData head = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f).uv(0, 0).cuboid(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f), ModelTransform.pivot(0.0f, 0, 0));
        head.addChild(EntityModelPartNames.RIGHT_HORN, ModelPartBuilder.create().uv(74, 55).cuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), ModelTransform.of(-10.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        head.addChild(EntityModelPartNames.LEFT_HORN, ModelPartBuilder.create().uv(74, 55).mirrored().cuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), ModelTransform.of(8.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        head.addChild(EntityModelPartNames.MOUTH, ModelPartBuilder.create().uv(0, 36).cuboid(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f), ModelTransform.pivot(0.0f, -2.0f, 2.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(CanonEntity ravagerEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float) Math.PI / 180);
        this.head.yaw = i * ((float) Math.PI / 180);
    }

    @Override
    public void animateModel(CanonEntity ravagerEntity, float f, float g, float h) {
        super.animateModel(ravagerEntity, f, g, h);
    }

}
