package com.vortex.simulator.api;

import com.vortex.simulator.api.navigation.AngleUnit;
import com.vortex.simulator.api.navigation.DistanceUnit;
import com.vortex.simulator.api.navigation.Pose2D;
import com.vortex.simulator.engine.OdometrySimulator;

/**
 * Simulator stand-in for the real goBILDA Pinpoint odometry driver.
 * Public API is identical to the real driver so TeamCode compiles unchanged
 * (except for the import path).
 */
public class GoBildaPinpointDriver {

    public enum EncoderDirection { FORWARD, REVERSED }
    public enum GoBildaOdometryPods { opticalOdometry, goBILDA_SWINGARM_POD, goBILDA_4_BAR_POD }
    public enum DeviceStatus { READY, CALIBRATING, NOT_READY, FAULT_NO_PODS_DETECTED, FAULT_X_POD_NOT_DETECTED, FAULT_Y_POD_NOT_DETECTED }

    private final OdometrySimulator odometry;
    private EncoderDirection xDirection = EncoderDirection.FORWARD;
    private EncoderDirection yDirection = EncoderDirection.FORWARD;

    public GoBildaPinpointDriver(OdometrySimulator odometry) {
        this.odometry = odometry;
    }

    public void setOffsets(double xOffset, double yOffset) { /* physical offsets ignored in sim */ }
    public void setEncoderResolution(GoBildaOdometryPods pods) { /* resolution fixed in sim */ }
    public void setEncoderResolution(double ticksPerMm) { /* ignored */ }

    public void setEncoderDirections(EncoderDirection xDir, EncoderDirection yDir) {
        this.xDirection = xDir;
        this.yDirection = yDir;
    }

    public void resetPosAndIMU() {
        odometry.resetPosAndIMU();
    }

    public void update() {
        // In the real driver this triggers a bus read; in sim the state is always fresh
    }

    public Pose2D getPosition() {
        return odometry.getPosition();
    }

    public double getHeading() {
        return odometry.getHeading(AngleUnit.RADIANS);
    }

    public double getHeading(AngleUnit unit) {
        return odometry.getHeading(unit);
    }

    public double getEncoderX() {
        double v = odometry.getEncoderX();
        return xDirection == EncoderDirection.REVERSED ? -v : v;
    }

    public double getEncoderY() {
        double v = odometry.getEncoderY();
        return yDirection == EncoderDirection.REVERSED ? -v : v;
    }

    public double getVelX() { return odometry.getVelX(); }
    public double getVelY() { return odometry.getVelY(); }

    public DeviceStatus getDeviceStatus() { return DeviceStatus.READY; }

    public int getLoopTime() { return 20; } // ~50 Hz
}
