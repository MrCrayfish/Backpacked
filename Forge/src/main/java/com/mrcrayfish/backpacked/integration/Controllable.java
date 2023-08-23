package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.controllable.client.BindingContext;
import com.mrcrayfish.controllable.client.BindingRegistry;
import com.mrcrayfish.controllable.client.ButtonBinding;
import com.mrcrayfish.controllable.event.ControllerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class Controllable
{
    public static final ButtonBinding BACKPACK_BUTTON = new ButtonBinding(-1, "backpacked.button.open_backpack", "button.categories.backpacked", BindingContext.IN_GAME);

    public static void init()
    {
        BindingRegistry.getInstance().register(BACKPACK_BUTTON);
        MinecraftForge.EVENT_BUS.register(Controllable.class);
    }

    @SubscribeEvent
    public static void onButtonInput(ControllerEvent.Button event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen != null)
            return;

        if(BACKPACK_BUTTON.isButtonPressed())
        {
            if(!Services.BACKPACK.getBackpackStack(minecraft.player).isEmpty())
            {
                Network.getPlay().sendToServer(new MessageOpenBackpack());
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER, 0.75F, 1.0F));
            }
        }
    }
}
