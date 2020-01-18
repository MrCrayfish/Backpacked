package com.mrcrayfish.backpacked.network;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.network.message.MessageBackpackWindow;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Author: MrCrayfish
 */
public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
    private static int messageId = 0;

    public static void init()
    {
        registerMessage(MessageOpenBackpack.class, MessageOpenBackpack.class, Side.SERVER);
        registerMessage(MessageUpdateBackpack.class, MessageUpdateBackpack.class, Side.CLIENT);
        registerMessage(MessageBackpackWindow.class, MessageBackpackWindow.class, Side.CLIENT);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
    {
        INSTANCE.registerMessage(messageHandler, requestMessageType, messageId++, side);
    }
}
