package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class OnPlacedBackpackListener implements ContainerListener
{
    @Override
    public void slotChanged(AbstractContainerMenu menu, int index, ItemStack stack)
    {
        if(!(menu instanceof BackpackManagementMenu managementMenu) || index != 0)
            return;

        if(!(stack.getItem() instanceof BackpackItem))
            return;

        Player player = managementMenu.getPlayer();
        if(player instanceof ServerPlayer serverPlayer)
        {
            BackpackItem.openBackpack(serverPlayer, serverPlayer);
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu menu, int type, int value) {}
}
