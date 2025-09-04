package com.mrcrayfish.backpacked.client.gui.screen.widget.dropdown;

import net.minecraft.client.gui.navigation.ScreenRectangle;

import java.util.function.BiConsumer;

public enum Alignment
{
    ABOVE_LEFT((menu, rectangle) -> {
        menu.setX(rectangle.left());
        menu.setY(rectangle.top() - menu.getHeight());
    }),
    ABOVE_RIGHT((menu, rectangle) -> {
        menu.setX(rectangle.right() - menu.getWidth());
        menu.setY(rectangle.top() - menu.getHeight());
    }),
    BELOW_LEFT((menu, rectangle) -> {
        menu.setX(rectangle.left() - 1);
        menu.setY(rectangle.bottom());
    }),
    BELOW_RIGHT((menu, rectangle) -> {
        menu.setX(rectangle.right() - menu.getWidth() + 1);
        menu.setY(rectangle.bottom());
    }),
    END_TOP((menu, rectangle) -> {
        menu.setX(rectangle.right());
        menu.setY(rectangle.top() - 1);
    }),
    END_BOTTOM((menu, rectangle) -> {
        menu.setX(rectangle.right());
        menu.setY(rectangle.bottom() - menu.getHeight() + 1);
    });

    private final BiConsumer<DropdownMenu, ScreenRectangle> aligner;

    Alignment(BiConsumer<DropdownMenu, ScreenRectangle> positioner)
    {
        this.aligner = positioner;
    }

    public BiConsumer<DropdownMenu, ScreenRectangle> aligner()
    {
        return this.aligner;
    }
}
