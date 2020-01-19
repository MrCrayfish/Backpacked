package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.proxy.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageBackpackWindow implements IMessage, IMessageHandler<MessageBackpackWindow, IMessage>
{
    private int windowId;
    private int rows;

    public MessageBackpackWindow() {}

    public MessageBackpackWindow(int windowId, int rows)
    {
        this.windowId = windowId;
        this.rows = rows;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.windowId);
        buf.writeInt(this.rows);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.windowId = buf.readInt();
        this.rows = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageBackpackWindow message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            ClientProxy.openBackpackWindow(message.windowId, message.rows);
        });
        return null;
    }
}
