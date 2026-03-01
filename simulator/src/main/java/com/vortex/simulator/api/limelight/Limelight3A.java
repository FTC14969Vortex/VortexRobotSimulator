package com.vortex.simulator.api.limelight;

import com.vortex.simulator.engine.CameraSimulator;

import java.util.List;

public class Limelight3A {
    private final CameraSimulator camera;
    private boolean running = false;
    private int pipeline = 0;

    public Limelight3A(CameraSimulator camera) {
        this.camera = camera;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void pipelineSwitch(int pipeline) {
        this.pipeline = pipeline;
    }

    public int getPipeline() {
        return pipeline;
    }

    public LLResult getLatestResult() {
        if (!running) return new LLResult(false, 0, List.of());
        return camera.getLatestResult();
    }
}
