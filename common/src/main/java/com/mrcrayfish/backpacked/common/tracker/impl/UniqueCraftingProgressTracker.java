package com.mrcrayfish.backpacked.common.tracker.impl;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.framework.api.event.FrameworkPlayerEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class UniqueCraftingProgressTracker extends CraftingProgressTracker
{
    protected Set<Identifier> craftedItems = new HashSet<>();

    public UniqueCraftingProgressTracker(int totalCount, ProgressFormatter formatter, Predicate<ItemStack> predicate)
    {
        super(totalCount, formatter, predicate);
    }

    @Override
    protected void processCrafted(ItemStack stack, ServerPlayer player)
    {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if(!this.craftedItems.contains(id) && this.predicate.test(stack))
        {
            this.count++;
            this.craftedItems.add(id);
            this.markForCompletionTest(player);
        }
    }

    @Override
    public void read(ValueInput input)
    {
        super.read(input);
        this.craftedItems.clear();
        input.list("CraftedItems", Codec.STRING).ifPresent(list -> {
            list.forEach(string -> {
                Identifier id = Identifier.tryParse(string);
                if(id != null) this.craftedItems.add(id);
            });
        });
    }

    @Override
    public void write(ValueOutput output)
    {
        super.write(output);
        ValueOutput.TypedOutputList<String> list = output.list("CraftedItems", Codec.STRING);
        this.craftedItems.forEach(location -> list.add(location.toString()));
    }

    public static void registerEvent()
    {
        FrameworkPlayerEvents.CRAFTED_ITEM.register((player, stack, inventory) -> {
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
