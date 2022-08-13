package de.hglabor.attackonvillager.raid;

public abstract class AbstractWave {
    protected final Raid raid;

    protected AbstractWave(Raid raid) {
        this.raid = raid;
    }

    protected final void startNextWave() {
        raid.setCurrentWave(nextWave());
        raid.getCurrentWave().start();
    }

    public abstract void tick();

    public abstract void start();

    public abstract AbstractWave nextWave();

    public abstract WaveType getWaveType();

    public abstract void updateBossBar();
}
