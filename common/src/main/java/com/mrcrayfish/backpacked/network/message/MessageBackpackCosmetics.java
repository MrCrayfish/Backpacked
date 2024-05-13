package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record MessageBackpackCosmetics(ResourceLocation cosmeticId, boolean showGlint, boolean showElytra, boolean showEffects)
{
    public static void encode(MessageBackpackCosmetics message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.cosmeticId);
        buffer.writeBoolean(message.showGlint);
        buffer.writeBoolean(message.showElytra);
        buffer.writeBoolean(message.showEffects);
    }

    public static MessageBackpackCosmetics decode(FriendlyByteBuf buffer)
    {
        return new MessageBackpackCosmetics(buffer.readResourceLocation(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(MessageBackpackCosmetics message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleCustomiseBackpack(message, context));
        context.setHandled(true);
    }
}
