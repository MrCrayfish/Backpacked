package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Stepper;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.LightweaverAugment;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public class LightweaverMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component LIGHT_LEVEL_LABEL = Component.translatable("augment.backpacked.lightweaver.light_level");
    private static final Component PLACE_SOUND_LABEL = Component.translatable("augment.backpacked.lightweaver.place_sound");
    private static final Component LIGHT_LEVEL_TOOLTIP = Component.translatable("augment.backpacked.lightweaver.light_level.tooltip");
    private static final Component PLACE_SOUND_TOOLTIP = Component.translatable("augment.backpacked.lightweaver.place_sound.tooltip");

    private static final int MIN_CONTENT_WIDTH = 130;

    public LightweaverMenu(PopupMenuHandler handler, AugmentHolder<LightweaverAugment> holder)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            layout.addChild(createOption(LIGHT_LEVEL_LABEL, LIGHT_LEVEL_TOOLTIP, Stepper.builder()
                .setSize(60, 18)
                .setInitialValue(holder.get().minimumLight())
                .setRange(0, 15)
                .setWrap(true)
                .setOnChange(newValue -> {
                    holder.update(holder.get().setMinimumLight(newValue));
                }).build(), divider.getWidth()));
            layout.addChild(createOption(PLACE_SOUND_LABEL, PLACE_SOUND_TOOLTIP, BackpackButtons.onOff(() -> {
                    return holder.get().sound();
                }, newValue -> {
                    holder.update(holder.get().setSound(newValue));
                })
                .setSize(60, 18)
                .build(), divider.getWidth()));
            return layout;
        });
    }
}
