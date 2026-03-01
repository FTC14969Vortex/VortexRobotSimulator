package org.firstinspires.ftc.teamcode.OpModes;

import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.annotations.Autonomous;

import org.firstinspires.ftc.teamcode.Helper.Constants;
import org.firstinspires.ftc.teamcode.Helper.Navigation;
import org.firstinspires.ftc.teamcode.Helper.Robot;

/**
 * V Autonomous: turn 135, move down,turn 90, move up
 */
@Autonomous(name = "AutoV", group = "Autonomous")
public class AutoV extends LinearOpMode {

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

        navigation.turnDegrees(115.0, Constants.TURN_SPEED);

        navigation.driveDistance(30.0, Constants.DRIVE_SPEED);

        navigation.turnDegrees(180.0, Constants.TURN_SPEED);

        navigation.driveDistance(30.0, Constants.DRIVE_SPEED);

        navigation.turnDegrees(-220.0, Constants.TURN_SPEED);

        navigation.driveDistance(30.0, Constants.DRIVE_SPEED);


        robot.chassis.stop();
        telemetry.addLine("AutoExample complete.");
        telemetry.update();
    }
}
