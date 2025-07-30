package com.mrcrayfish.backpacked.inventory.container.data;

import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.framework.api.menu.IMenuData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ManagementContainerData(UnlockedSlots slots) implements IMenuData<ManagementContainerData>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, ManagementContainerData> STREAM_CODEC = StreamCodec.composite(
        UnlockedSlots.STREAM_CODEC, ManagementContainerData::slots,
        ManagementContainerData::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ManagementContainerData> codec()
    {
        return STREAM_CODEC;
    }
}
