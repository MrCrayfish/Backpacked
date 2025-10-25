package com.mrcrayfish.backpacked.client.augment.widget;

import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TextWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.QuiverlinkAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class QuiverlinkMenu extends AugmentSettingsMenu
{
    public QuiverlinkMenu(PopupMenuHandler handler, Supplier<QuiverlinkAugment> supplier, Consumer<QuiverlinkAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(Component.literal("Options"), Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(120, 10 + title.getWidth() + 10)).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            layout.addChild(CustomButton.values(() -> supplier.get().priority(), priority -> updater.accept(supplier.get().setPriority(priority)))
                .setSize(divider.getWidth(), 18)
                .build()
            );
            return layout;
        });
    }
}
