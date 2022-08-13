package de.hglabor.attackonvillager.raid;

public enum WaveType {
    KILL_VILLAGERS,
    DESTROY_HOUSES,
    ROB_VILLAGERS,
    WIN;

    private static final WaveType[] VALUES = values();

    public WaveType next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
}
