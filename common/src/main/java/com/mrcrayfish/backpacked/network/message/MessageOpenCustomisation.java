package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public record MessageOpenCustomisation(Map<ResourceLocation, Component> progressMap)
{
    public static void encode(MessageOpenCustomisation message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.progressMap.size());
        message.progressMap.forEach((location, formattedProgress) -> {
            buffer.writeResourceLocation(location);
            buffer.writeComponent(formattedProgress);
        });
    }

    public static MessageOpenCustomisation decode(FriendlyByteBuf buffer)
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

    public static void handle(MessageOpenCustomisation message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleOpenCustomisation(message));
        context.setHandled(true);
    }
}
