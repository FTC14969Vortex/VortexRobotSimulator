package com.vortex.simulator.api.limelight;

import java.util.List;

public class LLResult {
    private final boolean valid;
    private final double txNC;
    private final List<LLResultTypes.FiducialResult> fiducialResults;

    public LLResult(boolean valid, double txNC, List<LLResultTypes.FiducialResult> fiducialResults) {
        this.valid = valid;
        this.txNC = txNC;
        this.fiducialResults = fiducialResults;
    }

    public boolean isValid() { return valid; }

    /** Normalized crosshair X (-1 to 1). */
    public double getTxNC() { return txNC; }

    public List<LLResultTypes.FiducialResult> getFiducialResults() { return fiducialResults; }
}
