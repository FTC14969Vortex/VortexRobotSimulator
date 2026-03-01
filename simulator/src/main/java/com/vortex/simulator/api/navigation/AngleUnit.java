package com.vortex.simulator.api.navigation;

public enum AngleUnit {
    DEGREES, RADIANS;

    public double toRadians(double angle) {
        return this == DEGREES ? Math.toRadians(angle) : angle;
    }

    public double toDegrees(double angle) {
        return this == RADIANS ? Math.toDegrees(angle) : angle;
    }

    public double fromUnit(AngleUnit unit, double angle) {
        if (unit == this) return angle;
        return this == RADIANS ? Math.toRadians(angle) : Math.toDegrees(angle);
    }
}
