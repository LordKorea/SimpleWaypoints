package io.gitlab.lordkorea.simplewaypoints;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.settings.KeyBinding;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Keeps track of and manages waypoints.
 */
public class WaypointManager {

    /**
     * The map in which all waypoints that are registered are stored. The key is the waypoint group.
     */
    private final Map<String, Set<Waypoint>> waypoints = new LinkedHashMap<>();

    /**
     * The waypoint IO which is used for storing and loading waypoints.
     */
    private final WaypointIO waypointIO;

    /**
     * The quick waypoint key binding.
     */
    private @Getter final KeyBinding quickWaypointKey;

    /**
     * The currently selected waypoint group.
     */
    private @Getter @Setter String activeGroup;

    /**
     * Whether waypoints have changed and need to be saved.
     */
    private boolean dirty = false;

    /**
     * Constructor.
     *
     * @param storageFile The file which is used for storing waypoints.
     */
    public WaypointManager(final File storageFile, final KeyBinding quickWaypointKey) {
        waypointIO = new WaypointIO(storageFile);
        this.quickWaypointKey = quickWaypointKey;
        for (final Waypoint waypoint : waypointIO.loadWaypoints()) {
            waypoints.computeIfAbsent(waypoint.getGroup(), g -> new LinkedHashSet<>()).add(waypoint);
        }

        if (!waypoints.isEmpty()) {
            activeGroup = waypoints.keySet().stream().findFirst().get();
        } else {
            activeGroup = "general";
        }
    }

    /**
     * Saves waypoints, if required. If waypoints haven't changed, nothing is done.
     */
    public void save() {
        if (dirty) {
            dirty = false;
            waypointIO.saveWaypoints(waypoints.values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
        }
    }

    /**
     * Adds a waypoint.
     *
     * @param waypoint The waypoint.
     */
    public void addWaypoint(final Waypoint waypoint) {
        final Set<Waypoint> group = waypoints.computeIfAbsent(waypoint.getGroup(), g -> new LinkedHashSet<>());
        dirty = group.add(waypoint) || dirty;
    }

    /**
     * Removes a waypoint.
     *
     * @param waypoint The waypoint.
     */
    public void removeWaypoint(final Waypoint waypoint) {
        final Set<Waypoint> group = waypoints.get(waypoint.getGroup());
        if (group == null) {
            return;
        }

        dirty = group.remove(waypoint) || dirty;

        if (group.isEmpty()) {
            waypoints.remove(waypoint.getGroup());
        }
    }

    /**
     * Returns the waypoints that are registered.
     *
     * @return The waypoints.
     */
    public Collection<Waypoint> getWaypoints(final String group) {
        return Collections.unmodifiableCollection(waypoints.getOrDefault(group, Collections.emptySet()));
    }

    /**
     * Obtains the waypoints that are in the currently active group.
     *
     * @return The active waypoints.
     */
    public Collection<Waypoint> getActiveWaypoints() {
        return getWaypoints(activeGroup);
    }

    /**
     * Cycles the active waypoint group.
     */
    public void cycleActiveGroup() {
        String firstGroup = null;
        boolean next = false;
        for (final String group : waypoints.keySet()) {
            if (firstGroup == null) {
                firstGroup = group;
            }

            if (next) {
                activeGroup = group;
                return;
            }

            if (group.equals(activeGroup)) {
                next = true;
            }
        }

        activeGroup = firstGroup;
    }

    /**
     * Returns the known waypoint groups.
     *
     * @return The groups.
     */
    public Collection<String> getGroups() {
        return waypoints.keySet();
    }
}
