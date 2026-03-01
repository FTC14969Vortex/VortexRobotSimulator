package com.vortex.simulator.runner;

import com.vortex.simulator.api.LinearOpMode;
import com.vortex.simulator.ui.SimulatorWindow;

/**
 * Wraps a student LinearOpMode in a daemon thread and manages its lifecycle.
 */
public class OpModeThread {

    private final LinearOpMode opMode;
    private final SimulatorWindow window;
    private Thread thread;

    public OpModeThread(LinearOpMode opMode, SimulatorWindow window) {
        this.opMode = opMode;
        this.window = window;
    }

    /** Begin the thread — runOpMode() starts and blocks at waitForStart(). */
    public void startThread() {
        opMode.internalInit();
        thread = new Thread(() -> {
            try {
                opMode.runOpMode();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("[OpMode] Exception in runOpMode(): " + e.getMessage());
                e.printStackTrace();
            } finally {
                window.notifyOpModeStopped();
            }
        }, "opmode-thread");
        thread.setDaemon(true);
        thread.start();
    }

    /** Release the waitForStart() latch so runOpMode() continues. */
    public void pressStart() {
        opMode.internalStart();
    }

    /** Signal stop and interrupt the thread if still running. */
    public void stopOpMode() {
        opMode.internalStop();
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    public boolean isAlive() {
        return thread != null && thread.isAlive();
    }
}
