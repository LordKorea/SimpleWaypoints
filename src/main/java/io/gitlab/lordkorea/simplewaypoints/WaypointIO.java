package io.gitlab.lordkorea.simplewaypoints;

import nge.lk.mods.commonlib.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * (De-)serializes waypoints.
 */
public class WaypointIO {

    /**
     * The current waypoint data version.
     */
    private static final int WAYPOINT_VERSION = 1;

    /**
     * The file in which waypoints are stored.
     */
    private final File storageFile;

    /**
     * Constructor.
     *
     * @param storageFile The file in which waypoints are stored.
     */
    public WaypointIO(final File storageFile) {
        FileUtil.ensureFileExists(storageFile);
        this.storageFile = storageFile;
    }

    /**
     * Saves waypoints using this IO.
     *
     * @param waypoints The waypoints to save. All waypoints not contained are removed from storage.
     */
    public void saveWaypoints(final Iterable<Waypoint> waypoints) {
        try {
            FileUtil.writeLineStorage(WAYPOINT_VERSION, storageFile,
                    StreamSupport.stream(waypoints.spliterator(), false)
                            .map(WaypointIO::serializeWaypoint)
                            .iterator());
        } catch (final IOException e) {
            throw new IllegalStateException("Could not save waypoints.", e);
        }
    }

    /**
     * Loads waypoints using this IO.
     *
     * @return The loaded waypoints.
     */
    public Collection<Waypoint> loadWaypoints() {
        try {
            final List<Waypoint> waypoints = new ArrayList<>();
            FileUtil.readLineStorage(storageFile, (line, no) -> waypoints.add(deserializeWaypoint(line)),
                    (ver, old) -> old);
            return waypoints;
        } catch (final IOException e) {
            throw new IllegalStateException("Could not load waypoints.", e);
        }
    }

    /**
     * Serializes a waypoint to a string.
     *
     * @param waypoint The waypoint.
     * @return The serialized waypoint.
     */
    private static String serializeWaypoint(final Waypoint waypoint) {
        return String.join("\0", waypoint.getName(), Double.toString(waypoint.getX()),
                Double.toString(waypoint.getY()), Double.toString(waypoint.getZ()),
                Integer.toString(waypoint.getColorRGB()));
    }

    /**
     * Deserializes a waypoint from a string.
     *
     * @param serial The string representation.
     * @return The waypoint.
     */
    private static Waypoint deserializeWaypoint(final String serial) {
        final String[] parts = serial.split("\0");
        return new Waypoint(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]), Integer.parseInt(parts[4]));
    }
}
