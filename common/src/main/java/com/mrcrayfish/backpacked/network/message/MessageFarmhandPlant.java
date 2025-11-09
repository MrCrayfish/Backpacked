package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record MessageFarmhandPlant(ItemStack stack, int entityId, BlockPos pos)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageFarmhandPlant> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC, MessageFarmhandPlant::stack,
        ByteBufCodecs.INT, MessageFarmhandPlant::entityId,
        BlockPos.STREAM_CODEC, MessageFarmhandPlant::pos,
        MessageFarmhandPlant::new
    );

    public static void handle(MessageFarmhandPlant message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleFarmhandPlant(message, context));
        context.setHandled(true);
    }
}
