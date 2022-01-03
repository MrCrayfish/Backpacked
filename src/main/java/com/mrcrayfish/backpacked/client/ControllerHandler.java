package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.BackpackedButtonBindings;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.controllable.event.ControllerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
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
        if(minecraft.screen != null)
            return;

        if(BackpackedButtonBindings.BACKPACK.isButtonPressed())
        {
            if(!Backpacked.getBackpackStack(minecraft.player).isEmpty())
            {
                Network.getPlayChannel().sendToServer(new MessageOpenBackpack());
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER, 0.75F, 1.0F));
            }
        }
    }
}
