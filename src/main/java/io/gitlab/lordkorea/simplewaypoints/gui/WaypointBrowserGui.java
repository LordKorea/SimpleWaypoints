package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.Waypoint;
import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import nge.lk.mods.commonlib.gui.designer.element.Button;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * The GUI used for browsing waypoints.
 */
public class WaypointBrowserGui extends PagedWaypointGui<Waypoint> {

    /**
     * The waypoint group of this waypoint browser.
     */
    private final String group;

    /**
     * Constructor.
     *
     * @param parent     The parent GUI.
     * @param manager    The waypoint manager.
     * @param pageOffset The page offset of the GUI.
     * @param group      The waypoint group.
     */
    private WaypointBrowserGui(final Supplier<GuiScreen> parent, final WaypointManager manager, final int pageOffset,
                               final String group) {
        super("Waypoint Browser", manager, parent, pageOffset);
        this.group = group;
        createGui();
    }

    /**
     * Constructor.
     *
     * @param parent  The parent GUI.
     * @param manager The waypoint manager.
     * @param group   The waypoint group.
     */
    public WaypointBrowserGui(final Supplier<GuiScreen> parent, final WaypointManager manager, final String group) {
        this(parent, manager, 0, group);
    }

    @Override
    protected GuiScreen createPageWithOffset(final int offset) {
        return new WaypointBrowserGui(parent, manager, offset, group);
    }

    @Override
    protected Collection<Waypoint> getData() {
        return manager.getWaypoints(group);
    }

    @Override
    protected void formatButton(final Waypoint obj, final GuiButton btn) {
        btn.displayString = obj.getShortName();
        btn.packedFGColour = obj.getColorRGB();
    }

    @Override
    public void accept(final Button buttonElement) {
        final Waypoint waypoint = (Waypoint) buttonElement.getMetadata();
        mc.displayGuiScreen(new WaypointEditorGui(this::recreateCurrent, manager, waypoint));
    }
}
