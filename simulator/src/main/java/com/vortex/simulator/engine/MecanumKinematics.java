package com.vortex.simulator.engine;

import java.util.Map;

/**
 * 50 Hz Euler-integration physics for a mecanum-drive robot.
 *
 * Motor name convention (must match Constants.java):
 *   "frontLeft", "frontRight", "backLeft", "backRight"
 *
 * Kinematics derived from the real Chassis.java inverse-kinematics in
 * SummerNavigation2026:
 *   axial   = -(fl + fr + bl + br) / 4   (forward is negative in SDK convention)
 *   lateral = (fl - fr - bl + br) / 4
 *   omega   = (fl - fr + bl - br) / 4
 */
public class MecanumKinematics implements Runnable {

    private static final double HZ              = 50.0;
    private static final double DT              = 1.0 / HZ;
    private static final double MAX_SPEED_IN_S  = 60.0;   // inches/sec at full power
    private static final double MAX_TURN_RAD_S  = Math.PI; // rad/sec at full omega
    private static final double TICKS_PER_INCH  = 19.89;  // goBILDA 4-bar

    private final RobotState state;
    private volatile boolean running = false;

    public MecanumKinematics(RobotState state) {
        this.state = state;
    }

    public void start() {
        running = true;
        Thread t = new Thread(this, "physics-loop");
        t.setDaemon(true);
        t.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long nextTick = System.nanoTime();
        while (running) {
            tick();
            nextTick += (long)(DT * 1_000_000_000L);
            long now = System.nanoTime();
            if (nextTick > now) {
                try {
                    Thread.sleep((nextTick - now) / 1_000_000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void tick() {
        Map<String, Double> powers = state.getAllMotorPowers();
        double fl = powers.getOrDefault("frontLeft",  0.0);
        double fr = powers.getOrDefault("frontRight", 0.0);
        double bl = powers.getOrDefault("backLeft",   0.0);
        double br = powers.getOrDefault("backRight",  0.0);

        // Mecanum inverse kinematics (SDK sign convention)
        double axial   = -(fl + fr + bl + br) / 4.0;
        double lateral = ( fl - fr - bl + br) / 4.0;
        double omega   = ( fl - fr + bl - br) / 4.0;

        double heading = state.getHeadingRad();

        // Robot-frame → field-frame
        double vx = (axial * Math.cos(heading) - lateral * Math.sin(heading)) * MAX_SPEED_IN_S;
        double vy = (axial * Math.sin(heading) + lateral * Math.cos(heading)) * MAX_SPEED_IN_S;
        double dTheta = omega * MAX_TURN_RAD_S * DT;

        double newX   = state.getX() + vx * DT;
        double newY   = state.getY() + vy * DT;
        double newH   = heading + dTheta;

        // Clamp to field boundaries
        double half = RobotState.FIELD_SIZE_IN / 2.0;
        newX = Math.max(-half, Math.min(half, newX));
        newY = Math.max(-half, Math.min(half, newY));

        // Normalize heading to [0, 2π) to prevent unbounded accumulation
        newH = ((newH % (2 * Math.PI)) + 2 * Math.PI) % (2 * Math.PI);

        state.setPose(newX, newY, newH);
        state.setVelocity(vx, vy);

        // Encoder ticks proportional to axial/lateral displacement
        int axialTicks   = (int)(axial   * MAX_SPEED_IN_S * TICKS_PER_INCH * DT);
        int lateralTicks = (int)(lateral * MAX_SPEED_IN_S * TICKS_PER_INCH * DT);

        state.addEncoderTicks("frontLeft",  axialTicks + lateralTicks);
        state.addEncoderTicks("frontRight", axialTicks - lateralTicks);
        state.addEncoderTicks("backLeft",   axialTicks - lateralTicks);
        state.addEncoderTicks("backRight",  axialTicks + lateralTicks);
    }
}
