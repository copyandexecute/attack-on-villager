package de.hglabor.attackonvillager.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(Structure.class)
public abstract class StructureMixin {

    @Shadow
    public abstract StructureType<?> getType();

    /**
     * @author norisk
     * @reason weil
     */
    @Overwrite
    public void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, net.minecraft.util.math.random.Random random, BlockBox box, ChunkPos chunkPos, StructurePiecesList pieces) {
        if (this.getType() == StructureType.JIGSAW) {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            for (StructurePiece piece : pieces.pieces()) {
                if (piece.getType() == StructurePieceType.JIGSAW) {
                    for (int x = piece.getBoundingBox().getMinX(); x <= piece.getBoundingBox().getMaxX(); ++x) {
                        for (int y = piece.getBoundingBox().getMinY(); y <= piece.getBoundingBox().getMaxY(); ++y) {
                            for (int z = piece.getBoundingBox().getMinZ(); z <= piece.getBoundingBox().getMaxZ(); ++z) {
                                BlockPos blockPos = new BlockPos(x, y, z);
                                scheduledExecutorService.scheduleWithFixedDelay(() -> MinecraftClient.getInstance().execute(() -> {
                                    world.setBlockState(blockPos, Blocks.DIAMOND_BLOCK.getDefaultState(), Block.SKIP_LIGHTING_UPDATES);
                                }), 0, 20, TimeUnit.SECONDS);
                            }
                        }
                    }
                }
            }
        }
    }
}
