package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import nge.lk.mods.commonlib.gui.designer.element.Button;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * The GUI used for browsing waypoint groups.
 */
public class WaypointGroupBrowserGui extends PagedWaypointGui<String> {

    /**
     * Constructor.
     *
     * @param parent     The parent GUI.
     * @param manager    The waypoint manager.
     * @param pageOffset The page offset.
     */
    private WaypointGroupBrowserGui(final Supplier<GuiScreen> parent, final WaypointManager manager,
                                    final int pageOffset) {
        super("Waypoint Browser", manager, parent, pageOffset);
        createGui();
    }

    /**
     * Constructor.
     *
     * @param parent  The parent GUI.
     * @param manager The waypoint manager.
     */
    public WaypointGroupBrowserGui(final Supplier<GuiScreen> parent, final WaypointManager manager) {
        this(parent, manager, 0);
    }

    @Override
    protected GuiScreen createPageWithOffset(final int offset) {
        return new WaypointGroupBrowserGui(parent, manager, offset);
    }

    @Override
    protected Collection<String> getData() {
        return manager.getGroups();
    }

    @Override
    protected void formatButton(final String obj, final GuiButton btn) {
        btn.displayString = obj;
    }

    @Override
    public void accept(final Button buttonElement) {
        final String group = (String) buttonElement.getMetadata();
        mc.displayGuiScreen(new WaypointBrowserGui(this::recreateCurrent, manager, group));
    }
}
