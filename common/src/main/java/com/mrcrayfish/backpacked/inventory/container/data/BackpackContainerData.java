package com.mrcrayfish.backpacked.inventory.container.data;

import com.mrcrayfish.backpacked.common.Pagination;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.framework.api.menu.IMenuData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Author: MrCrayfish
 */
public record BackpackContainerData(int backpackIndex, int columns, int rows, boolean owner, UnlockableSlots slots, Pagination pagination, Augments augments, UnlockableSlots bays) implements IMenuData<BackpackContainerData>
{
    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackContainerData> STREAM_CODEC = StreamCodec.of((buf, data) -> {
        ByteBufCodecs.INT.encode(buf, data.backpackIndex);
        ByteBufCodecs.INT.encode(buf, data.columns);
        ByteBufCodecs.INT.encode(buf, data.rows);
        ByteBufCodecs.BOOL.encode(buf, data.owner);
        UnlockableSlots.STREAM_CODEC.encode(buf, data.slots);
        Pagination.STREAM_CODEC.encode(buf, data.pagination);
        Augments.STREAM_CODEC.encode(buf, data.augments);
        UnlockableSlots.STREAM_CODEC.encode(buf, data.bays);
    }, buf -> {
        int backpackIndex = buf.readInt();
        int columns = buf.readInt();
        int rows = buf.readInt();
        boolean owner = buf.readBoolean();
        UnlockableSlots slots = UnlockableSlots.STREAM_CODEC.decode(buf);
        Pagination pagination = Pagination.STREAM_CODEC.decode(buf);
        Augments augments = Augments.STREAM_CODEC.decode(buf);
        UnlockableSlots bays = UnlockableSlots.STREAM_CODEC.decode(buf);
        return new BackpackContainerData(backpackIndex, columns, rows, owner, slots, pagination, augments, bays);
    });

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BackpackContainerData> codec()
    {
        return STREAM_CODEC;
    }
}
