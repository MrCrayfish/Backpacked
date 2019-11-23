package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.entity.player.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.network.PacketHandler;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player != null)
        {
            ClientPlayerEntity player = minecraft.player;
            if(ClientProxy.KEY_BACKPACK.isPressed())
            {
                if(player.inventory instanceof ExtendedPlayerInventory)
                {
                    if(!((ExtendedPlayerInventory) player.inventory).getBackpackItems().get(0).isEmpty())
                    {
                        PacketHandler.instance.sendToServer(new MessageOpenBackpack());
                    }
                }
            }
        }
    }
}
