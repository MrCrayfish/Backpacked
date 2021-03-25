package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.BackpackedButtonBindings;
import com.mrcrayfish.backpacked.network.PacketHandler;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.controllable.event.ControllerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ControllerHandler
{
    @SubscribeEvent
    public void onButtonInput(ControllerEvent.Button event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.currentScreen != null)
            return;

        if(BackpackedButtonBindings.BACKPACK.isButtonPressed())
        {
            if(!Backpacked.getBackpackStack(minecraft.player).isEmpty())
            {
                PacketHandler.instance.sendToServer(new MessageOpenBackpack());
                minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.75F, 1.0F));
            }
        }
    }
}
