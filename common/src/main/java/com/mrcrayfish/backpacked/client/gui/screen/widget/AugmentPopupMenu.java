package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.Alignment;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AugmentPopupMenu extends PopupMenu
{
    private static final int MAX_COLUMNS = 6;

    private final PaddedLinearLayout layout = (PaddedLinearLayout) new PaddedLinearLayout(LinearLayout.Orientation.VERTICAL).padding(8).spacing(2);

    public AugmentPopupMenu(PopupMenuHandler handler, Supplier<Augments> selectedAugments, Consumer<Augment<?>> updater)
    {
        super(handler);
        this.setAlignment(Alignment.END_TOP);
        this.setBackground(Utils.rl("augment/menu_background"));
        GridLayout grid = new GridLayout().rowSpacing(2).columnSpacing(2);
        AtomicInteger count = new AtomicInteger();
        AugmentType.stream().sorted().forEach(type -> {
            if(Config.getDisabledAugments().contains(type.id()))
                return;
            FrameworkButton augmentBtn = BackpackButtons.builder()
                .setIcon(type.sprite(), 12, 12)
                .setDependent(() -> type.isEmpty() || !selectedAugments.get().has(type))
                .setAction(btn -> {
                    if(type.isEmpty() || !selectedAugments.get().has(type)) {
                        updater.accept(type.defaultSupplier().get());
                        this.deepClose();
                    }
                })
                .setTooltip(btn -> {
                    List<Component> lines = new ArrayList<>();
                    lines.add(type.name().plainCopy().withStyle(ChatFormatting.BLUE));
                    AugmentType<?> depends = type.requires().get();
                    if(depends != null) {
                        lines.add(Component.translatable("backpacked.gui.requires_augment", depends.name()).withStyle(ChatFormatting.LIGHT_PURPLE));
                    }
                    if(!type.isEmpty()) {
                        String rawDescription = type.description().getString();
                        int firstBreak = rawDescription.indexOf("\n");
                        if(!Screen.hasShiftDown() && firstBreak != -1) {
                            rawDescription = "• " + rawDescription.substring(0, firstBreak);
                        } else {
                            rawDescription = "• " + rawDescription.replace("\n", "\n• ");
                        }
                        lines.add(Component.literal(rawDescription).withStyle(ChatFormatting.GRAY));
                        if(!Screen.hasShiftDown() && firstBreak != -1) {
                            lines.add(BackpackScreen.HOLD_TO_EXPAND.apply(ScreenUtil.getShiftIcon()).withStyle(ChatFormatting.DARK_GRAY));
                        }
                    }
                    if(Minecraft.getInstance().options.advancedItemTooltips)
                        lines.add(Component.literal(type.id().toString()).withStyle(ChatFormatting.DARK_GRAY));
                    return ScreenUtil.createMultilineTooltip(lines);
                }).setTooltipOptions(TooltipOptions.REBUILD_TOOLTIP_ON_SHIFT).build();
            augmentBtn.setTooltip(Tooltip.create(type.name()));
            int index = count.getAndIncrement();
            grid.addChild(augmentBtn, index / MAX_COLUMNS, index % MAX_COLUMNS);
        });
        grid.arrangeElements(); // Do this so the divider can match the width
        TitleWidget title = this.layout.addChild(new TitleWidget(Component.translatable("backpacked.gui.pick_an_augment"), Minecraft.getInstance().font));
        Divider divider = this.layout.addChild(Divider.horizontal(Math.max(grid.getWidth(), title.getWidth())).colour(0xFFE0CDB7));
        title.setWidth(divider.getWidth());
        this.layout.addChild(grid, LayoutSettings::alignHorizontallyLeft);
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }
}
