package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.DataResult;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
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
    public static final DataSerializer<Optional<CosmeticProperties>> OPTIONAL_COSMETIC_PROPERTIES = new DataSerializer<>(CosmeticProperties.STREAM_CODEC.apply(ByteBufCodecs::optional), (properties, provider) -> new CompoundTag(), (tag, provider) -> Optional.empty());

    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<>(ItemStack.OPTIONAL_STREAM_CODEC, ItemStack::saveOptional, (tag, provider) -> {
        return ItemStack.OPTIONAL_CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(ItemStack.EMPTY);
    });

    public static final DataSerializer<NonNullList<ItemStack>> BACKPACKS = new DataSerializer<>(ByteBufCodecs.collection(NonNullList::createWithCapacity, ItemStack.OPTIONAL_STREAM_CODEC, Config.MAX_EQUIPPABLE_BACKPACKS), (items, provider) -> {
        DataResult<Tag> result = ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(Config.MAX_EQUIPPABLE_BACKPACKS).encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), items);
        return result.resultOrPartial(Constants.LOG::error).orElse(null);
    }, (tag, provider) -> {
        DataResult<NonNullList<ItemStack>> result = BackpackedCodecs.BACKPACK_LIST.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag);
        return result.resultOrPartial(Constants.LOG::error).orElse(NonNullList.withSize(Config.MAX_EQUIPPABLE_BACKPACKS, ItemStack.EMPTY));
    });
}
