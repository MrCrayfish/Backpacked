package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.Alignment;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
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

public class AugmentPopupMenu extends PopupMenu
{
    private static final int MAX_COLUMNS = 5;

    private final PaddedLinearLayout layout = (PaddedLinearLayout) new PaddedLinearLayout(LinearLayout.Orientation.VERTICAL).padding(6).spacing(2);

    public AugmentPopupMenu(PopupMenuHandler handler, Augment<?> selected, Consumer<Augment<?>> updater)
    {
        super(handler);
        this.setAlignment(Alignment.END_TOP);
        this.setBackground(Utils.rl("backpack/label"));
        GridLayout grid = new GridLayout().rowSpacing(2).columnSpacing(2);
        AtomicInteger count = new AtomicInteger();
        AugmentType.stream().sorted().forEach(type -> {
            CustomButton augmentBtn = CustomButton.builder()
                .setIcon(type.sprite(), 12, 12)
                .setAction(btn -> {
                    updater.accept(type.defaultSupplier().get());
                    this.deepClose();
                }).build();
            augmentBtn.setTooltip(Tooltip.create(type.name()));
            if(type == selected.type())
                augmentBtn.active = false;
            int index = count.getAndIncrement();
            grid.addChild(augmentBtn, index / MAX_COLUMNS, index % MAX_COLUMNS);
        });
        grid.arrangeElements(); // Do this so the divider can match the width
        TextWidget title = this.layout.addChild(new TextWidget(Component.literal("Augments"), Minecraft.getInstance().font).setColour(0xFF61503D), layoutSettings -> layoutSettings.paddingHorizontal(10));
        this.layout.addChild(Divider.horizontal(Math.max(grid.getWidth(), 10 + title.getWidth() + 10)).colour(0xFFE0CDB7));
        this.layout.addChild(grid, LayoutSettings::alignHorizontallyLeft);
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }

    @Override
    protected int padding()
    {
        return this.layout.padding();
    }
}
