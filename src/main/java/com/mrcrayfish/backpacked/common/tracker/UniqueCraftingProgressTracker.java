package com.mrcrayfish.backpacked.common.tracker;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class UniqueCraftingProgressTracker extends CraftingProgressTracker
{
    protected Set<ResourceLocation> craftedItems = new HashSet<>();

    public UniqueCraftingProgressTracker(int totalCount, Predicate<ItemStack> predicate)
    {
        super(totalCount, predicate);
    }

    @Override
    public void processCrafted(ItemStack stack, ServerPlayer player)
    {
        ResourceLocation id = Registry.ITEM.getKey(stack.getItem());
        if(!this.craftedItems.contains(id) && this.predicate.test(stack))
        {
            this.count++;
            this.craftedItems.add(id);
            this.markForCompletionTest(player);
        }
    }

    @Override
    public void read(CompoundTag tag)
    {
        super.read(tag);
        this.craftedItems.clear();
        ListTag list = tag.getList("CraftedItems", Tag.TAG_STRING);
        list.forEach(inbt -> {
            ResourceLocation id = ResourceLocation.tryParse(inbt.getAsString());
            if(id != null) this.craftedItems.add(id);
        });
    }

    @Override
    public void write(CompoundTag tag)
    {
        super.write(tag);
        ListTag list = new ListTag();
        this.craftedItems.forEach(location -> {
            list.add(StringTag.valueOf(location.toString()));
        });
        tag.put("CraftedItems", list);
    }
}
