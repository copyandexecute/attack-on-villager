package de.hglabor.attackonvillager.mixin;

import de.hglabor.attackonvillager.VillageManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
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

import java.util.HashSet;
import java.util.function.Predicate;

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
            StructureStart structureStart = structureAccessor.getStructureStarts(chunkPos, Predicate.isEqual((Object) this)).get(0);
            for (StructurePiece piece : pieces.pieces()) {
                for (int x = piece.getBoundingBox().getMinX(); x <= piece.getBoundingBox().getMaxX(); ++x) {
                    for (int y = piece.getBoundingBox().getMinY(); y <= piece.getBoundingBox().getMaxY(); ++y) {
                        for (int z = piece.getBoundingBox().getMinZ(); z <= piece.getBoundingBox().getMaxZ(); ++z) {
                            VillageManager.VILLAGE_BLOCKS.computeIfAbsent(structureStart.getPos(), pos -> new HashSet<>()).add(new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }
    }
}
