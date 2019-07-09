package io.gitlab.lordkorea.simplewaypoints;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a waypoint.
 */
@Getter
@RequiredArgsConstructor
public class Waypoint {

    /**
     * The name of this waypoint.
     */
    private final String name;

    /**
     * The x position of this waypoint.
     */
    private final double x;

    /**
     * The y position of this waypoint.
     */
    private final double y;

    /**
     * The z position of this waypoint.
     */
    private final double z;

    /**
     * The RGB encoded color of this waypoint.
     */
    private final int colorRGB;
}
