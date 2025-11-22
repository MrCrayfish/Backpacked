package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public record MessageLootboundTakeItem(int entityId, ItemStack stack, Vec3 pos, boolean sound)
{
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageLootboundTakeItem> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, MessageLootboundTakeItem::entityId,
        ItemStack.STREAM_CODEC, MessageLootboundTakeItem::stack,
        ByteBufCodecs.fromCodec(Vec3.CODEC), MessageLootboundTakeItem::pos,
        ByteBufCodecs.BOOL, MessageLootboundTakeItem::sound,
        MessageLootboundTakeItem::new
    );

    public static void handle(MessageLootboundTakeItem message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleLootboundTakeItem(message, context));
        context.setHandled(true);
    }
}
