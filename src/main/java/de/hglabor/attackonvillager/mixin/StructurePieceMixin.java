package de.hglabor.attackonvillager.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin {

    @ModifyVariable(method = "addBlock", at = @At("HEAD"), ordinal = 0)
    private BlockState injected(BlockState y) {
        return Blocks.DIAMOND_BLOCK.getDefaultState();
    }
}
