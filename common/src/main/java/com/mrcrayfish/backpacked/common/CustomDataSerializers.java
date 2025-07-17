package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.DataResult;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class CustomDataSerializers
{
    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<>(ItemStack.OPTIONAL_STREAM_CODEC, ItemStack::saveOptional, (tag, provider) -> ItemStack.parse(provider, tag).orElse(ItemStack.EMPTY));
    public static final DataSerializer<Optional<BackpackProperties>> OPTIONAL_BACKPACK_PROPERTIES = new DataSerializer<>(BackpackProperties.STREAM_CODEC.apply(ByteBufCodecs::optional), (properties, provider) -> new CompoundTag(), (tag, provider) -> Optional.empty());
    public static final DataSerializer<NonNullList<ItemStack>> BACKPACKS = new DataSerializer<>(ByteBufCodecs.collection(NonNullList::createWithCapacity, ItemStack.OPTIONAL_STREAM_CODEC, 1), (items, provider) -> {
        DataResult<Tag> result = ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(1).encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), items);
        return result.result().orElse(null);
    }, (tag, provider) -> {
        DataResult<NonNullList<ItemStack>> result = ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(1)
                .map(list -> {
                    NonNullList<ItemStack> items = NonNullList.create();
                    items.addAll(list);
                    return items;
                })
                .parse(provider.createSerializationContext(NbtOps.INSTANCE), tag);
        return result.result().orElse(NonNullList.withSize(1, ItemStack.EMPTY));
    });
}
