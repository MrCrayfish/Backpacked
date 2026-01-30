package com.mrcrayfish.backpacked.inventory.container.data;

import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Author: MrCrayfish
 */ // TODO DONE
public record BackpackContainerData(int backpackIndex, int columns, int rows, boolean owner, UnlockableSlots slots, Pagination pagination, Augments augments, UnlockableSlots bays)
{
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.backpackIndex);
        buf.writeInt(this.columns);
        buf.writeInt(this.rows);
        buf.writeBoolean(this.owner);
        this.slots.encode(buf);
        this.pagination.encode(buf);
        this.augments.encode(buf);
        this.bays.encode(buf);
    }

    public static BackpackContainerData decode(FriendlyByteBuf buf)
    {
        int backpackIndex = buf.readInt();
        int columns = buf.readInt();
        int rows = buf.readInt();
        boolean owner = buf.readBoolean();
        UnlockableSlots slots = UnlockableSlots.decode(buf);
        Pagination pagination = Pagination.decode(buf);
        Augments augments = Augments.decode(buf);
        UnlockableSlots bays = UnlockableSlots.decode(buf);
        return new BackpackContainerData(backpackIndex, columns, rows, owner, slots, pagination, augments, bays);
    }
}
