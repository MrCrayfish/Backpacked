package com.mrcrayfish.backpacked.inventory.container.data;

import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import net.minecraft.network.FriendlyByteBuf;

public record ManagementContainerData(UnlockableSlots slots, boolean showInventoryButton)
{
    public void encode(FriendlyByteBuf buf)
    {
        this.slots.encode(buf);
        buf.writeBoolean(this.showInventoryButton);
    }

    public static ManagementContainerData decode(FriendlyByteBuf buf)
    {
        UnlockableSlots slots = UnlockableSlots.decode(buf);
        boolean showInventoryButton = buf.readBoolean();
        return new ManagementContainerData(slots, showInventoryButton);
    }
}
