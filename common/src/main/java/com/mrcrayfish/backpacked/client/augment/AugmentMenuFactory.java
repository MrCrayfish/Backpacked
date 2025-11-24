package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AugmentMenuFactory<T>
{
    PopupMenu apply(PopupMenuHandler handler, Supplier<T> supplier, Consumer<T> consumer);
}
