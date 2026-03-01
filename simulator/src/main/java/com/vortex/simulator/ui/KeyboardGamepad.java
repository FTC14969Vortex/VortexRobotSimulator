package com.vortex.simulator.ui;

import com.vortex.simulator.api.Gamepad;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Maps keyboard input to Gamepad volatile fields.
 *
 * Drive:
 *   ↑ / ↓          → left_stick_y  (-1 = forward / +1 = backward)
 *   ← / →          → left_stick_x  (-1 = strafe left / +1 = strafe right)
 *   W / S           → left_stick_y  (alternate binding)
 *   A / D           → left_stick_x  (alternate binding)
 *
 * Rotation:
 *   r (lowercase)   → right_stick_x = +1  (CCW)
 *   R (Shift+R)     → right_stick_x = -1  (CW)
 *
 * Buttons:
 *   b (lowercase)   → left_bumper
 *   B (Shift+B)     → right_bumper
 *   U / O           → left_trigger / right_trigger (1.0)
 *   I               → dpad_up
 *   Space           → start
 */
public class KeyboardGamepad extends KeyAdapter {

    private final Gamepad gamepad;

    private boolean wDown, sDown, aDown, dDown;
    private boolean upDown, downDown, leftDown, rightDown;
    private boolean rCCWDown, rCWDown;

    public KeyboardGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            // Drive — arrows
            case KeyEvent.VK_UP     -> { upDown    = true;  updateLeftY(); }
            case KeyEvent.VK_DOWN   -> { downDown  = true;  updateLeftY(); }
            case KeyEvent.VK_LEFT   -> { leftDown  = true;  updateLeftX(); }
            case KeyEvent.VK_RIGHT  -> { rightDown = true;  updateLeftX(); }
            // Drive — WASD
            case KeyEvent.VK_W      -> { wDown = true;  updateLeftY(); }
            case KeyEvent.VK_S      -> { sDown = true;  updateLeftY(); }
            case KeyEvent.VK_A      -> { aDown = true;  updateLeftX(); }
            case KeyEvent.VK_D      -> { dDown = true;  updateLeftX(); }
            // Rotation
            case KeyEvent.VK_R -> {
                if (e.isShiftDown()) { rCWDown  = true; }
                else                 { rCCWDown = true; }
                updateRightX();
            }
            // Bumpers
            case KeyEvent.VK_B -> {
                if (e.isShiftDown()) gamepad.right_bumper = true;
                else                 gamepad.left_bumper  = true;
            }
            // Triggers
            case KeyEvent.VK_U -> gamepad.left_trigger  = 1.0f;
            case KeyEvent.VK_O -> gamepad.right_trigger = 1.0f;
            // D-pad / start
            case KeyEvent.VK_I     -> gamepad.dpad_up = true;
            case KeyEvent.VK_SPACE -> gamepad.start   = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP     -> { upDown    = false; updateLeftY(); }
            case KeyEvent.VK_DOWN   -> { downDown  = false; updateLeftY(); }
            case KeyEvent.VK_LEFT   -> { leftDown  = false; updateLeftX(); }
            case KeyEvent.VK_RIGHT  -> { rightDown = false; updateLeftX(); }
            case KeyEvent.VK_W      -> { wDown = false; updateLeftY(); }
            case KeyEvent.VK_S      -> { sDown = false; updateLeftY(); }
            case KeyEvent.VK_A      -> { aDown = false; updateLeftX(); }
            case KeyEvent.VK_D      -> { dDown = false; updateLeftX(); }
            case KeyEvent.VK_R      -> { rCCWDown = false; rCWDown = false; updateRightX(); }
            case KeyEvent.VK_B      -> { gamepad.left_bumper = false; gamepad.right_bumper = false; }
            case KeyEvent.VK_U      -> gamepad.left_trigger  = 0.0f;
            case KeyEvent.VK_O      -> gamepad.right_trigger = 0.0f;
            case KeyEvent.VK_I      -> gamepad.dpad_up = false;
            case KeyEvent.VK_SPACE  -> gamepad.start   = false;
        }
    }

    private void updateLeftY() {
        boolean fwd = upDown   || wDown;
        boolean bwd = downDown || sDown;
        gamepad.left_stick_y = fwd ? 1f : bwd ? -1f : 0f;
    }

    private void updateLeftX() {
        boolean lft = leftDown  || aDown;
        boolean rgt = rightDown || dDown;
        gamepad.left_stick_x = lft ? 1f : rgt ? -1f : 0f;
    }

    private void updateRightX() {
        gamepad.right_stick_x = rCCWDown ? 1f : rCWDown ? -1f : 0f;
    }
}
