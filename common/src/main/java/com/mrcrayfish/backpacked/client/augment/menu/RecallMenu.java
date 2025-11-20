package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TextWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.ShelfKey;
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
            layout.addChild(new TextWidget(() -> {
                return Component.literal(supplier.get().shelfKey().map(shelfKey -> shelfKey.id().toString()).orElse("Not Set"));
            }, Minecraft.getInstance().font)).setWidth(200);
            layout.addChild(CustomButton.builder().setSize(20, 20).setAction(btn -> {
                Minecraft mc = Minecraft.getInstance();
                if(mc.level != null && mc.player != null && mc.hitResult instanceof BlockHitResult result) {
                    BlockPos pos = result.getBlockPos();
                    // This is checked server side too
                    if(pos.distToCenterSqr(mc.player.position()) > RecallAugment.UPDATE_SHELF_RANGE_SQR)
                        return;
                    if(mc.level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf) {
                        ResourceKey<Level> key = mc.level.dimension();
                        updater.accept(supplier.get().setShelf(new ShelfKey(key, shelf.id())));
                    }
                }
            }).build());
            return layout;
        });
    }
}
