package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import nge.lk.mods.commonlib.gui.factory.GuiFactory;
import nge.lk.mods.commonlib.gui.factory.Positioning;
import nge.lk.mods.commonlib.gui.factory.element.ButtonElement;

import java.util.function.Consumer;

/**
 * The GUI used for managing waypoints.
 */
public class WaypointManagerGui extends GuiFactory implements Consumer<ButtonElement> {

    /**
     * The waypoint manager.
     */
    private WaypointManager manager;

    /**
     * The button which is used for creating waypoints.
     */
    private ButtonElement createWaypointButton;

    /**
     * The button which is used for browsing waypoints.
     */
    private ButtonElement browseWaypointsButton;

    /**
     * The button which is used for deleting waypoints.
     */
    private ButtonElement deleteWaypointsButton;

    /**
     * Constructor.
     */
    public WaypointManagerGui(final WaypointManager manager) {
        this.manager = manager;
        createGui();
    }

    @Override
    public void accept(final ButtonElement buttonElement) {
        if (buttonElement == createWaypointButton) {
            mc.displayGuiScreen(new WaypointEditorGui(this, manager, null));
        }
    }

    @Override
    protected void createGui() {
        setPadding(0.05, 0.05, 0.1, 0.05);
        addText(new Positioning().center()).setText("Waypoint Manager", 0xAAAAAA);
        addBlank(new Positioning().breakRow().relativeHeight(7));

        createWaypointButton = addButton(this,
                new Positioning().center().absoluteHeight(20).relativeWidth(35));
        createWaypointButton.getButton().displayString = "Create Waypoint";
        addBlank(new Positioning().breakRow().relativeHeight(7));

        browseWaypointsButton = addButton(this,
                new Positioning().center().absoluteHeight(20).relativeWidth(35));
        browseWaypointsButton.getButton().displayString = "Browse Waypoints";
        addBlank(new Positioning().breakRow().relativeHeight(7));

        deleteWaypointsButton = addButton(this,
                new Positioning().center().absoluteHeight(20).relativeWidth(35));
        deleteWaypointsButton.getButton().displayString = "Delete Waypoints";
        addBlank(new Positioning().breakRow().relativeHeight(7));

        addBlank(new Positioning().alignBottom().breakRow().relativeHeight(4));
        addButton(button -> closeGui(), new Positioning().alignBottom().center().absoluteHeight(20).relativeWidth(15))
                .getButton().displayString = "Close";
    }
}
