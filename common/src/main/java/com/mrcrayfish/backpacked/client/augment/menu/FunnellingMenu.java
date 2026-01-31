package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.TextureDefinitions;
import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.ItemGrid;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.FunnellingAugment;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import com.mrcrayfish.framework.api.client.screen.widget.texture.WidgetTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;
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
            GridLayout root = new GridLayout().rowSpacing(2);
            GridLayout.RowHelper rootHelper = root.createRowHelper(1);
            TitleWidget title = rootHelper.addChild(new TitleWidget(() -> {
                int filterCount = holder.get().filters().ids().size();
                int maxFilters = Config.AUGMENTS.funnelling.maxFilters.get();
                Component amount = Component.translatable("backpacked.gui.x_of_y", filterCount, maxFilters);
                return ScreenUtil.join(" ", FILTERS_LABEL, amount);
            }, Minecraft.getInstance().font));
            Divider divider = rootHelper.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());

            ItemGrid<FunnellingAugment> grid = ItemGrid.builder(holder::get, holder::update)
                .setWidth(divider.getWidth())
                .setHeight(84)
                .setInitialQuery(lastQuery)
                .setPredicate(REMOVE_BLOCKS_WITHOUT_LOOT_TABLE)
                .build();
            grid.setSelectedOnly(selectedOnly);

            int filterButtonWidth = 55;
            GridLayout header = new GridLayout().spacing(2);
            GridLayout.RowHelper headerHelper = header.createRowHelper(2);
            FrameworkEditBox searchField = FrameworkEditBox.builder()
                .setWidth(divider.getWidth() - 3 - filterButtonWidth)
                .setPadding(2, 0, 2, 0)
                .setHeight(16)
                .setIcon(TextureDefinitions.SEARCH_ICON)
                .setInitialText(lastQuery)
                .setHint(SEARCH_HINT)
                .setCallback(s -> {
                    grid.setSearchQuery(s);
                    lastQuery = s;
                })
                .setBackground(new WidgetTextures(
                    TextureDefinitions.EDIT_BOX_ENABLED,
                    TextureDefinitions.EDIT_BOX_DISABLED,
                    TextureDefinitions.EDIT_BOX_ENABLED_HOVERED
                )).build();
            headerHelper.addChild(searchField, LayoutSettings.defaults().alignVerticallyMiddle());
            headerHelper.addChild(Buttons.createToggle(() -> selectedOnly, newValue -> selectedOnly = newValue, grid::setSelectedOnly)
                .setSize(filterButtonWidth, 18)
                .setSpacing(2)
                .setLabel(ACTIVE_LABEL)
                .setTooltip(btn -> Tooltip.create(selectedOnly ? ACTIVATED_ONLY_LABEL : SHOW_ALL_LABEL))
                .setTexture(new WidgetTextures(
                    TextureDefinitions.BUTTON_ENABLED,
                    TextureDefinitions.BUTTON_ENABLED_HOVERED
                )).build());
            rootHelper.addChild(header);

            rootHelper.addChild(grid);
            rootHelper.addChild(BackpackButtons.values(() -> holder.get().mode(), mode -> holder.update(holder.get().setMode(mode)), mode -> {}).setSize(divider.getWidth(), 18).build());
            return root;
        });
    }
}
