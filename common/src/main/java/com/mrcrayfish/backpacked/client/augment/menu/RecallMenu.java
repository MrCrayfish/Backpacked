package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RecallMenu extends AugmentSettingsMenu
{
    public RecallMenu(PopupMenuHandler handler, Supplier<RecallAugment> supplier, Consumer<RecallAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.horizontal().spacing(2);
            layout.addChild(CustomButton.builder().setSize(20, 20).setAction(btn -> {
                Minecraft mc = Minecraft.getInstance();
                if(mc.level != null && mc.hitResult instanceof BlockHitResult result) {
                    BlockPos pos = result.getBlockPos();
                    if(mc.level.getBlockEntity(pos) instanceof ShelfBlockEntity) {
                        ResourceKey<Level> key = mc.level.dimension();
                        updater.accept(supplier.get().setShelfPosition(key, pos));
                        mc.gui.getChat().addMessage(Component.literal("Recall has been saved to " + pos));
                    }
                }
            }).build());
            return layout;
        });
    }
}
