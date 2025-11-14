package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.augment.widget.ItemGrid;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomEditBox;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.HopperBridgeAugment;
import com.mrcrayfish.backpacked.common.augment.impl.SeedflowAugment;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HopperBridgeMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component SEARCH_HINT = Component.translatable("backpacked.gui.search_hint");
    private static final Component INSERT_LABEL = Component.translatable("augment.backpacked.hopper_bridge.insert");
    private static final Component INSERT_TOOLTIP = Component.translatable("augment.backpacked.hopper_bridge.insert.tooltip");
    private static final Component EXTRACT_LABEL = Component.translatable("augment.backpacked.hopper_bridge.extract");
    private static final Component EXTRACT_TOOLTIP = Component.translatable("augment.backpacked.hopper_bridge.extract.tooltip");
    private static final Component SHOW_ALL_LABEL = Component.translatable("backpacked.gui.show_all");
    private static final Component SELECTED_ONLY_LABEL = Component.translatable("backpacked.gui.selected_only");

    private static final ResourceLocation TOGGLE_OFF = Utils.rl("backpack/toggle_off");
    private static final ResourceLocation TOGGLE_ON = Utils.rl("backpack/toggle_on");

    private static final int MIN_CONTENT_WIDTH = 162;
    private static String lastQuery = "";
    private static boolean selectedOnly = false;

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

            layout.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));

            ItemGrid<HopperBridgeAugment> list = ItemGrid.builder(supplier, updater).setWidth(divider.getWidth()).setHeight(64).build();
            LinearLayout header = LinearLayout.horizontal().spacing(2);
            CustomEditBox searchField = header.addChild(CustomEditBox.create(divider.getWidth() - 18 - 2, 16, Utils.rl("backpack/editbox/search"), new WidgetSprites(
                Utils.rl("backpack/editbox/background"),
                Utils.rl("backpack/editbox/background_disabled"),
                Utils.rl("backpack/editbox/background_focused")
            )), LayoutSettings::alignVerticallyMiddle);
            searchField.getEditBox().setValue(lastQuery);
            searchField.getEditBox().setHint(SEARCH_HINT);
            searchField.getEditBox().setResponder(s -> {
                list.setSearchQuery(s);
                lastQuery = s;
            });
            header.addChild(CustomButton.state(() -> selectedOnly, newValue -> selectedOnly = newValue)
                .setSize(18, 18)
                .setIcon(btn -> selectedOnly ? TOGGLE_ON : TOGGLE_OFF, 6, 6)
                .setTooltip(btn -> Tooltip.create(selectedOnly ? SELECTED_ONLY_LABEL : SHOW_ALL_LABEL))
                .setAction(btn -> list.setSelectedOnly(selectedOnly))
                .build());
            layout.addChild(header);
            layout.addChild(list);
            return layout;
        });
    }
}
