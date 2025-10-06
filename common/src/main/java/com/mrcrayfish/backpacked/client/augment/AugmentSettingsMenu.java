package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.gui.layouts.Layout;

import java.util.function.Function;

public class AugmentSettingsMenu extends PopupMenu
{
    private final Layout layout;

    public AugmentSettingsMenu(PopupMenuHandler handler, Function<PopupMenu, Layout> layoutSupplier)
    {
        super(handler);
        this.setBackground(Utils.rl("augment/menu_background"));
        this.layout = layoutSupplier.apply(this);
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }
}
