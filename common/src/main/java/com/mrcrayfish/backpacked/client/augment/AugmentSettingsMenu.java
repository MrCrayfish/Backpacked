package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.client.TextureDefinitions;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TextWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class AugmentSettingsMenu extends PopupMenu
{
    private final FrameLayout layout;

    public AugmentSettingsMenu(PopupMenuHandler handler, Function<PopupMenu, Layout> layoutSupplier)
    {
        super(handler);
        this.setBackground(TextureDefinitions.AUGMENT_MENU_BACKGROUND);
        this.setScreenClampPadding(10);
        FrameLayout frameLayout = new FrameLayout();
        frameLayout.defaultChildLayoutSetting().padding(8);
        frameLayout.addChild(layoutSupplier.apply(this));
        this.layout = frameLayout;
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }

    protected static Layout createOption(Component label, Component tooltip, AbstractWidget widget, int width)
    {
        GridLayout optionLayout = new GridLayout().spacing(5);
        GridLayout.RowHelper optionHelper = optionLayout.createRowHelper(2);
        TextWidget text = new TextWidget(label, Minecraft.getInstance().font);
        text.setWidth(width - widget.getWidth() - 5);
        text.setColour(0xFF61503D);
        text.setTooltip(Tooltip.create(tooltip));
        text.setTooltipDelay(50);
        optionHelper.addChild(text, LayoutSettings.defaults().alignVerticallyMiddle());
        optionHelper.addChild(widget);
        return optionLayout;
    }
}
