package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.*;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.SeedflowAugment;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SeedflowMenu extends AugmentSettingsMenu
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
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget optionsTitle = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider1 = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, optionsTitle.getWidth())).colour(0xFFE0CDB7));
            optionsTitle.setWidth(divider1.getWidth());

            FrameworkButton randomizeBtn = BackpackButtons.onOff(() -> {
                    return holder.get().randomizeSeeds();
                }, newValue -> {
                    holder.update(holder.get().setRandomizeSeeds(newValue));
                })
                .setSize(60, 18).build();
            layout.addChild(createOption(RANDOMISE_SEEDS_LABEL, RANDOMISE_SEEDS_TOOLTIP, randomizeBtn, divider1.getWidth()));

            FrameworkButton useFiltersBtn = BackpackButtons.onOff(() -> {
                    return holder.get().useFilters();
                }, newValue -> {
                    holder.update(holder.get().setUseFilters(newValue));
                })
                .setSize(60, 18).build();
            layout.addChild(createOption(USE_FILTERS_LABEL, USE_FILTERS_TOOLTIP, useFiltersBtn, divider1.getWidth()));

            layout.addChild(Divider.horizontal(MIN_CONTENT_WIDTH).colour(0xFFE0CDB7));

            ItemGrid<SeedflowAugment> list = ItemGrid.builder(holder::get, holder::update)
                .setWidth(divider1.getWidth())
                .setHeight(64)
                .setInitialQuery(lastQuery)
                .setPredicate(SeedflowAugment.ITEM_PLACES_AGEABLE_CROP)
                .build();
            list.setActive(() -> holder.get().useFilters());

            layout.addChild(FrameworkEditBox.builder()
                .setSize(divider1.getWidth(), 16)
                .setPadding(2, 0, 2, 0)
                .setIcon(Utils.rl("backpack/editbox/search"), 12, 12)
                .setInitialText(lastQuery)
                .setHint(SEARCH_HINT)
                .setDependent(() -> holder.get().useFilters())
                .setCallback(s -> {
                    list.setSearchQuery(s);
                    lastQuery = s;
                })
                .setBackground(new WidgetSprites(
                        Utils.rl("backpack/editbox/background"),
                        Utils.rl("backpack/editbox/background_disabled"),
                        Utils.rl("backpack/editbox/background_focused")
                )).build());

            layout.addChild(list);
            return layout;
        });
    }

}
