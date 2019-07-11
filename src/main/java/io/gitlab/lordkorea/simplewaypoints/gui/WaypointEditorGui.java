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
        selectedColor = RANDOM.nextInt(0xFFFFFF + 1);
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
        final Box contentPane = new Box(RenderProperties.builder().fullSize().build(),
                Padding.relative(5, 5, 10, 5));

        final Label headerLabel = new Label(RenderProperties.builder().centered().groupBreaking().build());
        headerLabel.setText("Waypoint Manager > " + (isNewWaypoint() ? "Create" : "Edit"), 0xAAAAAA);
        headerLabel.pack();
        contentPane.addToActive(headerLabel);
        contentPane.addToActive(new Box(RenderProperties.builder().groupBreaking().absoluteHeight(15).build()));

        final Label waypointNameLabel = new Label(RenderProperties.builder().groupBreaking().build());
        waypointNameLabel.setText("Waypoint Name", 0xAAAAAA);
        waypointNameLabel.pack();
        contentPane.addToActive(waypointNameLabel);

        nameElement = new TextField(RenderProperties.builder().relativeWidth(100).absoluteHeight(20).groupBreaking()
                .build());
        nameElement.getTextField().setMaxStringLength(30);
        nameElement.getTextField().setText(isNewWaypoint() ? "" : editWaypoint.getName());
        nameElement.getTextField().setCursorPositionZero();
        contentPane.addToActive(nameElement);
        contentPane.addToActive(new Box(RenderProperties.builder().groupBreaking().absoluteHeight(15).build()));

        final Label waypointCoordsLabel = new Label(RenderProperties.builder().groupBreaking().build());
        waypointCoordsLabel.setText("Waypoint Coordinates", 0xAAAAAA);
        waypointCoordsLabel.pack();
        contentPane.addToActive(waypointCoordsLabel);
        contentPane.addToActive(new Box(RenderProperties.builder().groupBreaking().absoluteHeight(3).build()));

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
            final Label coordLabel = new Label(RenderProperties.builder().secondaryAlignment(Alignment.RIGHT).build());
            coordLabel.setText(captions[i], 0xAAAAAA);
            coordLabel.pack();
            labelBox.addToActive(new Box(RenderProperties.builder().groupBreaking().absoluteHeight(3).build()));
            labelBox.addToActive(new Box(RenderProperties.builder().absoluteWidth(3).secondaryAlignment(Alignment.RIGHT)
                    .build()));
            labelBox.addToActive(coordLabel);
            labelBox.commitBucket(Alignment.TOP);
            contentPane.addToActive(labelBox);

            coordinateElements[i] = new TextField(RenderProperties.builder().relativeWidth(18).absoluteHeight(20)
                    .build());
            coordinateElements[i].getTextField().setMaxStringLength(16);
            coordinateElements[i].getTextField().setText(String.valueOf(coords[i]));
            coordinateElements[i].getTextField().setCursorPositionZero();
            contentPane.addToActive(coordinateElements[i]);
        }

        contentPane.addToActive(new Box(RenderProperties.builder().relativeWidth(7).build()));
        colorButton = new Button(this, RenderProperties.builder().relativeWidth(18).absoluteHeight(20)
                .build());
        colorButton.getButton().displayString = "Color";
        colorButton.getButton().packedFGColour = selectedColor;
        contentPane.addToActive(colorButton);
        contentPane.addToActive(new Box(RenderProperties.builder().groupBreaking().absoluteHeight(20).build()));

        if (isNewWaypoint()) {
            contentPane.addToActive(new Box(RenderProperties.builder().groupBreaking().absoluteHeight(15).build()));

            final Label tip1 = new Label(RenderProperties.builder().groupBreaking().build());
            tip1.setText("Tip: In a hurry? You can use ??? to", 0xAAAAAA);
            tip1.pack();
            contentPane.addToActive(tip1);

            final Label tip2 = new Label(RenderProperties.builder().groupBreaking().build());
            tip2.setText("quickly create an anonymous waypoint", 0xAAAAAA);
            tip2.pack();
            contentPane.addToActive(tip2);
        }
        contentPane.commitBucket(Alignment.TOP);

        saveButton = new Button(this, RenderProperties.builder().relativeWidth(30).absoluteHeight(20)
                .build());
        saveButton.getButton().displayString = "Save & Back";
        saveButton.getButton().enabled = false;
        contentPane.addToActive(saveButton);
        contentPane.addToActive(new Box(RenderProperties.builder().relativeWidth(5).build()));

        saveCloseButton = new Button(this, RenderProperties.builder().relativeWidth(30).absoluteHeight(20)
                .build());
        saveCloseButton.getButton().displayString = "Save & Close";
        saveCloseButton.getButton().enabled = false;
        contentPane.addToActive(saveCloseButton);
        contentPane.addToActive(new Box(RenderProperties.builder().relativeWidth(5).build()));

        backButton = new Button(this, RenderProperties.builder().relativeWidth(30).absoluteHeight(20)
                .build());
        backButton.getButton().displayString = "Back";
        contentPane.addToActive(backButton);

        contentPane.commitBucket(Alignment.BOTTOM);
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
