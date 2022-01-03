package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageUnlockBackpack implements IMessage<MessageUnlockBackpack>
{
    private ResourceLocation id;

    public MessageUnlockBackpack() {}

    public MessageUnlockBackpack(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public void encode(MessageUnlockBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.id);
    }

    @Override
    public MessageUnlockBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageUnlockBackpack(buffer.readResourceLocation());
    }

    @Override
    public void handle(MessageUnlockBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleUnlockBackpack(message));
        supplier.get().setPacketHandled(true);
    }

    public ResourceLocation getId()
    {
        return this.id;
    }
}
