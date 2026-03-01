package com.vortex.simulator.runner;

import com.vortex.simulator.api.*;
import com.vortex.simulator.api.limelight.Limelight3A;
import com.vortex.simulator.engine.*;
import com.vortex.simulator.ui.*;

import javax.swing.*;
import java.util.List;

/**
 * Application entry point.
 *
 * Wires together the engine, API layer, and UI, then shows the simulator window.
 * Students run this via: ./gradlew :TeamCode:run
 */
public class SimulatorRunner {

    public static void main(String[] args) throws Exception {
        // ── Engine ───────────────────────────────────────────────
        RobotState       state     = new RobotState();
        CameraSimulator  camera    = new CameraSimulator(state);
        OdometrySimulator odometry = new OdometrySimulator(state);
        MecanumKinematics physics  = new MecanumKinematics(state);

        // ── Gamepads ─────────────────────────────────────────────
        Gamepad gamepad1 = new Gamepad();
        Gamepad gamepad2 = new Gamepad();

        // ── Scan for OpModes ─────────────────────────────────────
        System.out.println("Scanning classpath for @TeleOp / @Autonomous ...");
        List<OpModeScanner.OpModeEntry> opModes = OpModeScanner.scan();
        System.out.println("Found " + opModes.size() + " OpMode(s).");

        if (opModes.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "No @TeleOp or @Autonomous classes found.\n" +
                "Add your OpMode class to TeamCode/src/main/java/.../OpModes/",
                "No OpModes", JOptionPane.WARNING_MESSAGE);
        }

        // ── Build hardware map factory ────────────────────────────
        // (re-built fresh for each INIT so encoders/state resets cleanly)

        // ── UI ───────────────────────────────────────────────────
        // Cross-platform L&F so custom button colors (setBackground/setForeground) are respected.
        // The macOS Aqua L&F overrides those with its own rendering.
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        final SimulatorWindow[] windowRef = new SimulatorWindow[1];

        SwingUtilities.invokeAndWait(() -> {
            SimulatorWindow window = new SimulatorWindow(state, camera, gamepad1, opModes,
                entry -> initOpMode(entry, state, odometry, camera, gamepad1, gamepad2, windowRef[0], physics));
            windowRef[0] = window;
            window.setVisible(true);
        });

        // Start physics loop after window is up
        physics.start();
    }

    private static void initOpMode(
            OpModeScanner.OpModeEntry entry,
            RobotState state,
            OdometrySimulator odometry,
            CameraSimulator camera,
            Gamepad gamepad1,
            Gamepad gamepad2,
            SimulatorWindow window,
            MecanumKinematics physics) {

        try {
            // Fresh hardware map for every INIT
            HardwareMap hwMap = buildHardwareMap(state, odometry, camera);

            LinearOpMode opMode = (LinearOpMode) entry.opModeClass().getDeclaredConstructor().newInstance();
            opMode.hardwareMap = hwMap;
            opMode.gamepad1    = gamepad1;
            opMode.gamepad2    = gamepad2;
            opMode.telemetry   = new TelemetryImpl(window.getTelemetryPanel());

            // Reset robot state for clean start
            state.resetPose();
            state.stopAllMotors();
            gamepad1.reset();
            gamepad2.reset();

            OpModeThread opThread = new OpModeThread(opMode, window);
            window.setCurrentThread(opThread);
            opThread.startThread();

            System.out.println("[Runner] Initialized OpMode: " + entry.displayName());
        } catch (Exception e) {
            System.err.println("[Runner] Failed to init OpMode: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(window,
                "Failed to initialize OpMode:\n" + e.getMessage(),
                "Init Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Builds and populates the HardwareMap with simulator devices.
     * Device names must match the strings in TeamCode's Constants.java.
     */
    private static HardwareMap buildHardwareMap(RobotState state, OdometrySimulator odometry,
                                                 CameraSimulator camera) {
        HardwareMap hwMap = new HardwareMap();

        // Drive motors
        hwMap.put("frontLeft",  new DcMotor("frontLeft",  state));
        hwMap.put("frontRight", new DcMotor("frontRight", state));
        hwMap.put("backLeft",   new DcMotor("backLeft",   state));
        hwMap.put("backRight",  new DcMotor("backRight",  state));

        // Odometry
        hwMap.put("pinpoint", new GoBildaPinpointDriver(odometry));

        // Camera
        hwMap.put("limelight", new Limelight3A(camera));

        return hwMap;
    }
}
