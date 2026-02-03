package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class ForgePlatformHelper implements IPlatformHelper
{
    @Override
    public boolean isModLoaded(String modId)
    {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean testPredicate(Optional<BlockPredicate> optional, BlockState state, @Nullable CompoundTag tag)
    {
        if(optional.isEmpty())
            return true;
        BlockPredicate predicate = optional.get();
        if(predicate.tag != null && !state.is(predicate.tag))
            return false;
        if(predicate.blocks != null && !predicate.blocks.contains(state.getBlock()))
            return false;
        if(tag != null && !predicate.nbt.matches(tag))
            return false;
        return predicate.properties.matches(state);
    }

    @Override
    public Predicate<ItemStack> getValidProjectiles(ItemStack weapon)
    {
        if(weapon.getItem() instanceof ProjectileWeaponItem item)
        {
            return item.getAllSupportedProjectiles();
        }
        return stack -> false;
    }

    @Override
    public boolean isRepairable(ItemStack stack)
    {
        return stack.isRepairable();
    }

    @Override
    public CreativeModeTab.Output createCreativeTabOutput(Consumer<ItemStack> consumer)
    {
        return (stack, visibility) -> consumer.accept(stack);
    }
}
