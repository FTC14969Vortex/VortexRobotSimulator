package com.vortex.simulator.engine;

import java.util.List;

/**
 * INTO THE DEEP field AprilTag positions.
 * Coordinate system: origin at field center, X positive right, Y positive up (north).
 * Tags face inward toward the field center.
 */
public class AprilTagLayout {

    public record Tag(int id, double x, double y, double facingDeg) {}

    // IDs 11-16, positions in inches
    public static final List<Tag> TAGS = List.of(
        new Tag(11,  24,  72,  180), // north wall, facing south
        new Tag(12, -24,  72,  180), // north wall, facing south
        new Tag(13, -72,  24,   90), // west wall, facing east
        new Tag(14, -72, -24,   90), // west wall, facing east
        new Tag(15, -24, -72,    0), // south wall, facing north
        new Tag(16,  24, -72,    0)  // south wall, facing north
    );

    public static Tag getById(int id) {
        return TAGS.stream()
            .filter(t -> t.id() == id)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown tag ID: " + id));
    }
}
