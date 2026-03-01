package org.firstinspires.ftc.teamcode.OpModes;

import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.annotations.Autonomous;
import org.firstinspires.ftc.teamcode.Helper.Constants;
import org.firstinspires.ftc.teamcode.Helper.Navigation;
import org.firstinspires.ftc.teamcode.Helper.Robot;

/**
 * Example Autonomous: drive forward 24", turn 90° left, then align to nearest tag.
 */
@Autonomous(name = "Auto Letter E", group = "Autonomous")
public class LetterE extends LinearOpMode {

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

        navigation.driveDistance(24, Constants.DRIVE_SPEED);
        navigation.turnDegrees(180, Constants.TURN_SPEED);
        navigation.driveDistance(24, Constants.DRIVE_SPEED);
        navigation.turnDegrees(90, Constants.TURN_SPEED);
        navigation.driveDistance(24, Constants.DRIVE_SPEED);
        navigation.turnDegrees(90, Constants.TURN_SPEED);
        navigation.driveDistance(24, Constants.DRIVE_SPEED);
        navigation.turnDegrees(180, Constants.TURN_SPEED);
        



        robot.chassis.stop();
        telemetry.addLine("AutoExample complete.");
        telemetry.update();
    }
}
