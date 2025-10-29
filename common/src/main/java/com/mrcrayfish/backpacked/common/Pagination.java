package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageNavigateBackpack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Pagination(int currentPage, int totalPages)
{
    public static final Pagination NONE = new Pagination(0, 0);

    public static final StreamCodec<RegistryFriendlyByteBuf, Pagination> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, Pagination::currentPage,
        ByteBufCodecs.INT, Pagination::totalPages,
        Pagination::new
    );

    public void nextPage()
    {
        Network.getPlay().sendToServer(new MessageNavigateBackpack(Navigate.NEXT));
    }

    public void previousPage()
    {
        Network.getPlay().sendToServer(new MessageNavigateBackpack(Navigate.PREVIOUS));
    }
}
