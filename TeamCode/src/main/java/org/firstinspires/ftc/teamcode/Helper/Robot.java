package org.firstinspires.ftc.teamcode.Helper;

import com.vortex.simulator.api.GoBildaPinpointDriver;
import com.vortex.simulator.api.HardwareMap;
import com.vortex.simulator.api.navigation.AngleUnit;
import com.vortex.simulator.api.navigation.DistanceUnit;
import com.vortex.simulator.api.navigation.Pose2D;

/**
 * Top-level robot abstraction — adapted from VortexDecode/SummerNavigation2026.
 * Combines Chassis + Odometry + Vision into one convenient object.
 */
public class Robot {

    public final Chassis         chassis;
    public final LimelightHelper limelight;
    private final GoBildaPinpointDriver pinpoint;

    public Robot(HardwareMap hwMap) {
        chassis   = new Chassis(hwMap);
        limelight = new LimelightHelper(hwMap);
        pinpoint  = hwMap.get(GoBildaPinpointDriver.class, Constants.PINPOINT);
        pinpoint.resetPosAndIMU();
    }

    public void update() {
        pinpoint.update();
    }

    public Pose2D getPose() {
        return pinpoint.getPosition();
    }

    public double getX() {
        return getPose().getX(DistanceUnit.INCH);
    }

    public double getY() {
        return getPose().getY(DistanceUnit.INCH);
    }

    public double getHeadingRad() {
        return pinpoint.getHeading(AngleUnit.RADIANS);
    }

    public double getHeadingDeg() {
        return pinpoint.getHeading(AngleUnit.DEGREES);
    }

    public void resetOdometry() {
        pinpoint.resetPosAndIMU();
    }
}
