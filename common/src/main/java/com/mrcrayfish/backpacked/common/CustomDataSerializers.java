package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CustomDataSerializers
{
    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<>(ItemStack.OPTIONAL_STREAM_CODEC, ItemStack::saveOptional, (tag, provider) -> ItemStack.parse(provider, tag).orElse(ItemStack.EMPTY));
    public static final DataSerializer<Optional<BackpackProperties>> OPTIONAL_BACKPACK_PROPERTIES = new DataSerializer<>(BackpackProperties.STREAM_CODEC.apply(ByteBufCodecs::optional), (properties, provider) -> new CompoundTag(), (tag, provider) -> Optional.empty());
}
