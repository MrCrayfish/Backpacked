package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.BackpackConfig;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import com.mrcrayfish.backpacked.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Author: MrCrayfish
 */
public class MessageOpenBackpack implements IMessage, IMessageHandler<MessageOpenBackpack, IMessage>
{
    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(MessageOpenBackpack message, MessageContext ctx)
    {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() ->
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if(player != null)
            {
                if(!Backpacked.getBackpackStack(player).isEmpty())
                {
                    player.getNextWindowId();
                    int rows = BackpackConfig.COMMON.backpackInventorySize;
                    PacketHandler.INSTANCE.sendTo(new MessageBackpackWindow(player.currentWindowId, rows), player);
                    player.openContainer = new BackpackContainer(player.inventory, new BackpackInventory(rows), rows);
                    player.openContainer.windowId = player.currentWindowId;
                    player.openContainer.addListener(player);
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
                }
            }
        });
        return null;
    }
}
