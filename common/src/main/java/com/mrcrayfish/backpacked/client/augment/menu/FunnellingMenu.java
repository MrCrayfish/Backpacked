package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.LabelAndDescription;
import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.*;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.FunnellingAugment;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.function.Predicate;

public class FunnellingMenu extends AugmentSettingsMenu
{
    private static final Component SEARCH_HINT = Component.translatable("backpacked.gui.search_hint");
    private static final Component ACTIVE_LABEL = Component.translatable("backpacked.gui.active");
    private static final Component FILTERS_LABEL = Component.translatable("backpacked.gui.filters");
    private static final Component SHOW_ALL_LABEL = Component.translatable("backpacked.gui.show_all");
    private static final Component ACTIVATED_ONLY_LABEL = Component.translatable("backpacked.gui.activated_only");

    private static final Predicate<Item> REMOVE_BLOCKS_WITHOUT_LOOT_TABLE = item -> {
        // This removes most creative/operator blocks
        if(item instanceof BlockItem blockItem) {
            var key = blockItem.getBlock().getLootTable();
            return key != BuiltInLootTables.EMPTY;
        }
        return true;
    };

    private static final int MIN_CONTENT_WIDTH = 162;

    private static String lastQuery = "";
    private static boolean selectedOnly = false;

    public FunnellingMenu(PopupMenuHandler handler, AugmentHolder<FunnellingAugment> holder)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(() -> {
                int filterCount = holder.get().filters().ids().size();
                int maxFilters = Config.AUGMENTS.funnelling.maxFilters.get();
                Component amount = Component.translatable("backpacked.gui.x_of_y", filterCount, maxFilters);
                return ScreenUtil.join(" ", FILTERS_LABEL, amount);
            }, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());

            ItemGrid<FunnellingAugment> grid = ItemGrid.builder(holder::get, holder::update)
                .setWidth(divider.getWidth())
                .setHeight(84)
                .setInitialQuery(lastQuery)
                .setPredicate(REMOVE_BLOCKS_WITHOUT_LOOT_TABLE)
                .build();
            grid.setSelectedOnly(selectedOnly);

            int filterButtonWidth = 55;
            LinearLayout header = LinearLayout.horizontal().spacing(3);
            FrameworkEditBox searchField = FrameworkEditBox.builder()
                .setWidth(divider.getWidth() - 3 - filterButtonWidth)
                .setPadding(2, 0, 2, 0)
                .setHeight(16)
                .setIcon(Utils.rl("backpack/editbox/search"), 12, 12)
                .setInitialText(lastQuery)
                .setHint(SEARCH_HINT)
                .setCallback(s -> {
                    grid.setSearchQuery(s);
                    lastQuery = s;
                })
                .setBackground(new WidgetSprites(
                    Utils.rl("backpack/editbox/background"),
                    Utils.rl("backpack/editbox/background_focused")
                )).build();
            header.addChild(searchField, LayoutSettings::alignVerticallyMiddle);
            header.addChild(Buttons.createToggle(() -> selectedOnly, newValue -> selectedOnly = newValue, grid::setSelectedOnly)
                .setSize(filterButtonWidth, 18)
                .setSpacing(2)
                .setLabel(ACTIVE_LABEL)
                .setTooltip(btn -> Tooltip.create(selectedOnly ? ACTIVATED_ONLY_LABEL : SHOW_ALL_LABEL))
                .setTexture(new WidgetSprites(
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled"),
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled_focused")
                )).build());
            layout.addChild(header);

            layout.addChild(grid);
            layout.addChild(BackpackButtons.values(() -> holder.get().mode(), mode -> holder.update(holder.get().setMode(mode)), mode -> {}).setSize(divider.getWidth(), 18).build());
            return layout;
        });
    }
}
