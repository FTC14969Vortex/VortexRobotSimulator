package org.firstinspires.ftc.teamcode.OpModes;

import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.annotations.Autonomous;
import com.vortex.simulator.api.limelight.LLResultTypes;
import org.firstinspires.ftc.teamcode.Helper.Constants;
import org.firstinspires.ftc.teamcode.Helper.Robot;

/**
 * Example Autonomous: drive forward 24", turn 90° left, then align to nearest tag.
 */
@Autonomous(name = "AutoBlue", group = "Autonomous")
public class AutoBlue extends LinearOpMode {

    private Robot robot;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(hardwareMap);

        telemetry.addLine("AutoBlue ready. Press START.");
        telemetry.update();

        waitForStart();
        if (!opModeIsActive()) return;

        // Step 1: Drive forward 24 inches
        driveDistance(24.0, Constants.DRIVE_SPEED);

        // Step 2: Turn left 90 degrees
        turnDegrees(90.0, Constants.TURN_SPEED);

        // Step 3: Align to nearest AprilTag (up to 3 seconds)
        alignToTag(3000);

        robot.chassis.stop();
        telemetry.addLine("AutoBlue complete.");
        telemetry.update();
    }

    /** Drive straight axially for approximately {@code inches} at the given power. */
    private void driveDistance(double inches, double speed) throws InterruptedException {
        double startX = robot.getX();
        double startY = robot.getY();
        double startH = robot.getHeadingRad();

        while (opModeIsActive()) {
            robot.update();
            double dx = robot.getX() - startX;
            double dy = robot.getY() - startY;
            double traveled = Math.hypot(dx, dy);

            if (traveled >= inches) break;

            // Heading correction
            double headingError = robot.getHeadingRad() - startH;
            double correction   = headingError * Constants.HEADING_KP;

            robot.chassis.driveRobotCentric(-speed, 0, correction);

            telemetry.addData("Drive", String.format("%.1f / %.1f in", traveled, inches));
            telemetry.update();
            sleep(5);
        }
        robot.chassis.stop();
        sleep(200);
    }

    /** Turn in place by {@code degrees} (positive = left/CCW). */
    private void turnDegrees(double degrees, double speed) throws InterruptedException {
        double startH  = robot.getHeadingDeg();
        double targetH = startH + degrees;
        double dir     = degrees > 0 ? 1 : -1;

        while (opModeIsActive()) {
            robot.update();
            double currentH = robot.getHeadingDeg();
            double remaining = targetH - currentH;

            if (Math.abs(remaining) < 2.0) break;

            double power = dir * speed;
            robot.chassis.driveRobotCentric(0, 0, power);

            telemetry.addData("Turn", String.format("%.1f° remaining", remaining));
            telemetry.update();
            sleep(5);
        }
        robot.chassis.stop();
        sleep(200);
    }

    /** Steer toward the first visible AprilTag for up to {@code timeoutMs} ms. */
    private void alignToTag(long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (opModeIsActive() && System.currentTimeMillis() < deadline) {
            robot.update();
            LLResultTypes.FiducialResult fid = robot.limelight.getBestFiducial();
            if (fid == null) {
                robot.chassis.stop();
                telemetry.addLine("Searching for tag...");
                telemetry.update();
                sleep(50);
                continue;
            }

            double tx = fid.getTargetXDegrees();
            if (Math.abs(tx) < 1.5) {
                telemetry.addData("Aligned to tag", fid.getFiducialId());
                telemetry.update();
                break;
            }

            double yaw = tx / 30.0 * Constants.TURN_SPEED;
            robot.chassis.driveRobotCentric(0, 0, yaw);

            telemetry.addData("AlignTag", "ID=" + fid.getFiducialId()
                + " tx=" + String.format("%.1f°", tx));
            telemetry.update();
            sleep(5);
        }
        robot.chassis.stop();
    }
}
