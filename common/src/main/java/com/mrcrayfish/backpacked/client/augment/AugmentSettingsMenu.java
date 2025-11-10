package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TextWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.util.function.Function;

public class AugmentSettingsMenu extends PopupMenu
{
    private final Layout layout;

    public AugmentSettingsMenu(PopupMenuHandler handler, Function<PopupMenu, Layout> layoutSupplier)
    {
        super(handler);
        this.setBackground(Utils.rl("augment/menu_background"));
        this.setScreenClampPadding(10);
        PaddedLinearLayout layout = PaddedLinearLayout.vertical().padding(8);
        layout.addChild(layoutSupplier.apply(this));
        this.layout = layout;
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }

    protected static Layout createOption(Component label, Component tooltip, AbstractWidget widget, int width)
    {
        LinearLayout option = LinearLayout.horizontal().spacing(5);
        TextWidget text = new TextWidget(label, Minecraft.getInstance().font);
        text.setWidth(width - widget.getWidth() - 5);
        text.setColour(0xFF61503D);
        text.setTooltip(Tooltip.create(tooltip));
        text.setTooltipDelay(Duration.ofMillis(50));
        option.addChild(text, LayoutSettings::alignVerticallyMiddle);
        option.addChild(widget);
        return option;
    }
}
