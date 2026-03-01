package com.vortex.simulator.api;

/**
 * Mirrors the real FTC SDK Gamepad. All fields are volatile so the physics
 * thread can safely read while the KeyboardGamepad writes from the EDT.
 */
public class Gamepad {
    public volatile float left_stick_x   = 0f;
    public volatile float left_stick_y   = 0f;
    public volatile float right_stick_x  = 0f;
    public volatile float right_stick_y  = 0f;

    public volatile float left_trigger   = 0f;
    public volatile float right_trigger  = 0f;

    public volatile boolean a            = false;
    public volatile boolean b            = false;
    public volatile boolean x            = false;
    public volatile boolean y            = false;

    public volatile boolean left_bumper  = false;
    public volatile boolean right_bumper = false;

    public volatile boolean dpad_up      = false;
    public volatile boolean dpad_down    = false;
    public volatile boolean dpad_left    = false;
    public volatile boolean dpad_right   = false;

    public volatile boolean start        = false;
    public volatile boolean back         = false;

    /** Reset all inputs to neutral (used on STOP). */
    public void reset() {
        left_stick_x  = 0f; left_stick_y  = 0f;
        right_stick_x = 0f; right_stick_y = 0f;
        left_trigger  = 0f; right_trigger = 0f;
        a = b = x = y = false;
        left_bumper = right_bumper = false;
        dpad_up = dpad_down = dpad_left = dpad_right = false;
        start = back = false;
    }
}
