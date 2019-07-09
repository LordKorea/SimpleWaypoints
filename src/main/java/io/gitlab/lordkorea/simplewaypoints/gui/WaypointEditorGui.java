package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.Waypoint;
import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import nge.lk.mods.commonlib.gui.ColorPicking;
import nge.lk.mods.commonlib.gui.GuiColorPicker;
import nge.lk.mods.commonlib.gui.factory.GuiFactory;
import nge.lk.mods.commonlib.gui.factory.Positioning;
import nge.lk.mods.commonlib.gui.factory.element.ButtonElement;
import nge.lk.mods.commonlib.gui.factory.element.InputElement;
import nge.lk.mods.commonlib.gui.factory.element.TextElement;

import java.util.Random;
import java.util.function.Consumer;

/**
 * The GUI used for editing waypoints.
 */
public class WaypointEditorGui extends GuiFactory implements ColorPicking, Consumer<ButtonElement> {

    /**
     * A random number generator.
     */
    private static final Random RANDOM = new Random();

    /**
     * The parent screen of this GUI.
     */
    private final GuiScreen parent;

    /**
     * The waypoint manager.
     */
    private final WaypointManager manager;

    /**
     * The waypoint being edited.
     */
    private final Waypoint editWaypoint;

    /**
     * The input element containing the waypoint name.
     */
    private InputElement nameElement;

    /**
     * The input elements containing the coordinates.
     */
    private final InputElement[] coordinateElements = new InputElement[3];

    /**
     * The button for changing the color.
     */
    private ButtonElement colorButton;

    /**
     * The currently selected color.
     */
    private int selectedColor;

    /**
     * The button for saving and going to the parent menu.
     */
    private ButtonElement saveButton;

    /**
     * The button for saving and closing all GUIs.
     */
    private ButtonElement saveCloseButton;

    /**
     * The button for going back to the parent menu.
     */
    private ButtonElement backButton;

    /**
     * Constructor.
     */
    public WaypointEditorGui(final GuiScreen parent, final WaypointManager manager, final Waypoint editWaypoint) {
        this.parent = parent;
        this.manager = manager;
        this.editWaypoint = editWaypoint;
        selectedColor = RANDOM.nextInt(0xFFFFFF + 1);
        createGui();
    }

    @Override
    public void onPickColor(final int color) {
        selectedColor = color;
    }

    @Override
    public void accept(final ButtonElement buttonElement) {
        if (buttonElement == colorButton) {
            mc.displayGuiScreen(new GuiColorPicker(selectedColor, this));
        } else if (buttonElement == backButton) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    protected void closeGui() {
        mc.displayGuiScreen(parent);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        boolean canSave = true;
        if (nameElement.getTextField().getText().isEmpty()) {
            canSave = false;
        }

        for (int i = 0; i < 3; i++) {
            try {
                final int coordinate = Integer.parseInt(coordinateElements[i].getTextField().getText());
                if (Math.abs(coordinate) > 3e7 || (i == 1 && (coordinate < 0 || coordinate > 256))) {
                    canSave = false;
                    break;
                }
            } catch (final NumberFormatException ignored) {
                canSave = false;
                break;
            }
        }

        saveButton.getButton().enabled = canSave;
        saveCloseButton.getButton().enabled = canSave;
        colorButton.getButton().packedFGColour = selectedColor;
    }

    @Override
    protected void createGui() {
        setPadding(0.05, 0.05, 0.1, 0.05);
        addText(new Positioning().center()).setText("Waypoint Manager > "
                + (isNewWaypoint() ? "Create" : "Edit"), 0xAAAAAA);
        addBlank(new Positioning().breakRow().absoluteHeight(15));

        addText(new Positioning().breakRow()).setText("Waypoint Name", 0xAAAAAA);
        nameElement = addInput(new Positioning().relativeWidth(85).absoluteHeight(20).breakRow());
        nameElement.getTextField().setMaxStringLength(30);
        nameElement.getTextField().setText(isNewWaypoint() ? "" : editWaypoint.getName());
        nameElement.getTextField().setCursorPositionZero();
        addBlank(new Positioning().breakRow().absoluteHeight(15));

        addText(new Positioning().breakRow()).setText("Waypoint Coordinates", 0xAAAAAA);
        addBlank(new Positioning().breakRow().absoluteHeight(3));

        final String[] captions = {"X:", "Y:", "Z:"};
        final double[] coords = {0.0, 0.0, 0.0};
        if (!isNewWaypoint()) {
            coords[0] = editWaypoint.getX();
            coords[1] = editWaypoint.getY();
            coords[2] = editWaypoint.getZ();
        } else {
            final Minecraft mc = Minecraft.getMinecraft();
            coords[0] = (int) Math.floor(mc.player.posX);
            coords[1] = (int) Math.floor(mc.player.posY + mc.player.eyeHeight);
            coords[2] = (int) Math.floor(mc.player.posZ);
        }
        for (int i = 0; i < 3; i++) {
            addText(new Positioning()).setText(captions[i], 0xAAAAAA);
            coordinateElements[i] = addInput(new Positioning().absoluteWidth(70).absoluteHeight(20));
            coordinateElements[i].getTextField().setMaxStringLength(16);
            coordinateElements[i].getTextField().setText(String.valueOf(coords[i]));
            coordinateElements[i].getTextField().setCursorPositionZero();
            addBlank(new Positioning().relativeWidth(3));
        }

        colorButton = addButton(this, new Positioning().absoluteWidth(50).absoluteHeight(20));
        colorButton.getButton().displayString = "Color";
        colorButton.getButton().packedFGColour = selectedColor;
        addBlank(new Positioning().absoluteHeight(20).breakRow());

        if (isNewWaypoint()) {
            addBlank(new Positioning().breakRow().absoluteHeight(15));
            addText(new Positioning().breakRow()).setText("Tip: In a hurry? You can use ??? to",
                    0xAAAAAA);
            addText(new Positioning()).setText("quickly create an anonymous waypoint", 0xAAAAAA);
        }

        addBlank(new Positioning().alignBottom().breakRow().relativeHeight(4));

        saveButton = addButton(this, new Positioning().alignBottom().relativeWidth(27).absoluteHeight(20));
        saveButton.getButton().displayString = "Save & Back";
        saveButton.getButton().enabled = false;
        addBlank(new Positioning().alignBottom().relativeWidth(3));

        saveCloseButton = addButton(this,
                new Positioning().alignBottom().relativeWidth(27).absoluteHeight(20));
        saveCloseButton.getButton().displayString = "Save & Close";
        saveCloseButton.getButton().enabled = false;
        addBlank(new Positioning().alignBottom().relativeWidth(3));

        backButton = addButton(this, new Positioning().alignBottom().relativeWidth(27).absoluteHeight(20));
        backButton.getButton().displayString = "Back";
    }

    /**
     * Checks whether a new waypoint is being created, instead of one being edited.
     *
     * @return {@code true} if a new waypoint is being created.
     */
    private boolean isNewWaypoint() {
        return editWaypoint == null;
    }
}
