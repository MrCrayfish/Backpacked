package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record MessageSyncBackpacks(List<Backpack> backpacks)
{
    public static final StreamCodec<FriendlyByteBuf, MessageSyncBackpacks> STREAM_CODEC = StreamCodec.composite(
        Backpack.LIST_STREAM_CODEC, MessageSyncBackpacks::backpacks,
        MessageSyncBackpacks::new
    );

    public static FrameworkResponse handle(MessageSyncBackpacks message, Consumer<Runnable> executor)
    {
        CountDownLatch latch = new CountDownLatch(1);
        executor.accept(() -> {
            BackpackManager.instance().updateClientBackpacks(message.backpacks());
            latch.countDown();
        });
        try
        {
            latch.await();
        }
        catch(InterruptedException e)
        {
            return FrameworkResponse.error("Failed to update backpacks");
        }
        return FrameworkResponse.SUCCESS;
    }
}
