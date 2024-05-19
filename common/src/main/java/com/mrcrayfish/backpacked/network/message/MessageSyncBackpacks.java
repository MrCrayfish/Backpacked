package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.framework.api.network.FrameworkResponse;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record MessageSyncBackpacks(List<Backpack> backpacks)
{
    public static void encode(MessageSyncBackpacks message, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(message.backpacks.size());
        message.backpacks.forEach(backpack -> backpack.write(buffer));
    }

    public static MessageSyncBackpacks decode(FriendlyByteBuf buffer)
    {
        List<Backpack> backpacks = new ArrayList<>();
        int size = buffer.readVarInt();
        for(int i = 0; i < size; i++)
        {
            backpacks.add(new Backpack(buffer));
        }
        return new MessageSyncBackpacks(backpacks);
    }

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
