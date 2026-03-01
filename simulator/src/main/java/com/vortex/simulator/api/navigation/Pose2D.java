package com.vortex.simulator.api.navigation;

public class Pose2D {
    private final double x;
    private final double y;
    private final double heading;
    private final DistanceUnit distanceUnit;
    private final AngleUnit angleUnit;

    public Pose2D(DistanceUnit distanceUnit, double x, double y, AngleUnit angleUnit, double heading) {
        this.distanceUnit = distanceUnit;
        this.x = x;
        this.y = y;
        this.angleUnit = angleUnit;
        this.heading = heading;
    }

    public double getX(DistanceUnit unit) {
        return unit.fromUnit(distanceUnit, x);
    }

    public double getY(DistanceUnit unit) {
        return unit.fromUnit(distanceUnit, y);
    }

    public double getHeading(AngleUnit unit) {
        return unit.fromUnit(angleUnit, heading);
    }

    @Override
    public String toString() {
        return String.format("Pose2D{x=%.2f, y=%.2f, heading=%.2f}", x, y, heading);
    }
}
