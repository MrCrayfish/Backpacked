package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.LabelAndDescription;
import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.QuiverlinkAugment;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public class QuiverlinkMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component PRIORITY_LABEL = Component.translatable("augment.backpacked.quiverlink.priority");
    private static final Component PRIORITY_TOOLTIP = Component.translatable("augment.backpacked.quiverlink.priority.tooltip");

    private static final int MIN_CONTENT_WIDTH = 120;

    public QuiverlinkMenu(PopupMenuHandler handler, AugmentHolder<QuiverlinkAugment> holder)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            layout.addChild(createOption(
                PRIORITY_LABEL,
                PRIORITY_TOOLTIP,
                BackpackButtons.values(() -> holder.get().priority(), priority -> holder.update(holder.get().setPriority(priority)), priority -> {}).setSize(60, 18).build(),
                divider.getWidth()
            ));
            return layout;
        });
    }
}
