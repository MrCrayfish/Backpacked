package com.mrcrayfish.backpacked.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class InventoryHelper
{
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

    private static void spawnStack(ItemStack stack, Level level, Vec3 pos)
    {
        ItemEntity entity = new ItemEntity(level, pos.x, pos.y, pos.z, stack.copyAndClear());
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    }

    public static Stream<ItemStack> streamFor(Container container)
    {
        return IntStream.range(0, container.getContainerSize()).mapToObj(container::getItem);
    }
}
