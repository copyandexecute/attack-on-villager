package de.hglabor.attackonvillager.raid.wave;

import de.hglabor.attackonvillager.raid.AbstractWave;
import de.hglabor.attackonvillager.raid.Raid;
import de.hglabor.attackonvillager.raid.WaveType;

public class DestroyHousesWave extends AbstractWave {
    public DestroyHousesWave(Raid raid) {
        super(raid);
    }

    @Override
    public void start() {

    }

    @Override
    public AbstractWave nextWave() {
        return null;
    }

    @Override
    public WaveType getWaveType() {
        return WaveType.DESTROY_HOUSES;
    }
}
