package de.hglabor.attackonvillager.entity.golem;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;

public class GuardianGolemEntityModel<T extends GuardianGolemEntity> extends IronGolemEntityModel<T> {
    public GuardianGolemEntityModel(ModelPart root) {
        super(root);
    }
}
