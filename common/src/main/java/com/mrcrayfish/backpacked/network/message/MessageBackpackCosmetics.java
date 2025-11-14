package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record MessageBackpackCosmetics(int backpackIndex, CosmeticProperties properties)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageBackpackCosmetics> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageBackpackCosmetics::backpackIndex,
        CosmeticProperties.STREAM_CODEC, MessageBackpackCosmetics::properties,
        MessageBackpackCosmetics::new
    );

    public static void handle(MessageBackpackCosmetics message, MessageContext context)
    {
        context.execute(() -> ServerPlayHandler.handleCustomiseBackpack(message, context));
        context.setHandled(true);
    }
}
