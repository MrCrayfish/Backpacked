package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TextWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RecallMenu extends AugmentSettingsMenu
{
    public RecallMenu(PopupMenuHandler handler, Supplier<RecallAugment> supplier, Consumer<RecallAugment> updater)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.horizontal().spacing(2);
            layout.addChild(new TextWidget(() -> {
                return Component.literal(supplier.get().shelfKey().map(shelfKey -> Long.toString(shelfKey.position())).orElse("Not Set"));
            }, Minecraft.getInstance().font)).setWidth(200);
            layout.addChild(CustomButton.builder().setSize(20, 20).setAction(btn -> {
                Minecraft mc = Minecraft.getInstance();
                if(mc.level == null || mc.player == null || !(mc.hitResult instanceof BlockHitResult result))
                    return;
                BlockPos pos = result.getBlockPos();
                // This is checked server side too
                if(pos.distToCenterSqr(mc.player.getEyePosition()) > RecallAugment.UPDATE_SHELF_RANGE_SQR)
                    return;
                // Must be a shelf entity at the block position
                if(!(mc.level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf))
                    return;
                updater.accept(supplier.get().setShelfKey(shelf.key()));
            }).build());
            return layout;
        });
    }
}
