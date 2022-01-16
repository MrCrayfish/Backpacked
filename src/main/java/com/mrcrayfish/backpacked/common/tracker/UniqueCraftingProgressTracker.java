package com.mrcrayfish.backpacked.common.tracker;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

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
    public void processCrafted(ItemStack stack, ServerPlayerEntity player)
    {
        ResourceLocation id = stack.getItem().getRegistryName();
        if(!this.craftedItems.contains(id) && this.predicate.test(stack))
        {
            this.count++;
            this.craftedItems.add(id);
            this.markForCompletionTest(player);
        }
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);
        this.craftedItems.clear();
        ListNBT list = tag.getList("CraftedItems", Constants.NBT.TAG_STRING);
        list.forEach(inbt -> {
            ResourceLocation id = ResourceLocation.tryParse(inbt.getAsString());
            if(id != null) this.craftedItems.add(id);
        });
    }

    @Override
    public void write(CompoundNBT tag)
    {
        super.write(tag);
        ListNBT list = new ListNBT();
        this.craftedItems.forEach(location -> {
            list.add(StringNBT.valueOf(location.toString()));
        });
        tag.put("CraftedItems", list);
    }
}
