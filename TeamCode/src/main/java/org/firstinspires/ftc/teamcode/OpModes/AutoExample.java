package org.firstinspires.ftc.teamcode.OpModes;

import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.api.annotations.Autonomous;
import org.firstinspires.ftc.teamcode.Helper.Constants;
import org.firstinspires.ftc.teamcode.Helper.Navigation;
import org.firstinspires.ftc.teamcode.Helper.Robot;

/**
 * Example Autonomous: drive forward, then exercise CW and CCW turns at a range
 * of angles (±45°, ±90°, ±135°, ±180°) to verify turnDegrees across the full
 * 0°/360° wrap boundary.
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

        // Drive forward to give the path trail room to show
        navigation.driveDistance(24.0, Constants.DRIVE_SPEED);

        // --- CCW turns (positive angles) ---
        navigation.turnDegrees( 45.0, Constants.TURN_SPEED);   // +45°  CCW
        navigation.turnDegrees(-45.0, Constants.TURN_SPEED);   // back to start

        navigation.turnDegrees( 90.0, Constants.TURN_SPEED);   // +90°  CCW
        navigation.turnDegrees(-90.0, Constants.TURN_SPEED);   // back

        navigation.turnDegrees( 135.0, Constants.TURN_SPEED);  // +135° CCW
        navigation.turnDegrees(-135.0, Constants.TURN_SPEED);  // back

        navigation.turnDegrees( 180.0, Constants.TURN_SPEED);  // +180° CCW
        navigation.turnDegrees(-180.0, Constants.TURN_SPEED);  // back

        robot.chassis.stop();
        telemetry.addLine("AutoExample complete.");
        telemetry.update();
    }
}
