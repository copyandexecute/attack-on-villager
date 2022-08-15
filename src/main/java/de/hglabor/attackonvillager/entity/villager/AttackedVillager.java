package de.hglabor.attackonvillager.entity.villager;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public interface AttackedVillager {
    default ItemStack getWeapon() {
        return Items.DIAMOND_SWORD.getDefaultStack();
    }

    void setWeapon(ItemStack weapon);
}
