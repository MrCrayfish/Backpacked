package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
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
        PaddedLinearLayout layout = PaddedLinearLayout.vertical().padding(8);
        layout.addChild(layoutSupplier.apply(this));
        this.layout = layout;
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }
}
