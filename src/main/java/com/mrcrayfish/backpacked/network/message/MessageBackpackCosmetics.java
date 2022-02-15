package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ServerPlayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageBackpackCosmetics implements IMessage<MessageBackpackCosmetics>
{
    private ResourceLocation id;
    private boolean showGlint;
    private boolean showWithElytra;
    private boolean showEffects;

    public MessageBackpackCosmetics() {}

    public MessageBackpackCosmetics(ResourceLocation id, boolean showGlint, boolean showWithElytra, boolean showEffects)
    {
        this.id = id;
        this.showGlint = showGlint;
        this.showWithElytra = showWithElytra;
        this.showEffects = showEffects;
    }

    @Override
    public void encode(MessageBackpackCosmetics message, FriendlyByteBuf buffer)
    {
        buffer.writeResourceLocation(message.id);
        buffer.writeBoolean(message.showGlint);
        buffer.writeBoolean(message.showWithElytra);
        buffer.writeBoolean(message.showEffects);
    }

    @Override
    public MessageBackpackCosmetics decode(FriendlyByteBuf buffer)
    {
        return new MessageBackpackCosmetics(buffer.readResourceLocation(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    @Override
    public void handle(MessageBackpackCosmetics message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ServerPlayHandler.handleCustomiseBackpack(message, supplier.get().getSender()));
        supplier.get().setPacketHandled(true);
    }

    public ResourceLocation getBackpackId()
    {
        return this.id;
    }

    public boolean isShowGlint()
    {
        return this.showGlint;
    }

    public boolean isShowWithElytra()
    {
        return this.showWithElytra;
    }

    public boolean isShowEffects()
    {
        return this.showEffects;
    }
}
