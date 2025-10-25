package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.Alignment;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AugmentPopupMenu extends PopupMenu
{
    private static final int MAX_COLUMNS = 5;

    private final PaddedLinearLayout layout = (PaddedLinearLayout) new PaddedLinearLayout(LinearLayout.Orientation.VERTICAL).padding(6).spacing(2);

    public AugmentPopupMenu(PopupMenuHandler handler, Supplier<Augments> selectedAugments, Consumer<Augment<?>> updater)
    {
        super(handler);
        this.setAlignment(Alignment.END_TOP);
        this.setBackground(Utils.rl("backpack/label"));
        GridLayout grid = new GridLayout().rowSpacing(2).columnSpacing(2);
        AtomicInteger count = new AtomicInteger();
        AugmentType.stream().sorted().forEach(type -> {
            CustomButton augmentBtn = CustomButton.builder()
                .setIcon(type.sprite(), 12, 12)
                .setActive(() -> type.isEmpty() || !selectedAugments.get().has(type))
                .setAction(btn -> {
                    if(type.isEmpty() || !selectedAugments.get().has(type)) {
                        updater.accept(type.defaultSupplier().get());
                        this.deepClose();
                    }
                }).build();
            augmentBtn.setTooltip(Tooltip.create(type.name()));
            int index = count.getAndIncrement();
            grid.addChild(augmentBtn, index / MAX_COLUMNS, index % MAX_COLUMNS);
        });
        grid.arrangeElements(); // Do this so the divider can match the width
        TitleWidget title = this.layout.addChild(new TitleWidget(Component.literal("Augments"), Minecraft.getInstance().font));
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
