package com.mrcrayfish.backpacked.proxy;

import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.ExtendedPlayerContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Author: MrCrayfish
 */
public class CommonProxy
{
    public void setupClient() {}

    public void patchPlayerInventory(EntityPlayer player)
    {
        this.patchInventory(FMLCommonHandler.instance().getMinecraftServerInstance(), player);
    }

    /* Hack to patch the player inventory. Delayed by scheduling an event */
    void patchInventory(IThreadListener listener, EntityPlayer player)
    {
        listener.addScheduledTask(() ->
        {
            player.inventory = new ExtendedPlayerInventory(player);
            player.inventoryContainer = new ExtendedPlayerContainer(player.inventory, !player.world.isRemote, player);
            player.openContainer = player.inventoryContainer;
        });
    }
}
