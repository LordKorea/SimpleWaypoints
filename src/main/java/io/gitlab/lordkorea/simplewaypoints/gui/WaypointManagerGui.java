package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import nge.lk.mods.commonlib.gui.designer.GuiDesigner;
import nge.lk.mods.commonlib.gui.designer.RenderProperties;
import nge.lk.mods.commonlib.gui.designer.element.Box;
import nge.lk.mods.commonlib.gui.designer.element.Button;
import nge.lk.mods.commonlib.gui.designer.element.Label;
import nge.lk.mods.commonlib.gui.designer.util.Alignment;
import nge.lk.mods.commonlib.gui.designer.util.Padding;

import java.util.function.Consumer;

/**
 * The GUI used for managing waypoints.
 */
public class WaypointManagerGui extends GuiDesigner implements Consumer<Button> {

    /**
     * The waypoint manager.
     */
    private final WaypointManager manager;

    /**
     * The button which is used for creating waypoints.
     */
    private Button createWaypointButton;

    /**
     * The button which is used for browsing waypoints.
     */
    private Button browseWaypointsButton;

    /**
     * Constructor.
     */
    public WaypointManagerGui(final WaypointManager manager) {
        this.manager = manager;
        createGui();
    }

    @Override
    public void accept(final Button buttonElement) {
        if (buttonElement == createWaypointButton) {
            mc.displayGuiScreen(new WaypointEditorGui(() -> this, manager, null));
        } else if (buttonElement == browseWaypointsButton) {
            mc.displayGuiScreen(new WaypointGroupBrowserGui(() -> this, manager));
        }
    }

    @Override
    protected void createGui() {
        final Box contentPane = new Box(RenderProperties.fullSize(),
                Padding.relative(5, 5, 10, 5));

        contentPane.addToActive(Label.centered("Waypoint Manager", 0xAAAAAA));
        contentPane.addToActive(Box.relativeVerticalSpacer(15));

        createWaypointButton = new Button(this,
                Button.relativeProperties(35, true, true));
        createWaypointButton.getButton().displayString = "Create Waypoint";
        contentPane.addToActive(createWaypointButton);
        contentPane.addToActive(Box.relativeVerticalSpacer(10));

        browseWaypointsButton = new Button(this,
                Button.relativeProperties(35, true, true));
        browseWaypointsButton.getButton().displayString = "Browse Waypoints";
        contentPane.addToActive(browseWaypointsButton);
        contentPane.commitBucket(Alignment.TOP);

        final Button closeButton = new Button(b -> closeGui(),
                Button.relativeProperties(30, true, true));
        closeButton.getButton().displayString = "Close";
        contentPane.addRenderBucket(Alignment.BOTTOM, closeButton);

        root.addRenderBucket(Alignment.TOP, contentPane);
    }
}
