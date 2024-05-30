package com.mrcrayfish.backpacked.integration.item;

import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ForgeBackpackItem extends BackpackItem
{
    public ForgeBackpackItem(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return Curios.createBackpackProvider(stack);
    }
}
