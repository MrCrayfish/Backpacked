package com.mrcrayfish.backpacked.inventory.container.data;

import com.mrcrayfish.framework.api.menu.IMenuData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record BackpackContainerData(int columns, int rows, boolean owner) implements IMenuData<BackpackContainerData>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackContainerData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, BackpackContainerData::columns,
        ByteBufCodecs.INT, BackpackContainerData::rows,
        ByteBufCodecs.BOOL, BackpackContainerData::owner,
        BackpackContainerData::new
    );

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BackpackContainerData> codec()
    {
        return STREAM_CODEC;
    }
}
