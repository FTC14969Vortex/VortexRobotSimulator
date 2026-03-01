package org.firstinspires.ftc.teamcode.OpModes;

import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.annotations.Autonomous;
import org.firstinspires.ftc.teamcode.Helper.Constants;
import org.firstinspires.ftc.teamcode.Helper.Navigation;
import org.firstinspires.ftc.teamcode.Helper.Robot;

/**
 * Example Autonomous: drive forward 24", turn 90° left, then align to nearest tag.
 */
@Autonomous(name = "AutoExample", group = "Autonomous")
public class AutoExample extends LinearOpMode {

    private Robot robot;
    private Navigation navigation;

    @Override
    public void runOpMode() throws InterruptedException {
        robot      = new Robot(hardwareMap);
        navigation = new Navigation(this, robot);

        telemetry.addLine("AutoExample ready. Press START.");
        telemetry.update();

        waitForStart();
        if (!opModeIsActive()) return;

        // Step 1: Drive forward 24 inches
        navigation.driveDistance(24.0, Constants.DRIVE_SPEED);

        // Step 2: Turn left 90 degrees
        navigation.turnDegrees(90.0, Constants.TURN_SPEED);

        // Step 3: Align to nearest AprilTag (up to 3 seconds)
        navigation.alignToTag(3000);

        robot.chassis.stop();
        telemetry.addLine("AutoExample complete.");
        telemetry.update();
    }
}
