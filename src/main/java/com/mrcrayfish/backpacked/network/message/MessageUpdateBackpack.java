package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.proxy.ClientProxy;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageUpdateBackpack implements IMessage<MessageUpdateBackpack>
{
    private int entityId;
    private boolean wearing;

    public MessageUpdateBackpack()
    {
    }

    public MessageUpdateBackpack(int entityId, boolean wearing)
    {
        this.entityId = entityId;
        this.wearing = wearing;
    }

    @Override
    public void encode(MessageUpdateBackpack message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeBoolean(message.wearing);
    }

    @Override
    public MessageUpdateBackpack decode(PacketBuffer buffer)
    {
        return new MessageUpdateBackpack(buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public void handle(MessageUpdateBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() ->
        {
            ClientProxy.setPlayerBackpack(message.entityId, message.wearing);
        });
        supplier.get().setPacketHandled(true);
    }
}
