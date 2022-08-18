package de.hglabor.attackonvillager.raid.wave;

import de.hglabor.attackonvillager.AttackOnVillagerClient;
import de.hglabor.attackonvillager.raid.AbstractWave;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.WaveType;
import de.hglabor.attackonvillager.raid.defense.DefenseMethod;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.TimeUnit;

public class WinWave extends AbstractWave {
    protected WinWave(Raid raid) {
        super(raid);
    }

    @Override
    public void start() {
        super.start();
        runTaskLater(raid::end, 5, TimeUnit.SECONDS);
        for (PlayerEntity player : raid.getOnlineParticipants()) {
            player.addStatusEffect(new StatusEffectInstance(AttackOnVillagerClient.VILLAIN_OF_THE_VILLAGE_EFFECT, 24000, 1)); // 24000 ticks = 1 minecraft day
        }
    }

    @Override
    public void initDefenseMethods() {
        defenseMethods.add(100, DefenseMethod.NOTHING);
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
        raid.getBossBar().setName(Text.of("Victory!"));
        raid.getBossBar().setPercent(1f);
    }
}
