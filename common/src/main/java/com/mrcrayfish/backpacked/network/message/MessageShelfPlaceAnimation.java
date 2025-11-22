package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MessageShelfPlaceAnimation(BlockPos pos)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageShelfPlaceAnimation> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, MessageShelfPlaceAnimation::pos,
        MessageShelfPlaceAnimation::new
    );

    public static void handle(MessageShelfPlaceAnimation message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleMessageShelfPlaceAnimation(message, context));
        context.setHandled(true);
    }
}
