package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.TextureDefinitions;
import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.ItemGrid;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.SeedflowAugment;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import com.mrcrayfish.framework.api.client.screen.widget.texture.WidgetTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public class SeedflowMenu extends AugmentSettingsMenu // TODO DONE
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component SEARCH_HINT = Component.translatable("backpacked.gui.search_hint");
    private static final Component RANDOMISE_SEEDS_LABEL = Component.translatable("augment.backpacked.seedflow.randomize_seeds");
    private static final Component RANDOMISE_SEEDS_TOOLTIP = Component.translatable("augment.backpacked.seedflow.randomize_seeds.tooltip");
    private static final Component USE_FILTERS_LABEL = Component.translatable("augment.backpacked.seedflow.use_filters");
    private static final Component USE_FILTERS_TOOLTIP = Component.translatable("augment.backpacked.seedflow.use_filters.tooltip");

    private static final int MIN_CONTENT_WIDTH = 162;

    private static String lastQuery = "";

    public SeedflowMenu(PopupMenuHandler handler, AugmentHolder<SeedflowAugment> holder)
    {
        super(handler, menu -> {
            GridLayout root = new GridLayout().rowSpacing(2);
            GridLayout.RowHelper rootHelper = root.createRowHelper(1);
            TitleWidget optionsTitle = rootHelper.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider1 = rootHelper.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, optionsTitle.getWidth())).colour(0xFFE0CDB7));
            optionsTitle.setWidth(divider1.getWidth());

            FrameworkButton randomizeBtn = BackpackButtons.onOff(() -> {
                    return holder.get().randomizeSeeds();
                }, newValue -> {
                    holder.update(holder.get().setRandomizeSeeds(newValue));
                })
                .setSize(60, 18).build();
            rootHelper.addChild(createOption(RANDOMISE_SEEDS_LABEL, RANDOMISE_SEEDS_TOOLTIP, randomizeBtn, divider1.getWidth()));

            FrameworkButton useFiltersBtn = BackpackButtons.onOff(() -> {
                    return holder.get().useFilters();
                }, newValue -> {
                    holder.update(holder.get().setUseFilters(newValue));
                })
                .setSize(60, 18).build();
            rootHelper.addChild(createOption(USE_FILTERS_LABEL, USE_FILTERS_TOOLTIP, useFiltersBtn, divider1.getWidth()));

            rootHelper.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));

            ItemGrid<SeedflowAugment> list = ItemGrid.builder(holder::get, holder::update)
                .setWidth(divider1.getWidth())
                .setHeight(64)
                .setInitialQuery(lastQuery)
                .setPredicate(SeedflowAugment.ITEM_PLACES_AGEABLE_CROP)
                .build();
            list.setActive(() -> holder.get().useFilters());

            rootHelper.addChild(FrameworkEditBox.builder()
                .setSize(divider1.getWidth(), 16)
                .setPadding(2, 0, 2, 0)
                .setIcon(TextureDefinitions.SEARCH_ICON)
                .setInitialText(lastQuery)
                .setHint(SEARCH_HINT)
                .setDependent(() -> holder.get().useFilters())
                .setCallback(s -> {
                    list.setSearchQuery(s);
                    lastQuery = s;
                })
                .setBackground(new WidgetTextures(
                    TextureDefinitions.EDIT_BOX_ENABLED,
                    TextureDefinitions.EDIT_BOX_DISABLED,
                    TextureDefinitions.EDIT_BOX_ENABLED_HOVERED
                )).build());

            rootHelper.addChild(list);
            return root;
        });
    }

}
