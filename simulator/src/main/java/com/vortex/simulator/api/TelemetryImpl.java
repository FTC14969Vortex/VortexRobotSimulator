package com.vortex.simulator.api;

import com.vortex.simulator.ui.TelemetryPanel;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TelemetryImpl implements Telemetry {

    private final TelemetryPanel panel;
    private final Map<String, String> data = new LinkedHashMap<>();
    private final List<String> lines = new ArrayList<>();

    public TelemetryImpl(TelemetryPanel panel) {
        this.panel = panel;
    }

    @Override
    public Item addData(String caption, Object value) {
        data.put(caption, String.valueOf(value));
        return new ItemImpl(caption);
    }

    @Override
    public Item addData(String caption, String format, Object... args) {
        String val = String.format(format, args);
        data.put(caption, val);
        return new ItemImpl(caption);
    }

    @Override
    public Line addLine(String line) {
        lines.add(line);
        return new LineImpl();
    }

    @Override
    public boolean update() {
        List<String> snapshot = new ArrayList<>();
        data.forEach((k, v) -> snapshot.add(k + " : " + v));
        lines.forEach(snapshot::add);
        SwingUtilities.invokeLater(() -> panel.setLines(snapshot));
        // Mirror real FTC SDK default autoClear=true: wipe state after each update()
        // so addLine/addData calls don't accumulate across loop iterations.
        data.clear();
        lines.clear();
        return true;
    }

    @Override
    public void clear() {
        data.clear();
        lines.clear();
    }

    private class ItemImpl implements Item {
        private final String key;
        ItemImpl(String key) { this.key = key; }

        @Override
        public Item setValue(Object value) {
            data.put(key, String.valueOf(value));
            return this;
        }

        @Override
        public Item setValue(String format, Object... args) {
            data.put(key, String.format(format, args));
            return this;
        }
    }

    private class LineImpl implements Line {
        @Override
        public Line addData(String caption, Object value) {
            data.put(caption, String.valueOf(value));
            return this;
        }
    }
}
