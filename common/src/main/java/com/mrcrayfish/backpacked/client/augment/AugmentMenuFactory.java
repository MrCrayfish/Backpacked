package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;

public interface AugmentMenuFactory<T>
{
    PopupMenu apply(PopupMenuHandler handler, AugmentHolder<T> holder);
}
