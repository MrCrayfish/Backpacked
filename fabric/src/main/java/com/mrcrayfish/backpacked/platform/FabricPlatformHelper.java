package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import net.fabricmc.fabric.impl.resource.loader.BuiltinModResourcePackSource;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class FabricPlatformHelper implements IPlatformHelper
{
    @Override
    public boolean isModLoaded(String modId)
    {
        return FabricLoader.getInstance().isModLoaded(modId);
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
        return true;
    }

    @Override
    public CreativeModeTab.Output createCreativeTabOutput(Consumer<ItemStack> consumer)
    {
        return (stack, visibility) -> consumer.accept(stack);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public boolean isBuiltinOrModResourcePack(String info, PackSource source)
    {
        if(source == PackSource.BUILT_IN) return true;
        if(info.equals("vanilla")) return true;
        if(info.equals("bundle")) return true;
        if(info.equals("fabric")) return true;
        if(source == ModResourcePackCreator.RESOURCE_PACK_SOURCE) return true;
        if(source instanceof BuiltinModResourcePackSource) return true;
        //if(info.knownPackInfo().stream().anyMatch(pack -> pack.namespace().equals("minecraft"))) return true;
        return false;
    }
}
