package com.mrcrayfish.backpacked.inventory.container.data;

import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.framework.api.menu.IMenuData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ManagementContainerData(UnlockableSlots slots, boolean showInventoryButton) implements IMenuData<ManagementContainerData>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ManagementContainerData> STREAM_CODEC = StreamCodec.composite(
        UnlockableSlots.STREAM_CODEC, ManagementContainerData::slots,
        ByteBufCodecs.BOOL, ManagementContainerData::showInventoryButton,
        ManagementContainerData::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ManagementContainerData> codec()
    {
        return STREAM_CODEC;
    }
}
