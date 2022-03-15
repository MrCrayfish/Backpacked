package com.mrcrayfish.backpacked.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
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
    public static ListTag saveAllItems(ListTag list, SimpleContainer container)
    {
        for(int i = 0; i < container.getContainerSize(); ++i)
        {
            ItemStack itemstack = container.getItem(i);
            if(!itemstack.isEmpty())
            {
                CompoundTag compound = new CompoundTag();
                compound.putByte("Slot", (byte) i);
                itemstack.save(compound);
                list.add(compound);
            }
        }
        return list;
    }

    public static void loadAllItems(ListTag list, SimpleContainer container, Level level, Vec3 pos)
    {
        for(int i = 0; i < list.size(); i++)
        {
            CompoundTag compound = list.getCompound(i);
            int slot = compound.getByte("Slot") & 255;
            if(slot < container.getContainerSize())
            {
                container.setItem(slot, ItemStack.of(compound));
            }
            else if(!level.isClientSide())
            {
                ItemStack stack = ItemStack.of(compound);
                ItemEntity entity = new ItemEntity(level, pos.x, pos.y, pos.z, container.addItem(stack));
                entity.setDefaultPickUpDelay();
                level.addFreshEntity(entity);
            }
        }
    }

    public static void mergeInventory(SimpleContainer source, SimpleContainer target, Level level, Vec3 pos)
    {
        for(int i = 0; i < source.getContainerSize(); i++)
        {
            if(i < target.getContainerSize())
            {
                target.setItem(i, source.getItem(i).copy());
            }
            else if(!level.isClientSide())
            {
                ItemStack stack = source.getItem(i).copy();
                ItemEntity entity = new ItemEntity(level, pos.x, pos.y, pos.z, target.addItem(stack));
                entity.setDefaultPickUpDelay();
                level.addFreshEntity(entity);
            }
        }
    }

    public static Stream<ItemStack> streamFor(Container container)
    {
        return IntStream.range(0, container.getContainerSize()).mapToObj(container::getItem);
    }
}
