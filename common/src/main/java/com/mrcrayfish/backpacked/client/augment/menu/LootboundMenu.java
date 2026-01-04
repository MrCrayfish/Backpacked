package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.LootboundAugment;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public class LootboundMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component BLOCKS_LABEL = Component.translatable("augment.backpacked.lootbound.blocks");
    private static final Component BLOCKS_TOOLTIP = Component.translatable("augment.backpacked.lootbound.blocks.tooltip");
    private static final Component MOBS_LABEL = Component.translatable("augment.backpacked.lootbound.mobs");
    private static final Component MOBS_TOOLTIP = Component.translatable("augment.backpacked.lootbound.mobs.tooltip");

    private static final int MIN_CONTENT_WIDTH = 110;

    public LootboundMenu(PopupMenuHandler handler, AugmentHolder<LootboundAugment> holder)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            layout.addChild(createOption(BLOCKS_LABEL, BLOCKS_TOOLTIP, BackpackButtons.onOff(() -> holder.get().blocks(), value -> holder.update(holder.get().setBlocks(value))).setSize(60, 18).build(), divider.getWidth()));
            layout.addChild(createOption(MOBS_LABEL, MOBS_TOOLTIP, BackpackButtons.onOff(() -> holder.get().mobs(), value -> holder.update(holder.get().setMobs(value))).setSize(60, 18).build(), divider.getWidth()));
            return layout;
        });
    }
}
