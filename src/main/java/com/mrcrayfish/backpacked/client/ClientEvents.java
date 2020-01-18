package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.network.PacketHandler;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        if(minecraft.player != null && minecraft.currentScreen == null)
        {
            EntityPlayerSP player = minecraft.player;
            if(ClientProxy.KEY_BACKPACK.isPressed())
            {
                if(!Backpacked.getBackpackStack(player).isEmpty())
                {
                    PacketHandler.INSTANCE.sendToServer(new MessageOpenBackpack());
                    minecraft.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.75F, 1.0F));
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyPress(GuiScreenEvent.KeyboardInputEvent event)
    {
        if(event.getGui() instanceof BackpackScreen)
        {
            Minecraft minecraft = event.getGui().mc;
            if(Keyboard.getEventKeyState() && Keyboard.getEventKey() == ClientProxy.KEY_BACKPACK.getKeyCode())
            {
                minecraft.player.closeScreen();
            }
        }
    }
}
