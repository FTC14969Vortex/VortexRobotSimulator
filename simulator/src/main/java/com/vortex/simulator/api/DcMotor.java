package com.vortex.simulator.api;

import com.vortex.simulator.engine.RobotState;

public class DcMotor extends DcMotorSimple {

    public enum ZeroPowerBehavior {
        BRAKE, FLOAT, UNKNOWN
    }

    public enum RunMode {
        RUN_WITHOUT_ENCODER,
        RUN_USING_ENCODER,
        RUN_TO_POSITION,
        STOP_AND_RESET_ENCODER
    }

    private final String name;
    private final RobotState state;
    private Direction direction = Direction.FORWARD;
    private ZeroPowerBehavior zeroPowerBehavior = ZeroPowerBehavior.FLOAT;
    private RunMode runMode = RunMode.RUN_WITHOUT_ENCODER;

    public DcMotor(String name, RobotState state) {
        this.name = name;
        this.state = state;
    }

    public void setPower(double power) {
        // Store the commanded power directly. The direction flag models physical motor
        // mounting on the real robot, but the simulator physics reads commanded values
        // and applies its own kinematics — applying the flip here would corrupt the
        // kinematics formula (swapping axial↔omega).
        state.setMotorPower(name, Math.max(-1.0, Math.min(1.0, power)));
    }

    public double getPower() {
        // Report back with direction flip so student code reads the logical value.
        double raw = state.getMotorPower(name);
        return direction == Direction.REVERSE ? -raw : raw;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setZeroPowerBehavior(ZeroPowerBehavior behavior) {
        this.zeroPowerBehavior = behavior;
    }

    public ZeroPowerBehavior getZeroPowerBehavior() {
        return zeroPowerBehavior;
    }

    public void setMode(RunMode mode) {
        this.runMode = mode;
        if (mode == RunMode.STOP_AND_RESET_ENCODER) {
            state.resetEncoder(name);
        }
    }

    public RunMode getMode() {
        return runMode;
    }

    public int getCurrentPosition() {
        return state.getEncoderTicks(name);
    }

    public String getName() {
        return name;
    }
}
