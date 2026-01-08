package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.item;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.MenuItem;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ButtonItem extends MenuItem
{
    private final Runnable action;

    private ButtonItem(Component label, Runnable action)
    {
        super(label);
        this.action = action;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick)
    {
        this.action.run();
        this.getPopupMenu().deepClose();
    }

    public static MenuItem create(Component label, Runnable clickHandler)
    {
        return new ButtonItem(label, clickHandler);
    }
}
