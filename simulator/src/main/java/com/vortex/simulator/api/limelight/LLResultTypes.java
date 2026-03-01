package com.vortex.simulator.api.limelight;

public class LLResultTypes {

    public static class FiducialResult {
        private final int fiducialId;
        private final double targetXDegrees;
        private final double targetYDegrees;
        private final double targetArea;

        public FiducialResult(int fiducialId, double targetXDegrees, double targetYDegrees, double targetArea) {
            this.fiducialId = fiducialId;
            this.targetXDegrees = targetXDegrees;
            this.targetYDegrees = targetYDegrees;
            this.targetArea = targetArea;
        }

        public int getFiducialId() { return fiducialId; }
        public double getTargetXDegrees() { return targetXDegrees; }
        public double getTargetYDegrees() { return targetYDegrees; }
        public double getTargetArea() { return targetArea; }
    }
}
