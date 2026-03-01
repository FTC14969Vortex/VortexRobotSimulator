package org.firstinspires.ftc.teamcode.Helper;

/**
 * Hardware name constants — must match the keys registered in
 * SimulatorRunner.buildHardwareMap().
 */
public final class Constants {
    private Constants() {}

    // Drive motors
    public static final String FRONT_LEFT  = "frontLeft";
    public static final String FRONT_RIGHT = "frontRight";
    public static final String BACK_LEFT   = "backLeft";
    public static final String BACK_RIGHT  = "backRight";

    // Odometry computer
    public static final String PINPOINT    = "pinpoint";

    // Vision
    public static final String LIMELIGHT   = "limelight";

    // Robot physical constants
    public static final double TRACK_WIDTH_IN  = 13.5; // distance between left/right wheels
    public static final double WHEEL_DIAM_IN   =  4.0;
    public static final double TICKS_PER_REV   = 537.7;
    public static final double TICKS_PER_INCH  = TICKS_PER_REV / (Math.PI * WHEEL_DIAM_IN);

    // Autonomous tuning
    public static final double DRIVE_SPEED   = 1;
    public static final double TURN_SPEED    = 1;
    public static final double HEADING_KP    = 0.03;

    // Limelight pipeline indices
    public static final int LL_APRIL_TAG_PIPELINE = 0;
}
