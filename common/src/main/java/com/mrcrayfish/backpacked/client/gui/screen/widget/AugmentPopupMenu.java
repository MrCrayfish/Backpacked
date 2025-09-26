package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.client.gui.screen.layout.BorderedGridLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.Alignment;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AugmentPopupMenu extends PopupMenu
{
    private static final int MAX_COLUMNS = 5;

    private final BorderedGridLayout layout = (BorderedGridLayout) new BorderedGridLayout()
            .border(2).rowSpacing(2).columnSpacing(2);

    public AugmentPopupMenu(PopupMenuHandler handler, Augment<?> selected, Consumer<Augment<?>> updater)
    {
        super(handler);
        this.setAlignment(Alignment.BELOW_LEFT);
        this.setBackground(Utils.rl("backpack/dropdown/background"));
        AtomicInteger count = new AtomicInteger();
        AugmentType.all().stream().sorted(Comparator.comparing(type -> type.name().getString())).forEach(type -> {
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
            this.layout.addChild(augmentBtn, index / MAX_COLUMNS, index % MAX_COLUMNS);
        });
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }

    @Override
    protected int border()
    {
        return this.layout.getBorder();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.defaultButtonNarrationText(output);
    }
}
