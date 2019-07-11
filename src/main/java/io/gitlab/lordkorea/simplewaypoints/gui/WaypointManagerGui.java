package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import nge.lk.mods.commonlib.gui.designer.GuiDesigner;
import nge.lk.mods.commonlib.gui.designer.RenderProperties;
import nge.lk.mods.commonlib.gui.designer.element.Box;
import nge.lk.mods.commonlib.gui.designer.element.Button;
import nge.lk.mods.commonlib.gui.designer.element.Label;
import nge.lk.mods.commonlib.gui.designer.util.Alignment;
import nge.lk.mods.commonlib.gui.designer.util.Padding;
import nge.lk.mods.commonlib.gui.factory.GuiFactory;
import nge.lk.mods.commonlib.gui.factory.Positioning;
import nge.lk.mods.commonlib.gui.factory.element.ButtonElement;

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
     * The button which is used for deleting waypoints.
     */
    private Button deleteWaypointsButton;

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
            mc.displayGuiScreen(new WaypointEditorGui(this, manager, null));
        } else if (buttonElement == browseWaypointsButton) {
            mc.displayGuiScreen(new WaypointBrowserGui(this, manager, 0));
        }
    }

    @Override
    protected void createGui() {
        final Box contentPane = new Box(RenderProperties.builder().fullSize().build(),
                Padding.relative(5, 5, 10, 5));

        final Label headerLabel = new Label(RenderProperties.builder().centered().groupBreaking().build());
        headerLabel.setText("Waypoint Manager", 0xAAAAAA);
        headerLabel.pack();
        contentPane.addToActive(headerLabel);
        contentPane.addToActive(new Box(RenderProperties.builder().relativeHeight(7).groupBreaking().build()));

        createWaypointButton = new Button(this, RenderProperties.builder().centered().absoluteHeight(20)
                .relativeWidth(35).groupBreaking().build());
        createWaypointButton.getButton().displayString = "Create Waypoint";
        contentPane.addToActive(createWaypointButton);
        contentPane.addToActive(new Box(RenderProperties.builder().relativeHeight(7).groupBreaking().build()));

        browseWaypointsButton = new Button(this, RenderProperties.builder().centered().absoluteHeight(20)
                .relativeWidth(35).groupBreaking().build());
        browseWaypointsButton.getButton().displayString = "Browse Waypoints";
        contentPane.addToActive(browseWaypointsButton);
        contentPane.addToActive(new Box(RenderProperties.builder().relativeHeight(7).groupBreaking().build()));

        deleteWaypointsButton = new Button(this, RenderProperties.builder().absoluteHeight(20)
                .relativeWidth(35).centered().groupBreaking().build());
        deleteWaypointsButton.getButton().displayString = "Delete Waypoints";
        contentPane.addToActive(deleteWaypointsButton);
        contentPane.commitBucket(Alignment.TOP);

        final Button closeButton = new Button(b -> closeGui(), RenderProperties.builder().centered().absoluteHeight(20)
                .relativeWidth(30).build());
        closeButton.getButton().displayString = "Close";
        contentPane.addRenderBucket(Alignment.BOTTOM, closeButton);

        root.addRenderBucket(Alignment.TOP, contentPane);
    }
}
