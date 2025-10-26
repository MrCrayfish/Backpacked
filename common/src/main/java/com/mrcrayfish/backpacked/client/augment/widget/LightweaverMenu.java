package com.mrcrayfish.backpacked.client.augment.widget;

import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Stepper;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.LightweaverAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LightweaverMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component LIGHT_LEVEL_LABEL = Component.translatable("augment.backpacked.lightweaver.light_level");

    private static final int MIN_CONTENT_WIDTH = 130;

    public LightweaverMenu(PopupMenuHandler handler, Supplier<LightweaverAugment> supplier, Consumer<LightweaverAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            layout.addChild(createOption(LIGHT_LEVEL_LABEL, Stepper.builder()
                .setSize(60, 18)
                .setInitialValue(supplier.get().minimumLight())
                .setMin(0)
                .setMax(15)
                .setOnChange(newValue -> {
                    updater.accept(supplier.get().setMinimumLight(newValue));
                }).build(), divider.getWidth()));
            layout.addChild(createOption(Component.literal("Place Sound"), CustomButton.state(() -> {
                    return supplier.get().sound();
                }, newValue -> {
                    updater.accept(supplier.get().setSound(newValue));
                })
                .setMessage(() -> CommonComponents.optionStatus(supplier.get().sound()))
                .setSize(60, 18)
                .build(), divider.getWidth()));
            return layout;
        });
    }
}
