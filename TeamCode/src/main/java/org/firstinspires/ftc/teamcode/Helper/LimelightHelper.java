package org.firstinspires.ftc.teamcode.Helper;

import com.vortex.simulator.api.HardwareMap;
import com.vortex.simulator.api.limelight.LLResult;
import com.vortex.simulator.api.limelight.LLResultTypes;
import com.vortex.simulator.api.limelight.Limelight3A;

import java.util.List;

/**
 * Thin wrapper around Limelight3A — adapted from VortexDecode/SummerNavigation2026.
 */
public class LimelightHelper {

    private final Limelight3A limelight;

    public LimelightHelper(HardwareMap hwMap) {
        limelight = hwMap.get(Limelight3A.class, Constants.LIMELIGHT);
        limelight.pipelineSwitch(Constants.LL_APRIL_TAG_PIPELINE);
        limelight.start();
    }

    /** @return most recent result, or null if no valid detection. */
    public LLResult getResult() {
        LLResult r = limelight.getLatestResult();
        return r.isValid() ? r : null;
    }

    /** @return first FiducialResult if valid, else null. */
    public LLResultTypes.FiducialResult getBestFiducial() {
        LLResult r = getResult();
        if (r == null) return null;
        List<LLResultTypes.FiducialResult> fids = r.getFiducialResults();
        return fids.isEmpty() ? null : fids.get(0);
    }

    /** Normalized horizontal offset [-1, 1]. 0 = centered. */
    public double getTxNC() {
        LLResult r = getResult();
        return r != null ? r.getTxNC() : 0.0;
    }

    public void stop() {
        limelight.stop();
    }
}
