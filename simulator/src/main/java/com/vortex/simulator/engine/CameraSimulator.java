package com.vortex.simulator.engine;

import com.vortex.simulator.api.limelight.LLResult;
import com.vortex.simulator.api.limelight.LLResultTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simulates a Limelight 3A camera for AprilTag detection.
 *
 * FOV: ±30° horizontal from robot heading.
 * Range: 120" max.
 * Noise: ±0.5° X, ±0.3° Y, ±2% area.
 * 20% chance of invalid result even when tags are visible (simulates real-world noise).
 */
public class CameraSimulator {

    private static final double FOV_DEG        = 30.0;
    private static final double MAX_RANGE_IN   = 120.0;
    private static final double TAG_HEIGHT_IN  =  6.0; // tag center height off floor
    private static final double CAM_HEIGHT_IN  =  8.0; // camera mount height
    private static final double NOISE_X_DEG    =  0.5;
    private static final double NOISE_Y_DEG    =  0.3;
    private static final double NOISE_AREA_PCT =  0.02;
    private static final double INVALID_PROB   =  0.20;

    private final RobotState state;
    private final Random rng = new Random();

    /** Last visible tag ID (for CameraPanel rendering). -1 if none. */
    private volatile int lastVisibleTagId = -1;
    /** Last target X degrees (normalized ±30°). */
    private volatile double lastTxDeg = 0;

    public CameraSimulator(RobotState state) {
        this.state = state;
    }

    public LLResult getLatestResult() {
        double robotX   = state.getX();
        double robotY   = state.getY();
        double heading  = state.getHeadingRad();

        List<AprilTagLayout.Tag> visible = new ArrayList<>();
        for (AprilTagLayout.Tag tag : AprilTagLayout.TAGS) {
            double dx = tag.x() - robotX;
            double dy = tag.y() - robotY;
            double dist = Math.hypot(dx, dy);
            if (dist > MAX_RANGE_IN) continue;

            // Angle from robot heading to tag
            double angleToTag = Math.atan2(dy, dx);
            double relAngle   = normalizeAngle(angleToTag - heading);
            if (Math.abs(Math.toDegrees(relAngle)) <= FOV_DEG) {
                visible.add(tag);
            }
        }

        if (visible.isEmpty() || rng.nextDouble() < INVALID_PROB) {
            lastVisibleTagId = -1;
            lastTxDeg = 0;
            return new LLResult(false, 0, List.of());
        }

        // Pick one tag at random from visible set
        AprilTagLayout.Tag tag = visible.get(rng.nextInt(visible.size()));
        double dx = tag.x() - robotX;
        double dy = tag.y() - robotY;
        double dist = Math.hypot(dx, dy);

        double angleToTag    = Math.atan2(dy, dx);
        double relAngle      = normalizeAngle(angleToTag - heading);
        double txDeg         = Math.toDegrees(relAngle) + gaussNoise(NOISE_X_DEG);
        double tyDeg         = Math.toDegrees(Math.atan2(TAG_HEIGHT_IN - CAM_HEIGHT_IN, dist))
                               + gaussNoise(NOISE_Y_DEG);
        double area          = clamp(200.0 / (dist * dist), 0, 100)
                               * (1 + gaussNoise(NOISE_AREA_PCT));
        double txNC          = clamp(txDeg / FOV_DEG, -1, 1);

        lastVisibleTagId = tag.id();
        lastTxDeg = txDeg;

        LLResultTypes.FiducialResult fid = new LLResultTypes.FiducialResult(tag.id(), txDeg, tyDeg, area);
        return new LLResult(true, txNC, List.of(fid));
    }

    public int getLastVisibleTagId() { return lastVisibleTagId; }
    public double getLastTxDeg()     { return lastTxDeg; }

    private double gaussNoise(double stddev) {
        return rng.nextGaussian() * stddev;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private static double normalizeAngle(double rad) {
        while (rad >  Math.PI) rad -= 2 * Math.PI;
        while (rad < -Math.PI) rad += 2 * Math.PI;
        return rad;
    }
}
