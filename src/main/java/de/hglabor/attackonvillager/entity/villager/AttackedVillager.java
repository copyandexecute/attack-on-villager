package de.hglabor.attackonvillager.entity.villager;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public interface AttackedVillager {
    default ItemStack getWeapon() {
        return Items.AIR.getDefaultStack();
    }

    void setWeapon(ItemStack weapon);

    void playAttackSound();

    void shoot(LivingEntity enemy, float progress);
}
