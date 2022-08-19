package de.hglabor.attackonvillager.raid.wave;

import de.hglabor.attackonvillager.raid.AbstractWave;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.WaveType;
import de.hglabor.attackonvillager.raid.defense.DefenseMethod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class DestroyHousesWave extends AbstractWave {
    private final Set<BlockPos> villageBlocks = raid.getBlocks().stream().filter(blockPos -> !raid.getWorld().getBlockState(blockPos).isAir()).collect(Collectors.toUnmodifiableSet());
    private final int blocksToDestroy = ((new Random().nextInt(15, 65) * villageBlocks.size()) / 100);

    public DestroyHousesWave(Raid raid) {
        super(raid);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void initDefenseMethods() {
        defenseMethods
                .add(50, DefenseMethod.NOTHING)
                .add(50, DefenseMethod.SPAWN_IRON_GOLEMS);
    }

    @Override
    public void tick() {
        super.tick();
        if (getDestroyedBlocks().size() >= blocksToDestroy) {
            startNextWave();
        }
    }

    @Override
    public AbstractWave nextWave() {
        return new WinWave(raid);
    }

    @Override
    public WaveType getWaveType() {
        return WaveType.DESTROY_HOUSES;
    }

    public Set<BlockPos> getDestroyedBlocks() {
        return villageBlocks.stream().filter(pos -> raid.getWorld().getBlockState(pos).isAir()).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void onBlockBreak(BlockPos pos, PlayerEntity player) {
        raid.addParticipant(player);
    }

    @Override
    public void updateBossBar() {
        raid.getBossBar().setName(Text.of("Überfall - Blöcke übrig: " + (blocksToDestroy - getDestroyedBlocks().size())));
        raid.getBossBar().setPercent(1f - (float) getDestroyedBlocks().size() / blocksToDestroy);
    }
}
