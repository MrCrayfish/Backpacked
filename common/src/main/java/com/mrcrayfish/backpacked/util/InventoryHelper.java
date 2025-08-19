package com.mrcrayfish.backpacked.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class InventoryHelper
{
    public static void mergeItemsOrSpawnIntoLevel(NonNullList<ItemStack> source, NonNullList<ItemStack> target, Level level, Vec3 pos)
    {
        for(int i = 0; i < source.size(); i++)
        {
            ItemStack stack = source.get(i);
            if(i < target.size())
            {
                target.set(i, stack.copy());
                continue;
            }
            spawnStack(stack, level, pos);
        }
    }

    public static void mergeInventoryOrSpawnIntoLevel(Container source, Container target, Level level, Vec3 pos)
    {
        for(int i = 0; i < source.getContainerSize(); i++)
        {
            ItemStack stack = source.getItem(i);
            if(i < target.getContainerSize() && target.canPlaceItem(i, stack))
            {
                target.setItem(i, stack.copy());
                continue;
            }
            spawnStack(stack, level, pos);
        }
    }

    public static void spawnStack(ItemStack stack, Level level, Vec3 pos)
    {
        if(!level.isClientSide())
        {
            ItemEntity entity = new ItemEntity(level, pos.x, pos.y, pos.z, stack.copyAndClear());
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
    }

    public static Stream<ItemStack> streamFor(Container container)
    {
        return IntStream.range(0, container.getContainerSize()).mapToObj(container::getItem);
    }

    /**
     * Determines if the given item and count is found in the list of containers
     *
     * @param item the item to find
     * @param count the total count of the item to find
     * @param containers the containers to search
     * @return the sum of the
     */
    public static boolean hasRemovableItemAndCount(Item item, int count, List<Container> containers)
    {
        int found = 0;
        for(Container container : containers)
        {
            for(int i = 0; i < container.getContainerSize(); i++)
            {
                ItemStack stack = container.getItem(i);
                if(stack.isEmpty() || !stack.is(item))
                    continue;
                if(!container.canTakeItem(container, i, stack))
                    continue;
                found += stack.getCount();
                if(found >= count)
                    return true;
            }
        }
        return false;
    }

    /**
     * Creates a {@link Runnable} job that consumes the given item and count from the given
     * containers. If the containers do not contain the required count of the item, an empty
     * optional will be returned, and this means the job cannot be completed.
     *
     * @param item the item to consume
     * @param count the required amount to consume
     * @param containers a list of containers to search for the item
     * @return an optional containing the {@link Runnable} or empty if unable to consume
     */
    public static Optional<Runnable> createRemoveItemJob(Item item, int count, List<Container> containers)
    {
        if(item == Items.AIR || count < 0 || containers.isEmpty())
            return Optional.empty();

        List<Runnable> transactions = new ArrayList<>();
        outer: for(Container container : containers)
        {
            for(int i = 0; i < container.getContainerSize(); i++)
            {
                ItemStack stack = container.getItem(i);
                if(stack.isEmpty() || !stack.is(item))
                    continue;

                if(!container.canTakeItem(container, i, stack))
                    continue;

                int shrink = Math.min(stack.getCount(), count);
                count -= shrink;
                transactions.add(() -> {
                    stack.shrink(shrink);
                    container.setChanged();
                });

                if(shrink == 0)
                    break outer;
            }
        }
        if(count == 0)
        {
            return Optional.of(() -> transactions.forEach(Runnable::run));
        }
        return Optional.empty();
    }
}
