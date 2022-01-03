package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageOpenCustomisation implements IMessage<MessageOpenCustomisation>
{
    private Map<ResourceLocation, Component> map;

    public MessageOpenCustomisation() {}

    public MessageOpenCustomisation(Map<ResourceLocation, Component> map)
    {
        this.map = map;
    }

    @Override
    public void encode(MessageOpenCustomisation message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.map.size());
        message.map.forEach((location, formattedProgress) -> {
            buffer.writeResourceLocation(location);
            buffer.writeComponent(formattedProgress);
        });
    }

    @Override
    public MessageOpenCustomisation decode(FriendlyByteBuf buffer)
    {
        Map<ResourceLocation, Component> map = new HashMap<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++)
        {
            ResourceLocation id = buffer.readResourceLocation();
            Component formattedProgress = buffer.readComponent();
            map.put(id, formattedProgress);
        }
        return new MessageOpenCustomisation(map);
    }

    @Override
    public void handle(MessageOpenCustomisation message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleOpenCustomisation(message));
        supplier.get().setPacketHandled(true);
    }

    public Map<ResourceLocation, Component> getProgressMap()
    {
        return this.map;
    }
}
