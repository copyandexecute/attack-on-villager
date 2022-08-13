package de.hglabor.attackonvillager.raid.wave;

import de.hglabor.attackonvillager.raid.AbstractWave;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.WaveType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.Text;

import java.util.concurrent.TimeUnit;

public class WinWave extends AbstractWave {
    protected WinWave(Raid raid) {
        super(raid);
    }

    @Override
    public void start() {
        runTaskLater(() -> {
            System.out.println("END");
            raid.end();
        }, 5, TimeUnit.SECONDS);
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
