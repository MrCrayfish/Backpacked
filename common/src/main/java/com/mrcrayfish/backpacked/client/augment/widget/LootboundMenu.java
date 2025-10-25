package com.mrcrayfish.backpacked.client.augment.widget;

import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.LootboundAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootboundMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component BLOCKS_LABEL = Component.translatable("augment.backpacked.lootbound.blocks");
    private static final Component MOBS_LABEL = Component.translatable("augment.backpacked.lootbound.mobs");

    private static final int MIN_CONTENT_WIDTH = 70;

    public LootboundMenu(PopupMenuHandler handler, Supplier<LootboundAugment> supplier, Consumer<LootboundAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            layout.addChild(CustomButton.state(() -> supplier.get().blocks(), value -> updater.accept(supplier.get().setBlocks(value)))
                .setSize(divider.getWidth(), 18)
                .setMessage(BLOCKS_LABEL)
                .setContentRenderer(CustomButton.ToggleContentRenderer.INSTANCE)
                .build());
            layout.addChild(CustomButton.state(() -> supplier.get().mobs(), value -> updater.accept(supplier.get().setMobs(value)))
                .setSize(divider.getWidth(), 18)
                .setMessage(MOBS_LABEL)
                .setContentRenderer(CustomButton.ToggleContentRenderer.INSTANCE)
                .build());
            return layout;
        });
    }
}
