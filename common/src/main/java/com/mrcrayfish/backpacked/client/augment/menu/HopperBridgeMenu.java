package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.TextureDefinitions;
import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.*;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.HopperBridgeAugment;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import com.mrcrayfish.framework.api.client.screen.widget.texture.WidgetTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

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

    private static final int MIN_CONTENT_WIDTH = 162;
    private static String lastQuery = "";
    private static boolean selectedOnly = false;

    public HopperBridgeMenu(PopupMenuHandler handler, AugmentHolder<HopperBridgeAugment> holder)
    {
        super(handler, menu -> {
            GridLayout root = new GridLayout().spacing(2);
            GridLayout.RowHelper rootHelper = root.createRowHelper(1);

            TitleWidget title = rootHelper.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = rootHelper.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());

            rootHelper.addChild(createOption(INSERT_LABEL, INSERT_TOOLTIP, BackpackButtons.onOff(() -> holder.get().insert(), value -> holder.update(holder.get().setInsert(value))).setSize(60, 18).build(), divider.getWidth()));
            rootHelper.addChild(createOption(EXTRACT_LABEL, EXTRACT_TOOLTIP, BackpackButtons.onOff(() -> holder.get().extract(), value -> holder.update(holder.get().setExtract(value))).setSize(60, 18).build(), divider.getWidth()));

            rootHelper.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));
            rootHelper.addChild(new TitleWidget(() -> {
                int filterCount = holder.get().filters().ids().size();
                int maxFilters = Config.AUGMENTS.hopperBridge.maxFilters.get();
                Component amount = Component.translatable("backpacked.gui.x_of_y", filterCount, maxFilters);
                return ScreenUtil.join(" ", FILTERS_LABEL, amount);
            }, Minecraft.getInstance().font)).setWidth(MIN_CONTENT_WIDTH);
            rootHelper.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));

            rootHelper.addChild(createOption(FILTER_MODE_LABEL, FILTER_MODE_TOOLTIP, BackpackButtons.values(() -> holder.get().filterMode(), value -> holder.update(holder.get().setFilterMode(value)), filterMode -> {}).setSize(60, 18).build(), divider.getWidth()));

            ItemGrid<HopperBridgeAugment> list = ItemGrid.builder(holder::get, holder::update)
                .setWidth(divider.getWidth())
                .setHeight(64)
                .build();
            list.setActive(() -> holder.get().filterMode() != HopperBridgeAugment.FilterMode.OFF);

            GridLayout header = new GridLayout().spacing(2);
            GridLayout.RowHelper headerHelper = header.createRowHelper(2);

            FrameworkEditBox searchField = FrameworkEditBox.builder()
                .setWidth(divider.getWidth() - 18 - 2)
                .setPadding(2, 0, 2, 0)
                .setHeight(16)
                .setIcon(TextureDefinitions.SEARCH_ICON)
                .setInitialText(lastQuery)
                .setHint(SEARCH_HINT)
                .setDependent(() -> holder.get().filterMode() != HopperBridgeAugment.FilterMode.OFF)
                .setCallback(s -> {
                    list.setSearchQuery(s);
                    lastQuery = s;
                })
                .setBackground(new WidgetTextures(
                    TextureDefinitions.EDIT_BOX_ENABLED,
                    TextureDefinitions.EDIT_BOX_DISABLED,
                    TextureDefinitions.EDIT_BOX_ENABLED_HOVERED
                )).build();
            headerHelper.addChild(searchField, LayoutSettings.defaults().alignVerticallyMiddle());
            headerHelper.addChild(BackpackButtons.toggle(() -> selectedOnly, newValue -> selectedOnly = newValue, list::setSelectedOnly)
                .setSize(18, 18)
                .setIcon(btn -> DynamicIcon.create(() -> selectedOnly ? TextureDefinitions.TOGGLE_ON : TextureDefinitions.TOGGLE_OFF))
                .setTooltip(btn -> Tooltip.create(selectedOnly ? SELECTED_ONLY_LABEL : SHOW_ALL_LABEL))
                .setDependent(() -> holder.get().filterMode() != HopperBridgeAugment.FilterMode.OFF)
                .build());
            rootHelper.addChild(header);
            rootHelper.addChild(list);
            return root;
        });
    }
}
