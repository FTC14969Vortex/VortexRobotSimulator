package com.vortex.simulator.api;

import java.util.HashMap;
import java.util.Map;

public class HardwareMap {
    private final Map<String, Object> devices = new HashMap<>();

    public void put(String name, Object device) {
        devices.put(name, device);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type, String name) {
        Object device = devices.get(name);
        if (device == null) {
            throw new IllegalArgumentException(
                "Hardware device not found: '" + name + "' of type " + type.getSimpleName() +
                ". Check Constants.java and the simulator registry.");
        }
        if (!type.isInstance(device)) {
            throw new ClassCastException(
                "Device '" + name + "' is " + device.getClass().getSimpleName() +
                ", not " + type.getSimpleName());
        }
        return type.cast(device);
    }

    public boolean contains(String name) {
        return devices.containsKey(name);
    }
}
