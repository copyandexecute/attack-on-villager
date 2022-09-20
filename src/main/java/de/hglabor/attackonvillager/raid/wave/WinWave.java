package de.hglabor.attackonvillager.raid.wave;

import de.hglabor.attackonvillager.AttackOnVillagerClient;
import de.hglabor.attackonvillager.raid.AbstractWave;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.WaveType;
import de.hglabor.attackonvillager.raid.defense.DefenseMethod;
import de.hglabor.attackonvillager.utils.VillagerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.TimeUnit;

public class WinWave extends AbstractWave {
    public WinWave(Raid raid) {
        super(raid, WaveType.WIN);
    }

    @Override
    public void start() {
        super.start();
        runTaskLater(raid::end, 5, TimeUnit.SECONDS);
        for (PlayerEntity player : raid.getOnlineParticipants()) {
            player.addStatusEffect(new StatusEffectInstance(AttackOnVillagerClient.VILLAIN_OF_THE_VILLAGE_EFFECT, 24000, 1)); // 24000 ticks = 1 minecraft day
        }
        itemRain();
    }

    private void itemRain() {
        BlockPos pos = raid.getCenter().withY(VillagerUtils.getHighestY(raid.getWorld(), raid.getCenter().getX(), raid.getCenter().getZ()));
        raid.getWorld().setBlockState(pos, Blocks.CHEST.getDefaultState());
        BlockState blockState = raid.getWorld().getBlockState(pos);
        //TODO chest befüllen machen

       /* runTaskTimer(new Task() {
            //private final BlockPos pos = raid.getCenter().withY(VillagerUtils.getHighestY(raid.getWorld(), raid.getCenter().getX(), raid.getCenter().getZ()));
            private final BlockPos pos = raid.getLeader().get().getBlockPos();
            private final Stack<ItemStack> loot = VillagerUtils.toStack(raid.getWinLoot());
            private double angle;

            @Override
            public void run() {
                if (loot.isEmpty()) {
                    cancel();
                    return;
                }
                if (angle < 360) {
                    ServerWorld world = raid.getWorld();
                    ItemEntity entity = new ItemEntity(world, 0, 0, 0, loot.pop());
                    entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
                    double radius = 0.3;
                    entity.addVelocity(radius * Math.sin(angle), 0.4, radius * Math.cos(angle));
                    world.spawnEntity(entity);
                    world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1, 1);
                    angle += 0.6;
                } else {
                    angle = 0;
                }
            }
        }, 200, TimeUnit.MILLISECONDS); */
    }

    @Override
    public void initDefenseMethods() {
        defenseMethods.add(100, DefenseMethod.NOTHING);
    }

    @Override
    public void initRaiders() {
    }

    @Override
    public AbstractWave nextWave() {
        return null;
    }

    @Override
    public WaveType getWaveType() {
        return WaveType.WIN;
    }

    @Override
    public void updateBossBar() {
        raid.getBossBar().setName(Text.translatable("raid.wave.win.victory"));
        raid.getBossBar().setPercent(1f);
    }
}
