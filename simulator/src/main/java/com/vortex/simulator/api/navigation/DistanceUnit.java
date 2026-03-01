package com.vortex.simulator.api.navigation;

public enum DistanceUnit {
    MM, CM, INCH, METER;

    private static final double MM_PER_INCH = 25.4;
    private static final double MM_PER_CM   = 10.0;
    private static final double MM_PER_M    = 1000.0;

    public double toMm(double value) {
        return switch (this) {
            case MM    -> value;
            case CM    -> value * MM_PER_CM;
            case INCH  -> value * MM_PER_INCH;
            case METER -> value * MM_PER_M;
        };
    }

    public double fromMm(double mm) {
        return switch (this) {
            case MM    -> mm;
            case CM    -> mm / MM_PER_CM;
            case INCH  -> mm / MM_PER_INCH;
            case METER -> mm / MM_PER_M;
        };
    }

    public double fromUnit(DistanceUnit unit, double value) {
        return fromMm(unit.toMm(value));
    }
}
