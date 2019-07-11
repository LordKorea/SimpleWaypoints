package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.Waypoint;
import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import nge.lk.mods.commonlib.gui.ColorPickerGui;
import nge.lk.mods.commonlib.gui.ColorPicking;
import nge.lk.mods.commonlib.gui.designer.GuiDesigner;
import nge.lk.mods.commonlib.gui.designer.RenderProperties;
import nge.lk.mods.commonlib.gui.designer.element.Box;
import nge.lk.mods.commonlib.gui.designer.element.Button;
import nge.lk.mods.commonlib.gui.designer.element.Label;
import nge.lk.mods.commonlib.gui.designer.element.TextField;
import nge.lk.mods.commonlib.gui.designer.util.Alignment;
import nge.lk.mods.commonlib.gui.designer.util.Padding;

import java.util.Random;
import java.util.function.Consumer;

/**
 * The GUI used for editing waypoints.
 */
public class WaypointEditorGui extends GuiDesigner implements ColorPicking, Consumer<Button> {

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
    private TextField nameElement;

    /**
     * The input elements containing the coordinates.
     */
    private final TextField[] coordinateElements = new TextField[3];

    /**
     * The button for changing the color.
     */
    private Button colorButton;

    /**
     * The currently selected color.
     */
    private int selectedColor;

    /**
     * The button for deleting the waypoint.
     */
    private Button deleteButton;

    /**
     * The button for saving and going to the parent menu.
     */
    private Button saveButton;

    /**
     * The button for saving and closing all GUIs.
     */
    private Button saveCloseButton;

    /**
     * The button for going back to the parent menu.
     */
    private Button backButton;

    /**
     * Constructor.
     */
    public WaypointEditorGui(final GuiScreen parent, final WaypointManager manager, final Waypoint editWaypoint) {
        this.parent = parent;
        this.manager = manager;
        this.editWaypoint = editWaypoint;
        selectedColor = isNewWaypoint() ? RANDOM.nextInt(0xFFFFFF + 1) : editWaypoint.getColorRGB();
        createGui();
    }

    @Override
    public void onPickColor(final int color) {
        selectedColor = color;
    }

    @Override
    public void accept(final Button buttonElement) {
        if (buttonElement == colorButton) {
            mc.displayGuiScreen(new ColorPickerGui(selectedColor, this));
        } else if (buttonElement == backButton) {
            mc.displayGuiScreen(parent);
        } else if (buttonElement == saveButton || buttonElement == saveCloseButton) {
            final boolean close = buttonElement == saveCloseButton;

            final int[] coords = new int[3];
            for (int i = 0; i < 3; i++) {
                coords[i] = Integer.parseInt(coordinateElements[i].getTextField().getText());
            }

            final Waypoint newWaypoint = new Waypoint(nameElement.getTextField().getText(), coords[0], coords[1],
                    coords[2], selectedColor);
            if (!isNewWaypoint()) {
                manager.removeWaypoint(editWaypoint);
            }
            manager.addWaypoint(newWaypoint);
            manager.save();

            if (close) {
                mc.displayGuiScreen(null);
            } else {
                mc.displayGuiScreen(parent);
            }
        } else if (buttonElement == deleteButton) {
            manager.removeWaypoint(editWaypoint);
            manager.save();
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
        final Box contentPane = new Box(RenderProperties.fullSize(),
                Padding.relative(5, 5, 10, 5));

        final String caption = "Waypoint Manager > " + (isNewWaypoint() ? "Create" : "Edit");
        contentPane.addToActive(Label.centered(caption, 0xAAAAAA));
        contentPane.addToActive(Box.relativeVerticalSpacer(7));

        contentPane.addToActive(Label.regular("Waypoint Name", 0xAAAAAA, true));
        nameElement = new TextField(TextField.relativeProperties(100, true));
        nameElement.getTextField().setMaxStringLength(30);
        nameElement.getTextField().setText(isNewWaypoint() ? "" : editWaypoint.getName());
        nameElement.getTextField().setCursorPositionZero();
        contentPane.addToActive(nameElement);
        contentPane.addToActive(Box.relativeVerticalSpacer(7));

        contentPane.addToActive(Label.regular("Waypoint Coordinates", 0xAAAAAA, true));
        final String[] captions = {"X:", "Y:", "Z:"};
        final int[] coords = new int[3];
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
            final Box labelBox = new Box(RenderProperties.builder().relativeWidth(7).absoluteHeight(20).build());
            labelBox.addToActive(Box.absoluteVerticalSpacer(3));
            labelBox.addToActive(Box.absoluteHorizontalPlaceholder(3, Alignment.RIGHT));
            labelBox.addToActive(Label.regular(captions[i], 0xAAAAAA, false, Alignment.RIGHT));
            labelBox.commitBucket(Alignment.TOP);
            contentPane.addToActive(labelBox);

            coordinateElements[i] = new TextField(TextField.relativeProperties(18));
            coordinateElements[i].getTextField().setMaxStringLength(16);
            coordinateElements[i].getTextField().setText(String.valueOf(coords[i]));
            coordinateElements[i].getTextField().setCursorPositionZero();
            contentPane.addToActive(coordinateElements[i]);
        }

        contentPane.addToActive(Box.relativeHorizontalPlaceholder(7));
        colorButton = new Button(this, Button.relativeProperties(18, true));
        colorButton.getButton().displayString = "Color";
        colorButton.getButton().packedFGColour = selectedColor;
        contentPane.addToActive(colorButton);
        contentPane.addToActive(Box.relativeVerticalSpacer(7));

        if (isNewWaypoint()) {
            final String quickWaypointText = manager.getQuickWaypointKey().getDisplayName();
            contentPane.addToActive(
                    Label.regular(String.format("Tip: In a hurry? You can use %s to", quickWaypointText),
                    0xAAAAAA, true));
            contentPane.addToActive(Label.regular("quickly create an anonymous waypoint",
                    0xAAAAAA, true));
        }
        contentPane.commitBucket(Alignment.TOP);

        saveButton = new Button(this, Button.relativeProperties(30));
        saveButton.getButton().displayString = "Save & Back";
        saveButton.getButton().enabled = false;
        contentPane.addToActive(saveButton);
        contentPane.addToActive(Box.relativeHorizontalPlaceholder(5));

        saveCloseButton = new Button(this, Button.relativeProperties(30));
        saveCloseButton.getButton().displayString = "Save & Close";
        saveCloseButton.getButton().enabled = false;
        contentPane.addToActive(saveCloseButton);
        contentPane.addToActive(Box.relativeHorizontalPlaceholder(5));

        backButton = new Button(this, Button.relativeProperties(30));
        backButton.getButton().displayString = "Back";
        contentPane.addToActive(backButton);
        contentPane.commitBucket(Alignment.BOTTOM);

        // Add a delete button, if a waypoint is being edited.
        if (!isNewWaypoint()) {
            deleteButton = new Button(this, Button.relativeProperties(15, false,
                    false, Alignment.RIGHT));
            deleteButton.getButton().displayString = "Delete";
            deleteButton.getButton().packedFGColour = 0xAA0000;
            contentPane.addRenderBucket(Alignment.TOP, deleteButton);
        }

        root.addRenderBucket(Alignment.TOP, contentPane);
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
