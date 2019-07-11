package io.gitlab.lordkorea.simplewaypoints.gui;

import io.gitlab.lordkorea.simplewaypoints.Waypoint;
import io.gitlab.lordkorea.simplewaypoints.WaypointManager;
import net.minecraft.client.gui.GuiScreen;
import nge.lk.mods.commonlib.gui.designer.GuiDesigner;
import nge.lk.mods.commonlib.gui.designer.RenderProperties;
import nge.lk.mods.commonlib.gui.designer.element.Box;
import nge.lk.mods.commonlib.gui.designer.element.Button;
import nge.lk.mods.commonlib.gui.designer.element.Label;
import nge.lk.mods.commonlib.gui.designer.util.Alignment;
import nge.lk.mods.commonlib.gui.designer.util.Padding;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * The GUI used for managing waypoints.
 */
public class WaypointBrowserGui extends GuiDesigner implements Consumer<Button> {

    /**
     * The number of entries per page.
     */
    private static final int ENTRIES_PER_PAGE = 10;

    /**
     * The parent screen.
     */
    private final GuiScreen parent;

    /**
     * The waypoint manager.
     */
    private final WaypointManager manager;

    /**
     * The page offset of this browser instance.
     */
    private final int pageOffset;

    /**
     * Constructor.
     */
    public WaypointBrowserGui(final GuiScreen parent, final WaypointManager manager, final int pageOffset) {
        this.parent = parent;
        this.manager = manager;
        this.pageOffset = pageOffset;
        createGui();
    }

    @Override
    public void accept(final Button buttonElement) {
        // TODO
    }

    @Override
    protected void closeGui() {
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void createGui() {
        final Box contentPane = new Box(RenderProperties.fullSize(),
                Padding.relative(5, 5, 10, 5));

        contentPane.addToActive(Label.centered("Waypoint Browser", 0xAAAAAA));
        contentPane.addToActive(Box.relativeVerticalSpacer(7));

        int i = 0;
        int skip = pageOffset;
        final Collection<Waypoint> waypoints = manager.getWaypoints();
        for (final Waypoint waypoint : waypoints) {
            if (skip-- > 0) {
                continue;
            }

            final RenderProperties.RenderPropertiesBuilder properties = RenderProperties.builder().relativeWidth(45)
                    .absoluteHeight(20);
            if (i % 2 != 0) {
                properties.secondaryAlignment(Alignment.RIGHT).groupBreaking();
            }

            final Button button = new Button(this, properties.build());
            button.getButton().displayString = waypoint.getShortName();
            button.getButton().packedFGColour = waypoint.getColorRGB();
            contentPane.addToActive(button);

            if (i % 2 != 0) {
                contentPane.addToActive(Box.relativeVerticalSpacer(5));
            }
            i++;

            if (i == ENTRIES_PER_PAGE) {
                break;
            }
        }
        contentPane.commitBucket(Alignment.TOP);

        final Button closeButton = new Button(b -> closeGui(),
                Button.relativeProperties(30, true, true));
        closeButton.getButton().displayString = "Back";
        contentPane.addRenderBucket(Alignment.BOTTOM, closeButton);

        // Show pagination buttons.
        if (pageOffset > 0) {
            contentPane.addToActive(Box.relativeHorizontalPlaceholder(15));
            final Button prevButton = new Button(b -> mc.displayGuiScreen(
                    new WaypointBrowserGui(parent, manager, pageOffset - ENTRIES_PER_PAGE)),
                    Button.relativeProperties(15));
            prevButton.getButton().displayString = "<<<";
            contentPane.addToActive(prevButton);
        }
        if (waypoints.size() - pageOffset > ENTRIES_PER_PAGE) {
            contentPane.addToActive(Box.relativeHorizontalPlaceholder(15, Alignment.RIGHT));
            final Button nextButton = new Button(b -> mc.displayGuiScreen(
                    new WaypointBrowserGui(parent, manager, pageOffset + ENTRIES_PER_PAGE)),
                    Button.relativeProperties(15, false, false, Alignment.RIGHT));
            nextButton.getButton().displayString = ">>>";
            contentPane.addToActive(nextButton);
        }
        contentPane.commitBucket(Alignment.BOTTOM);

        root.addRenderBucket(Alignment.TOP, contentPane);
    }
}
