package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import net.minecraft.client.gui.navigation.ScreenRectangle;

import java.util.function.BiConsumer;

public enum Alignment
{
    // PLACEMENT_ALIGNMENT
    ABOVE_LEFT((menu, rectangle) -> {
        menu.setX(rectangle.left());
        menu.setY(rectangle.top() - menu.getHeight());
    }),
    ABOVE_RIGHT((menu, rectangle) -> {
        menu.setX(rectangle.right() - menu.getWidth());
        menu.setY(rectangle.top() - menu.getHeight());
    }),
    BELOW_LEFT((menu, rectangle) -> {
        menu.setX(rectangle.left() - menu.border());
        menu.setY(rectangle.bottom());
    }),
    BELOW_RIGHT((menu, rectangle) -> {
        menu.setX(rectangle.right() - menu.getWidth() + menu.border());
        menu.setY(rectangle.bottom());
    }),
    END_TOP((menu, rectangle) -> {
        menu.setX(rectangle.right());
        menu.setY(rectangle.top() - menu.border());
    }),
    END_BOTTOM((menu, rectangle) -> {
        menu.setX(rectangle.right());
        menu.setY(rectangle.bottom() - menu.getHeight() + menu.border());
    });

    private final BiConsumer<PopupMenu, ScreenRectangle> aligner;

    Alignment(BiConsumer<PopupMenu, ScreenRectangle> positioner)
    {
        this.aligner = positioner;
    }

    public BiConsumer<PopupMenu, ScreenRectangle> aligner()
    {
        return this.aligner;
    }
}
