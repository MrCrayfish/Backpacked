package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageNavigateBackpack;
import net.minecraft.network.FriendlyByteBuf;

public record Pagination(int currentPage, int totalPages) // TODO DONE
{
    public static final Pagination NONE = new Pagination(0, 0);

    public void nextPage()
    {
        Network.getPlay().sendToServer(new MessageNavigateBackpack(Navigate.NEXT));
    }

    public void previousPage()
    {
        Network.getPlay().sendToServer(new MessageNavigateBackpack(Navigate.PREVIOUS));
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.currentPage);
        buf.writeInt(this.totalPages);
    }

    public static Pagination decode(FriendlyByteBuf buf)
    {
        int currentPage = buf.readInt();
        int totalPages = buf.readInt();
        return new Pagination(currentPage, totalPages);
    }
}
