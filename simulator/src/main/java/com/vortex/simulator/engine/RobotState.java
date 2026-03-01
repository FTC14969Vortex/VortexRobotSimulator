package com.vortex.simulator.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Central shared state for the simulated robot.
 * All fields are protected by a ReadWriteLock so the physics thread can write
 * while the UI/OpMode threads read concurrently.
 */
public class RobotState {

    // Field dimensions in inches (origin at center)
    public static final double FIELD_SIZE_IN = 144.0;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // Pose (inches, radians)
    private double x = 0.0;
    private double y = 0.0;
    private double headingRad = 0.0;

    // Motor powers [-1, 1] keyed by hardware name
    private final Map<String, Double> motorPowers   = new HashMap<>();
    private final Map<String, Integer> encoderTicks = new HashMap<>();

    // Velocities (inches/sec) — updated by physics
    private double velX = 0.0;
    private double velY = 0.0;

    // Path trail — list of [x, y] waypoints recorded as the robot moves
    private final List<double[]> path = new ArrayList<>();
    private static final double PATH_MIN_DIST_IN = 1.0; // inches between recorded points

    // ---------------------------------------------------------------
    // Pose accessors
    // ---------------------------------------------------------------

    public double getX() {
        lock.readLock().lock();
        try { return x; } finally { lock.readLock().unlock(); }
    }

    public double getY() {
        lock.readLock().lock();
        try { return y; } finally { lock.readLock().unlock(); }
    }

    public double getHeadingRad() {
        lock.readLock().lock();
        try { return headingRad; } finally { lock.readLock().unlock(); }
    }

    public void setPose(double x, double y, double headingRad) {
        lock.writeLock().lock();
        try {
            this.x = x;
            this.y = y;
            this.headingRad = headingRad;
            // Record path waypoint if the robot has moved far enough from the last one
            if (path.isEmpty()) {
                path.add(new double[]{x, y});
            } else {
                double[] last = path.get(path.size() - 1);
                double dx = x - last[0];
                double dy = y - last[1];
                if (dx * dx + dy * dy >= PATH_MIN_DIST_IN * PATH_MIN_DIST_IN) {
                    path.add(new double[]{x, y});
                }
            }
        } finally { lock.writeLock().unlock(); }
    }

    /** Returns a snapshot of the recorded path waypoints for rendering. */
    public List<double[]> getPathSnapshot() {
        lock.readLock().lock();
        try { return new ArrayList<>(path); } finally { lock.readLock().unlock(); }
    }

    public void resetPose() {
        lock.writeLock().lock();
        try {
            x = 0; y = 0; headingRad = 0;
            path.clear();
        } finally { lock.writeLock().unlock(); }
    }

    // ---------------------------------------------------------------
    // Velocity accessors (written by physics, read by odometry)
    // ---------------------------------------------------------------

    public void setVelocity(double vx, double vy) {
        lock.writeLock().lock();
        try { this.velX = vx; this.velY = vy; } finally { lock.writeLock().unlock(); }
    }

    public double getVelX() {
        lock.readLock().lock();
        try { return velX; } finally { lock.readLock().unlock(); }
    }

    public double getVelY() {
        lock.readLock().lock();
        try { return velY; } finally { lock.readLock().unlock(); }
    }

    // ---------------------------------------------------------------
    // Motor powers
    // ---------------------------------------------------------------

    public void setMotorPower(String name, double power) {
        lock.writeLock().lock();
        try { motorPowers.put(name, Math.max(-1.0, Math.min(1.0, power))); }
        finally { lock.writeLock().unlock(); }
    }

    public double getMotorPower(String name) {
        lock.readLock().lock();
        try { return motorPowers.getOrDefault(name, 0.0); } finally { lock.readLock().unlock(); }
    }

    public Map<String, Double> getAllMotorPowers() {
        lock.readLock().lock();
        try { return new HashMap<>(motorPowers); } finally { lock.readLock().unlock(); }
    }

    // ---------------------------------------------------------------
    // Encoder ticks
    // ---------------------------------------------------------------

    public void addEncoderTicks(String name, int delta) {
        lock.writeLock().lock();
        try { encoderTicks.merge(name, delta, Integer::sum); }
        finally { lock.writeLock().unlock(); }
    }

    public void resetEncoder(String name) {
        lock.writeLock().lock();
        try { encoderTicks.put(name, 0); } finally { lock.writeLock().unlock(); }
    }

    public int getEncoderTicks(String name) {
        lock.readLock().lock();
        try { return encoderTicks.getOrDefault(name, 0); } finally { lock.readLock().unlock(); }
    }

    public void stopAllMotors() {
        lock.writeLock().lock();
        try { motorPowers.replaceAll((k, v) -> 0.0); } finally { lock.writeLock().unlock(); }
    }
}
