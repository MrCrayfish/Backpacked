package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.framework.api.sync.DataSerializer;
import net.minecraft.world.item.ItemStack;

public class CustomDataSerializers
{
    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<>(ItemStack.OPTIONAL_STREAM_CODEC, ItemStack::saveOptional, (tag, provider) -> ItemStack.parse(provider, tag).orElse(ItemStack.EMPTY));
}
