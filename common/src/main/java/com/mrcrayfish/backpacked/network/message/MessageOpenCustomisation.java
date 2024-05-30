package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class MessageOpenCustomisation extends PlayMessage<MessageOpenCustomisation>
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
    public void handle(MessageOpenCustomisation message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleOpenCustomisation(message));
        context.setHandled(true);
    }

    public Map<ResourceLocation, Component> progressMap()
    {
        return this.map;
    }
}
