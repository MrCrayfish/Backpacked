package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.DataResult;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.inventory.ManagementInventory;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CustomDataSerializers
{
    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<>(ItemStack.OPTIONAL_STREAM_CODEC, ItemStack::saveOptional, (tag, provider) -> {
        if(tag instanceof CompoundTag) {
            return ItemStack.parseOptional(provider, (CompoundTag) tag);
        }
        return ItemStack.EMPTY;
    });
    public static final DataSerializer<Optional<BackpackProperties>> OPTIONAL_BACKPACK_PROPERTIES = new DataSerializer<>(BackpackProperties.STREAM_CODEC.apply(ByteBufCodecs::optional), (properties, provider) -> new CompoundTag(), (tag, provider) -> Optional.empty());
    public static final DataSerializer<NonNullList<ItemStack>> BACKPACKS = new DataSerializer<>(ByteBufCodecs.collection(NonNullList::createWithCapacity, ItemStack.OPTIONAL_STREAM_CODEC, 9), (items, provider) -> {
        DataResult<Tag> result = ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(ManagementInventory.getMaxEquipable()).encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), items);
        return result.result().orElse(null);
    }, (tag, provider) -> {
        DataResult<NonNullList<ItemStack>> result = ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(ManagementInventory.getMaxEquipable())
                .map(list -> {
                    NonNullList<ItemStack> items = NonNullList.create();
                    items.addAll(list);
                    return items;
                })
                .parse(provider.createSerializationContext(NbtOps.INSTANCE), tag);
        return result.result().orElse(NonNullList.withSize(ManagementInventory.getMaxEquipable(), ItemStack.EMPTY));
    });
}
