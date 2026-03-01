package com.vortex.simulator.api;

public interface Telemetry {
    Item addData(String caption, Object value);
    Item addData(String caption, String format, Object... args);
    Line addLine(String line);
    boolean update();
    void clear();

    interface Item {
        Item setValue(Object value);
        Item setValue(String format, Object... args);
    }

    interface Line {
        Line addData(String caption, Object value);
    }
}
