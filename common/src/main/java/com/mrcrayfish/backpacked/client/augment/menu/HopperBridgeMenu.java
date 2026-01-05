package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.LabelAndDescription;
import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.*;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.HopperBridgeAugment;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HopperBridgeMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component FILTERS_LABEL = Component.translatable("backpacked.gui.filters");
    private static final Component SEARCH_HINT = Component.translatable("backpacked.gui.search_hint");
    private static final Component INSERT_LABEL = Component.translatable("augment.backpacked.hopper_bridge.insert");
    private static final Component INSERT_TOOLTIP = Component.translatable("augment.backpacked.hopper_bridge.insert.tooltip");
    private static final Component EXTRACT_LABEL = Component.translatable("augment.backpacked.hopper_bridge.extract");
    private static final Component EXTRACT_TOOLTIP = Component.translatable("augment.backpacked.hopper_bridge.extract.tooltip");
    private static final Component SHOW_ALL_LABEL = Component.translatable("backpacked.gui.show_all");
    private static final Component SELECTED_ONLY_LABEL = Component.translatable("backpacked.gui.selected_only");
    private static final Component FILTER_MODE_LABEL = Component.translatable("augment.backpacked.hopper_bridge.filter_mode");
    private static final Component FILTER_MODE_TOOLTIP = Component.translatable("augment.backpacked.hopper_bridge.filter_mode.tooltip");

    private static final ResourceLocation TOGGLE_OFF = Utils.rl("backpack/toggle_off");
    private static final ResourceLocation TOGGLE_ON = Utils.rl("backpack/toggle_on");

    private static final int MIN_CONTENT_WIDTH = 162;
    private static String lastQuery = "";
    private static boolean selectedOnly = false;

    public HopperBridgeMenu(PopupMenuHandler handler, AugmentHolder<HopperBridgeAugment> holder)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());

            layout.addChild(createOption(INSERT_LABEL, INSERT_TOOLTIP, BackpackButtons.onOff(() -> holder.get().insert(), value -> holder.update(holder.get().setInsert(value))).setSize(60, 18).build(), divider.getWidth()));
            layout.addChild(createOption(EXTRACT_LABEL, EXTRACT_TOOLTIP, BackpackButtons.onOff(() -> holder.get().extract(), value -> holder.update(holder.get().setExtract(value))).setSize(60, 18).build(), divider.getWidth()));

            layout.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));
            layout.addChild(new TitleWidget(FILTERS_LABEL, Minecraft.getInstance().font)).setWidth(MIN_CONTENT_WIDTH);
            layout.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));

            layout.addChild(createOption(FILTER_MODE_LABEL, FILTER_MODE_TOOLTIP, BackpackButtons.values(() -> holder.get().filterMode(), value -> holder.update(holder.get().setFilterMode(value)), filterMode -> {}).setSize(60, 18).build(), divider.getWidth()));

            ItemGrid<HopperBridgeAugment> list = ItemGrid.builder(holder::get, holder::update)
                .setWidth(divider.getWidth())
                .setHeight(64)
                .build();
            list.setActive(() -> holder.get().filterMode() != HopperBridgeAugment.FilterMode.OFF);
            LinearLayout header = LinearLayout.horizontal().spacing(2);

            FrameworkEditBox searchField = FrameworkEditBox.builder()
                .setWidth(divider.getWidth() - 18 - 2)
                .setPadding(2, 0, 2, 0)
                .setHeight(16)
                .setIcon(Utils.rl("backpack/editbox/search"), 12, 12)
                .setInitialText(lastQuery)
                .setHint(SEARCH_HINT)
                .setDependent(() -> holder.get().filterMode() != HopperBridgeAugment.FilterMode.OFF)
                .setCallback(s -> {
                    list.setSearchQuery(s);
                    lastQuery = s;
                })
                .setBackground(new WidgetSprites(
                    Utils.rl("backpack/editbox/background"),
                    Utils.rl("backpack/editbox/background_disabled"),
                    Utils.rl("backpack/editbox/background_focused")
                )).build();
            header.addChild(searchField, LayoutSettings::alignVerticallyMiddle);
            header.addChild(BackpackButtons.toggle(() -> selectedOnly, newValue -> selectedOnly = newValue, list::setSelectedOnly)
                .setSize(18, 18)
                .setIcon(btn -> () -> selectedOnly ? TOGGLE_ON : TOGGLE_OFF, 6, 6)
                .setTooltip(btn -> Tooltip.create(selectedOnly ? SELECTED_ONLY_LABEL : SHOW_ALL_LABEL))
                .setDependent(() -> holder.get().filterMode() != HopperBridgeAugment.FilterMode.OFF)
                .build());
            layout.addChild(header);
            layout.addChild(list);
            return layout;
        });
    }
}
