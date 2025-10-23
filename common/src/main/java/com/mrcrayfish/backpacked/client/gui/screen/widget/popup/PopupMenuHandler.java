package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

/**
 * Author: MrCrayfish
 */
public interface PopupMenuHandler
{
    PopupMenuController getPopupMenuController();

    default boolean hasPopupMenu()
    {
        return this.getPopupMenuController().isOpened();
    }
}
