package com.mrcrayfish.backpacked.inventory.container.data;

import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.framework.api.menu.IMenuData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record BackpackContainerData(int columns, int rows, boolean owner, UnlockableSlots slots, int index, int total, Augments augments) implements IMenuData<BackpackContainerData>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackContainerData> STREAM_CODEC = StreamCodec.of((buf, data) -> {
        ByteBufCodecs.INT.encode(buf, data.columns);
        ByteBufCodecs.INT.encode(buf, data.rows);
        ByteBufCodecs.BOOL.encode(buf, data.owner);
        UnlockableSlots.STREAM_CODEC.encode(buf, data.slots);
        ByteBufCodecs.INT.encode(buf, data.index);
        ByteBufCodecs.INT.encode(buf, data.total);
        Augments.STREAM_CODEC.encode(buf, data.augments);
    }, buf -> {
        int columns = buf.readInt();
        int rows = buf.readInt();
        boolean owner = buf.readBoolean();
        UnlockableSlots slots = UnlockableSlots.STREAM_CODEC.decode(buf);
        int index = buf.readInt();
        int total = buf.readInt();
        Augments augments = Augments.STREAM_CODEC.decode(buf);
        return new BackpackContainerData(columns, rows, owner, slots, index, total, augments);
    });

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BackpackContainerData> codec()
    {
        return STREAM_CODEC;
    }
}
