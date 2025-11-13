package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.HopperBridgeAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HopperBridgeMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component INSERT_LABEL = Component.translatable("augment.backpacked.hopper_bridge.insert");
    private static final Component INSERT_TOOLTIP = Component.translatable("augment.backpacked.hopper_bridge.insert.tooltip");
    private static final Component EXTRACT_LABEL = Component.translatable("augment.backpacked.hopper_bridge.extract");
    private static final Component EXTRACT_TOOLTIP = Component.translatable("augment.backpacked.hopper_bridge.extract.tooltip");

    private static final int MIN_CONTENT_WIDTH = 140;

    public HopperBridgeMenu(PopupMenuHandler handler, Supplier<HopperBridgeAugment> supplier, Consumer<HopperBridgeAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            layout.addChild(createOption(INSERT_LABEL, INSERT_TOOLTIP, CustomButton.state(() -> supplier.get().insert(), value -> updater.accept(supplier.get().setInsert(value)))
                .setSize(60, 18)
                .setMessage(() -> CommonComponents.optionStatus(supplier.get().insert()))
                .build(), divider.getWidth()));
            layout.addChild(createOption(EXTRACT_LABEL, EXTRACT_TOOLTIP, CustomButton.state(() -> supplier.get().extract(), value -> updater.accept(supplier.get().setExtract(value)))
                .setSize(60, 18)
                .setMessage(() -> CommonComponents.optionStatus(supplier.get().extract()))
                .build(), divider.getWidth()));
            return layout;
        });
    }
}
