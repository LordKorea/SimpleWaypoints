package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import net.minecraft.client.gui.GuiScreen;

import java.util.function.Supplier;

/**
 * A paged GUI for waypoint related things.
 */
public abstract class PagedWaypointGui<T> extends PagedGui<T> {

    /**
     * The waypoint manager.
     */
    protected final WaypointManager manager;

    /**
     * Constructor.
     *
     * @param title      The title of this GUI.
     * @param manager    The waypoint manager.
     * @param parent     The parent GUI.
     * @param pageOffset The page offset of this GUI.
     */
    public PagedWaypointGui(final String title, final WaypointManager manager, final Supplier<GuiScreen> parent,
                            final int pageOffset) {
        super(title, parent, pageOffset);
        this.manager = manager;
    }
}
