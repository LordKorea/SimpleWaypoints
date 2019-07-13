package io.gitlab.lordkorea.simplewaypoints.gui;

import net.minecraft.client.gui.GuiButton;
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
import java.util.function.Supplier;

/**
 * Represents a paged GUI.
 */
public abstract class PagedGui<T> extends GuiDesigner implements Consumer<Button> {

    /**
     * The number of entries per page.
     */
    private static final int ENTRIES_PER_PAGE = 10;

    /**
     * The parent screen.
     */
    protected final Supplier<GuiScreen> parent;

    /**
     * The title of this GUI.
     */
    private final String title;

    /**
     * The page offset of this browser instance.
     */
    private final int pageOffset;

    /**
     * Constructor.
     *
     * @param title      The title of this GUI.
     * @param parent     The parent GUI.
     * @param pageOffset The page offset of this GUI.
     */
    public PagedGui(final String title, final Supplier<GuiScreen> parent, final int pageOffset) {
        this.title = title;
        this.parent = parent;
        this.pageOffset = pageOffset;
    }

    /**
     * Recreates the current page GUI.
     *
     * @return The page GUI.
     */
    protected GuiScreen recreateCurrent() {
        return createPageWithOffset(pageOffset);
    }

    /**
     * Returns a GUI for a page with the given offset.
     *
     * @param offset The offset.
     * @return The page GUI.
     */
    protected abstract GuiScreen createPageWithOffset(final int offset);

    /**
     * Returns the data for this paged GUI.
     *
     * @return The data.
     */
    protected abstract Collection<T> getData();

    /**
     * Formats a button for an object.
     *
     * @param obj The object.
     * @param btn The button.
     */
    protected abstract void formatButton(final T obj, final GuiButton btn);

    @Override
    protected void createGui() {
        final Box contentPane = new Box(RenderProperties.fullSize(),
                Padding.relative(5, 5, 10, 5));

        contentPane.addToActive(Label.centered(title, 0xAAAAAA));
        contentPane.addToActive(Box.relativeVerticalSpacer(7));

        int i = 0;
        int skip = pageOffset;
        final Collection<T> data = getData();
        for (final T object : data) {
            if (skip-- > 0) {
                continue;
            }

            final RenderProperties.RenderPropertiesBuilder properties = RenderProperties.builder().relativeWidth(45)
                    .absoluteHeight(20);
            if (i % 2 != 0) {
                properties.secondaryAlignment(Alignment.RIGHT).groupBreaking();
            }

            final Button button = new Button(this, properties.build());
            formatButton(object, button.getButton());
            button.setMetadata(object);
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
            final Button prevButton = new Button(
                    b -> mc.displayGuiScreen(createPageWithOffset(pageOffset - ENTRIES_PER_PAGE)),
                    Button.relativeProperties(15));
            prevButton.getButton().displayString = "<<<";
            contentPane.addToActive(prevButton);
        }
        if (data.size() - pageOffset > ENTRIES_PER_PAGE) {
            contentPane.addToActive(Box.relativeHorizontalPlaceholder(15, Alignment.RIGHT));
            final Button nextButton = new Button(
                    b -> mc.displayGuiScreen(createPageWithOffset(pageOffset + ENTRIES_PER_PAGE)),
                    Button.relativeProperties(15, false, false, Alignment.RIGHT));
            nextButton.getButton().displayString = ">>>";
            contentPane.addToActive(nextButton);
        }
        contentPane.commitBucket(Alignment.BOTTOM);

        root.addRenderBucket(Alignment.TOP, contentPane);
    }

    @Override
    protected void closeGui() {
        mc.displayGuiScreen(parent.get());
    }
}
