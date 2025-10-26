package com.mrcrayfish.backpacked.client.augment.widget;

import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Stepper;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.LightweaverAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LightweaverMenu extends AugmentSettingsMenu
{
    private static final Component LIGHT_LEVEL_LABEL = Component.translatable("augment.backpacked.lightweaver.light_level");

    private static final int MIN_CONTENT_WIDTH = 120;

    public LightweaverMenu(PopupMenuHandler handler, Supplier<LightweaverAugment> supplier, Consumer<LightweaverAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(LIGHT_LEVEL_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            Stepper lightLevelStepper = Stepper.builder()
                .setSize(divider.getWidth(), 16)
                .setInitialValue(supplier.get().minimumLight()) 
                .setMin(0)
                .setMax(15)
                .setOnChange(newValue -> {
                    updater.accept(supplier.get().setMinimumLight(newValue));
                }).build();
            layout.addChild(lightLevelStepper);
            return layout;
        });
    }
}
