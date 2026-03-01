package org.firstinspires.ftc.teamcode.OpModes;

import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.annotations.TeleOp;
import com.vortex.simulator.api.limelight.LLResultTypes;
import org.firstinspires.ftc.teamcode.Helper.Robot;

/**
 * Robot-centric mecanum TeleOp with Limelight telemetry.
 *
 * Controls:
 *   ↑/↓ or W/S    → forward / back
 *   ←/→ or A/D    → strafe left / right
 *   r / R         → rotate CCW / CW
 *   b             → left bumper
 *   B (Shift+B)   → right bumper
 */
@TeleOp(name = "TeleOp", group = "TeleOp")
public class TeleOpMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Robot robot = new Robot(hardwareMap);

        telemetry.addLine("TeleOp initialized. Press START.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            robot.update();

            double axial   = -gamepad1.left_stick_y;   // forward / back
            double lateral =  gamepad1.left_stick_x;   // strafe
            double yaw     =  gamepad1.right_stick_x;  // rotate

            robot.chassis.driveRobotCentric(axial, lateral, yaw);

            // Telemetry
            LLResultTypes.FiducialResult fid = robot.limelight.getBestFiducial();
            telemetry.addData("X (in)",   String.format("%.2f", robot.getX()));
            telemetry.addData("Y (in)",   String.format("%.2f", robot.getY()));
            telemetry.addData("Heading",  String.format("%.1f°", normalizeDeg(robot.getHeadingDeg())));
            telemetry.addData("Tag",      fid != null ? "ID " + fid.getFiducialId() : "none");
            telemetry.addData("Tag TX",   fid != null ? String.format("%.1f°", fid.getTargetXDegrees()) : "--");
            telemetry.addData("axial",    String.format("%.2f", axial));
            telemetry.addData("lateral",  String.format("%.2f", lateral));
            telemetry.addData("yaw",      String.format("%.2f", yaw));
            telemetry.update();
        }

        robot.chassis.stop();
    }

    /** Wrap any degree value into [0, 360). */
    private static double normalizeDeg(double deg) {
        return ((deg % 360) + 360) % 360;
    }
}
