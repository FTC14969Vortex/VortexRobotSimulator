package com.vortex.simulator.api;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for all student OpModes — mirrors the real FTC LinearOpMode API.
 * The simulator injects hardwareMap, gamepad1/2, telemetry before calling
 * runOpMode() on a background thread.
 */
public abstract class LinearOpMode {

    // Injected by SimulatorRunner before thread start
    public HardwareMap hardwareMap;
    public Gamepad     gamepad1;
    public Gamepad     gamepad2;
    public Telemetry   telemetry;

    private final AtomicBoolean  opModeActive = new AtomicBoolean(false);
    private final AtomicBoolean  startPressed  = new AtomicBoolean(false);
    private volatile CountDownLatch startLatch  = new CountDownLatch(1);

    // ---------------------------------------------------------------
    // Student override
    // ---------------------------------------------------------------
    public abstract void runOpMode() throws InterruptedException;

    // ---------------------------------------------------------------
    // Lifecycle called by simulator infrastructure
    // ---------------------------------------------------------------

    /** Called by OpModeThread when the INIT button is pressed. */
    public final void internalInit() {
        startLatch   = new CountDownLatch(1);
        startPressed.set(false);
        opModeActive.set(false);
    }

    /** Called by SimulatorRunner when the START button is pressed. */
    public final void internalStart() {
        opModeActive.set(true);
        startPressed.set(true);
        startLatch.countDown();
    }

    /** Called by SimulatorRunner when the STOP button is pressed. */
    public final void internalStop() {
        opModeActive.set(false);
        startLatch.countDown(); // unblock waitForStart so thread can exit
    }

    // ---------------------------------------------------------------
    // API methods used by student code
    // ---------------------------------------------------------------

    /** Blocks until START is pressed. */
    public final void waitForStart() throws InterruptedException {
        startLatch.await();
    }

    public final boolean opModeIsActive() {
        return opModeActive.get() && !Thread.currentThread().isInterrupted();
    }

    public final boolean isStarted() {
        return startPressed.get();
    }

    public final boolean isStopRequested() {
        return !opModeActive.get();
    }

    public final void sleep(long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    /** Non-throwing convenience sleep — swallows InterruptedException and re-sets interrupt flag. */
    public final void idle() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
