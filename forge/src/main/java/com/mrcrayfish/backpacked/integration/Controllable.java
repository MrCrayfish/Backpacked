package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.controllable.client.binding.BindingContext;
import com.mrcrayfish.controllable.client.binding.BindingRegistry;
import com.mrcrayfish.controllable.client.binding.ButtonBinding;
import com.mrcrayfish.controllable.client.input.Controller;
import com.mrcrayfish.controllable.event.ControllerEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.MinecraftForge;

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
        ControllerEvents.BUTTON.register(Controllable::onButtonInput);
    }

    private static boolean onButtonInput(Controller controller)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen != null)
            return false;

        if(BACKPACK_BUTTON.isButtonPressed())
        {
            if(!Services.BACKPACK.getBackpackStack(minecraft.player).isEmpty())
            {
                Network.getPlay().sendToServer(new MessageOpenBackpack());
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER, 0.75F, 1.0F));
            }
        }

        return false;
    }
}
