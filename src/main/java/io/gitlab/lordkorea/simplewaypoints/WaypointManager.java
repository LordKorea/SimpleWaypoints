package io.gitlab.lordkorea.simplewaypoints;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps track of and manages waypoints.
 */
public class WaypointManager {

    /**
     * The list in which all waypoints that are registered are stored.
     */
    private final List<Waypoint> waypoints = new ArrayList<>();

    /**
     * The waypoint IO which is used for storing and loading waypoints.
     */
    private final WaypointIO waypointIO;

    /**
     * Whether waypoints have changed and need to be saved.
     */
    private boolean dirty = false;

    /**
     * Constructor.
     *
     * @param storageFile The file which is used for storing waypoints.
     */
    public WaypointManager(final File storageFile) {
        waypointIO = new WaypointIO(storageFile);
        waypoints.addAll(waypointIO.loadWaypoints());
    }

    /**
     * Saves waypoints, if required. If waypoints haven't changed, nothing is done.
     */
    public void save() {
        if (dirty) {
            dirty = false;
            waypointIO.saveWaypoints(waypoints);
        }
    }

    /**
     * Adds a waypoint.
     *
     * @param waypoint The waypoint.
     */
    public void addWaypoint(final Waypoint waypoint) {
        waypoints.add(waypoint);
        dirty = true;
    }

    /**
     * Returns the waypoints that are registered.
     *
     * @return The waypoints.
     */
    public Iterable<Waypoint> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }
}
