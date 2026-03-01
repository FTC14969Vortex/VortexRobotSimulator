package org.firstinspires.ftc.teamcode.Helper;

import com.vortex.simulator.api.DcMotor;
import com.vortex.simulator.api.DcMotorSimple;
import com.vortex.simulator.api.HardwareMap;

/**
 * Mecanum chassis abstraction — adapted from VortexDecode/SummerNavigation2026.
 * Import paths updated for the simulator; all logic unchanged.
 */
public class Chassis {

    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;

    public Chassis(HardwareMap hwMap) {
        frontLeft  = hwMap.get(DcMotor.class, Constants.FRONT_LEFT);
        frontRight = hwMap.get(DcMotor.class, Constants.FRONT_RIGHT);
        backLeft   = hwMap.get(DcMotor.class, Constants.BACK_LEFT);
        backRight  = hwMap.get(DcMotor.class, Constants.BACK_RIGHT);

        // Standard FTC motor direction setup for mecanum
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Field-centric mecanum drive.
     *
     * @param axial    forward/back [-1, 1]  (negative = forward in SDK convention)
     * @param lateral  left/right  [-1, 1]
     * @param yaw      rotation    [-1, 1]
     * @param heading  current robot heading in radians (from odometry)
     */
    public void driveFieldCentric(double axial, double lateral, double yaw, double heading) {
        // Rotate input by -heading to convert to robot-relative frame
        double rotX =  lateral * Math.cos(-heading) - axial * Math.sin(-heading);
        double rotY =  lateral * Math.sin(-heading) + axial * Math.cos(-heading);

        // Normalize so max power is 1.0
        double max = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(yaw), 1.0);

        double fl = (rotY + rotX + yaw) / max;
        double fr = (rotY - rotX - yaw) / max;
        double bl = (rotY - rotX + yaw) / max;
        double br = (rotY + rotX - yaw) / max;

        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);
    }

    /**
     * Robot-centric mecanum drive (no heading correction).
     */
    public void driveRobotCentric(double axial, double lateral, double yaw) {
        driveFieldCentric(axial, lateral, yaw, 0);
    }

    /**
     * Drive with individual motor powers directly.
     */
    public void setMotorPowers(double fl, double fr, double bl, double br) {
        frontLeft.setPower(fl);
        frontRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);
    }

    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
}
