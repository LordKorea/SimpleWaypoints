package io.gitlab.lordkorea.simplewaypoints;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Keeps track of and manages waypoints.
 */
public class WaypointManager {

    /**
     * The collection in which all waypoints that are registered are stored.
     */
    private final Collection<Waypoint> waypoints = new LinkedHashSet<>();

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
        dirty = waypoints.add(waypoint) || dirty;
    }

    /**
     * Removes a waypoint.
     *
     * @param waypoint The waypoint.
     */
    public void removeWaypoint(final Waypoint waypoint) {
        dirty = waypoints.remove(waypoint) || dirty;
    }

    /**
     * Returns the waypoints that are registered.
     *
     * @return The waypoints.
     */
    public Iterable<Waypoint> getWaypoints() {
        return Collections.unmodifiableCollection(waypoints);
    }
}
