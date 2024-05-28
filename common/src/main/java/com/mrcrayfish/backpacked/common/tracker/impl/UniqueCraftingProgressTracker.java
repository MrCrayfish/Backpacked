package com.mrcrayfish.backpacked.common.tracker.impl;

import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.core.registries.BuiltInRegistries;
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

    public UniqueCraftingProgressTracker(int totalCount, ProgressFormatter formatter, Predicate<ItemStack> predicate)
    {
        super(totalCount, formatter, predicate);
    }

    @Override
    protected void processCrafted(ItemStack stack, ServerPlayer player)
    {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
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
        list.forEach(nbt -> {
            ResourceLocation id = ResourceLocation.tryParse(nbt.getAsString());
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

    public static void registerEvent()
    {
        PlayerEvents.CRAFT_ITEM.register((player, stack, inventory) -> {
            if(player.level().isClientSide())
                return;
            UnlockManager.getTrackers(player, UniqueCraftingProgressTracker.class).forEach(tracker -> {
                if(!tracker.isComplete()) {
                    tracker.processCrafted(stack, (ServerPlayer) player);
                }
            });
        });
    }
}
