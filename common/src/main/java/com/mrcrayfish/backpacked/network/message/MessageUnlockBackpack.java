package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record MessageUnlockBackpack(ResourceLocation cosmeticId)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageUnlockBackpack> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, MessageUnlockBackpack::cosmeticId,
        MessageUnlockBackpack::new
    );

    public static void handle(MessageUnlockBackpack message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUnlockBackpack(message));
        context.setHandled(true);
    }
}
