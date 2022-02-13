package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageEntityBackpack implements IMessage<MessageEntityBackpack>
{
    private int entityId;

    public MessageEntityBackpack() {}

    public MessageEntityBackpack(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void encode(MessageEntityBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
    }

    @Override
    public MessageEntityBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageEntityBackpack(buffer.readInt());
    }

    @Override
    public void handle(MessageEntityBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ServerPlayHandler.handleEntityBackpack(message, supplier.get().getSender()));
        supplier.get().setPacketHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }
}
