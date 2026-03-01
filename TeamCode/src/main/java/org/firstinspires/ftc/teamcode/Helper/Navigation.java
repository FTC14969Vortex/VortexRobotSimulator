package org.firstinspires.ftc.teamcode.Helper;

import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.limelight.LLResultTypes;

/**
 * Reusable autonomous navigation primitives.
 * Construct with the active OpMode and a Robot, then call from any LinearOpMode.
 */
public class Navigation {

    private final LinearOpMode opMode;
    private final Robot robot;

    public Navigation(LinearOpMode opMode, Robot robot) {
        this.opMode = opMode;
        this.robot  = robot;
    }

    /** Drive straight axially for approximately {@code inches} at the given power. */
    public void driveDistance(double inches, double speed) throws InterruptedException {
        double startX = robot.getX();
        double startY = robot.getY();
        double startH = robot.getHeadingRad();

        while (opMode.opModeIsActive()) {
            robot.update();
            double dx      = robot.getX() - startX;
            double dy      = robot.getY() - startY;
            double traveled = Math.hypot(dx, dy);

            if (traveled >= inches) break;

            double headingError = robot.getHeadingRad() - startH;
            double correction   = headingError * Constants.HEADING_KP;

            robot.chassis.driveRobotCentric(-speed, 0, correction);

            opMode.telemetry.addData("Drive", String.format("%.1f / %.1f in", traveled, inches));
            opMode.telemetry.update();
            opMode.sleep(5);
        }
        robot.chassis.stop();
        opMode.sleep(200);
    }

    /** Turn in place by {@code degrees} (positive = left/CCW). */
    public void turnDegrees(double degrees, double speed) throws InterruptedException {
        double startH  = robot.getHeadingDeg();
        double targetH = startH + degrees;
        double dir     = degrees > 0 ? 1 : -1;

        while (opMode.opModeIsActive()) {
            robot.update();
            double currentH  = robot.getHeadingDeg();
            double remaining = targetH - currentH;
            // Normalize to [-180, 180] so the error is correct across the 0/360° wrap
            remaining = ((remaining + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;

            if (Math.abs(remaining) < 2.0) break;

            robot.chassis.driveRobotCentric(0, 0, dir * speed);

            opMode.telemetry.addData("Turn", String.format("%.1f° remaining", remaining));
            opMode.telemetry.update();
            opMode.sleep(5);
        }
        robot.chassis.stop();
        opMode.sleep(200);
    }

    /** Steer toward the first visible AprilTag for up to {@code timeoutMs} ms. */
    public void alignToTag(long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (opMode.opModeIsActive() && System.currentTimeMillis() < deadline) {
            robot.update();
            LLResultTypes.FiducialResult fid = robot.limelight.getBestFiducial();
            if (fid == null) {
                robot.chassis.stop();
                opMode.telemetry.addLine("Searching for tag...");
                opMode.telemetry.update();
                opMode.sleep(50);
                continue;
            }

            double tx = fid.getTargetXDegrees();
            if (Math.abs(tx) < 1.5) {
                opMode.telemetry.addData("Aligned to tag", fid.getFiducialId());
                opMode.telemetry.update();
                break;
            }

            double yaw = tx / 30.0 * Constants.TURN_SPEED;
            robot.chassis.driveRobotCentric(0, 0, yaw);

            opMode.telemetry.addData("AlignTag", "ID=" + fid.getFiducialId()
                    + " tx=" + String.format("%.1f°", tx));
            opMode.telemetry.update();
            opMode.sleep(5);
        }
        robot.chassis.stop();
    }
}
