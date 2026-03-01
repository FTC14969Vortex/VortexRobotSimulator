package com.vortex.simulator.engine;

import com.vortex.simulator.api.navigation.AngleUnit;
import com.vortex.simulator.api.navigation.DistanceUnit;
import com.vortex.simulator.api.navigation.Pose2D;

/**
 * Provides odometry readings backed by RobotState.
 * Mirrors the public API of the real GoBildaPinpointDriver odometry methods.
 */
public class OdometrySimulator {

    private static final double TICKS_PER_INCH = 19.89;

    private final RobotState state;

    // Accumulates odometry ticks independently from absolute pose (reset-able)
    private double originX = 0;
    private double originY = 0;
    private double originH = 0;

    public OdometrySimulator(RobotState state) {
        this.state = state;
    }

    /** Reset odometry to current pose (mirrors resetPosAndIMU on real driver). */
    public synchronized void resetPosAndIMU() {
        originX = state.getX();
        originY = state.getY();
        originH = state.getHeadingRad();
    }

    public Pose2D getPosition() {
        double relX = state.getX() - originX;
        double relY = state.getY() - originY;
        double relH = state.getHeadingRad() - originH;
        return new Pose2D(DistanceUnit.INCH, relX, relY, AngleUnit.RADIANS, relH);
    }

    public double getHeading(AngleUnit unit) {
        double h = state.getHeadingRad() - originH;
        return unit == AngleUnit.DEGREES ? Math.toDegrees(h) : h;
    }

    /** Encoder X (forward/back in robot frame) in inches × TICKS_PER_INCH. */
    public double getEncoderX() {
        double dx = state.getX() - originX;
        double dy = state.getY() - originY;
        double h  = originH;
        // Project onto robot-forward axis
        double axial = dx * Math.cos(h) + dy * Math.sin(h);
        return axial * TICKS_PER_INCH;
    }

    /** Encoder Y (strafe) in inches × TICKS_PER_INCH. */
    public double getEncoderY() {
        double dx = state.getX() - originX;
        double dy = state.getY() - originY;
        double h  = originH;
        // Project onto robot-lateral axis
        double lateral = -dx * Math.sin(h) + dy * Math.cos(h);
        return lateral * TICKS_PER_INCH;
    }

    public double getVelX() { return state.getVelX(); }
    public double getVelY() { return state.getVelY(); }
}
