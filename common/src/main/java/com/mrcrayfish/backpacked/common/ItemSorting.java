package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public enum ItemSorting
{
    ALPHABETICAL("alphabetical", Comparator.comparing(stack -> stack.getItem().getName(stack).getString())),
    ITEMS_FIRST("items_first", Comparator.comparing(stack -> !stack.getDescriptionId().startsWith("item"))), // The description id more accurately represents if a block or an item
    BLOCKS_FIRST("blocks_first", Comparator.comparing(stack -> !stack.getDescriptionId().startsWith("block"))), // The description id more accurately represents if a block or an item
    STACK_SIZE("stack_size", Comparator.comparingInt(ItemStack::getCount).reversed()),
    MOST_DAMAGED("most_damaged", Comparator.<ItemStack>comparingInt(stack -> stack.isDamageableItem() ? (stack.getDamageValue() * 1000 / stack.getMaxDamage()) : -1).reversed()),
    CREATIVE_CATEGORY("creative_category", Comparator.comparingInt(stack -> CreativeCategorySort.getSortIndex(stack.getItem()))),
    MOD("mod", Comparator.comparing(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace())),
    SHUFFLE("shuffle", Comparator.comparingInt(stack -> Utils.RANDOM.nextInt()));

    public static final StreamCodec<FriendlyByteBuf, ItemSorting> STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(ItemSorting.class));

    private final Component label;
    private final Comparator<ItemStack> comparator;

    ItemSorting(String key, Comparator<ItemStack> comparator)
    {
        this.label = Component.translatable("backpacked.gui.sort.%s".formatted(key));
        this.comparator = comparator;
    }

    public Component label()
    {
        return this.label;
    }

    public Comparator<ItemStack> comparator()
    {
        return this.comparator;
    }
}
