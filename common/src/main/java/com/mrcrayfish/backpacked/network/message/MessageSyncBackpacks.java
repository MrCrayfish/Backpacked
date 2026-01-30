package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.HandshakeMessage;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Author: MrCrayfish
 */
public final class MessageSyncBackpacks extends HandshakeMessage<MessageSyncBackpacks> // TODO DONE
{
    private List<Backpack> backpacks;

    public MessageSyncBackpacks() {}

    public MessageSyncBackpacks(List<Backpack> backpacks)
    {
        this.backpacks = backpacks;
    }

    @Override
    public void encode(MessageSyncBackpacks message, FriendlyByteBuf buffer)
    {
        buffer.writeVarInt(message.backpacks.size());
        message.backpacks.forEach(backpack -> backpack.write(buffer));
    }

    @Override
    public MessageSyncBackpacks decode(FriendlyByteBuf buf)
    {
        List<Backpack> backpacks = new ArrayList<>();
        int size = buf.readVarInt();
        for(int i = 0; i < size; i++)
        {
            backpacks.add(new Backpack(buf));
        }
        return new MessageSyncBackpacks(backpacks);
    }

    @Override
    public void handle(MessageSyncBackpacks message, MessageContext context)
    {
        CountDownLatch latch = new CountDownLatch(1);
        context.execute(() -> {
            ClientRegistry.instance().updateBackpacks(message.backpacks);
            latch.countDown();
        });
        try
        {
            latch.await();
        }
        catch(InterruptedException e)
        {
            Constants.LOG.error("Failed to update backpacks", e);
        }
        context.setHandled(true);
        context.reply(new Acknowledge());
    }
}
