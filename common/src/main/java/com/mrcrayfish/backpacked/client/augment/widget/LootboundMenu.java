package com.mrcrayfish.backpacked.client.augment.widget;

import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TextWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.item.CheckboxItem;
import com.mrcrayfish.backpacked.common.augment.impl.LootboundAugment;
import com.mrcrayfish.backpacked.common.augment.impl.QuiverlinkAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootboundMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component BLOCKS_LABEL = Component.translatable("augment.backpacked.lootbound.blocks");
    private static final Component MOBS_LABEL = Component.translatable("augment.backpacked.lootbound.mobs");

    public LootboundMenu(PopupMenuHandler handler, Supplier<LootboundAugment> supplier, Consumer<LootboundAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TextWidget title = layout.addChild(new TextWidget(OPTIONS_LABEL, Minecraft.getInstance().font).setColour(0xFF61503D));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(70, 10 + title.getWidth() + 10)).colour(0xFFE0CDB7));
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
