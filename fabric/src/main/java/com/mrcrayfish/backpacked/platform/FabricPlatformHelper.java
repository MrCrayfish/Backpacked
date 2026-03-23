package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import com.mrcrayfish.framework.Registration;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.impl.resource.pack.BuiltinModPackSource;
import net.fabricmc.fabric.impl.resource.pack.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.ItemLike;

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
    @SuppressWarnings("UnstableApiUsage")
    public boolean isBuiltinOrModResourcePack(PackLocationInfo info)
    {
        if(info.source() == PackSource.BUILT_IN) return true;
        if(info.id().equals("fabric")) return true;
        if(info.source() == ModResourcePackCreator.RESOURCE_PACK_SOURCE) return true;
        if(info.source() instanceof BuiltinModPackSource) return true;
        if(info.knownPackInfo().stream().anyMatch(pack -> pack.namespace().equals("minecraft"))) return true;
        return false;
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
    public void generateCreativeTabOutput(CreativeModeTab.DisplayItemsGenerator generator, CreativeModeTab.ItemDisplayParameters parameters, Consumer<ItemStack> consumer)
    {
        generator.accept(parameters, (stack, visibility) -> consumer.accept(stack));
    }

    @Override
    public TagKey<Item> getLeatherItemTag()
    {
        return ConventionalItemTags.LEATHERS;
    }

    @Override
    public TagKey<Item> getStringItemTag()
    {
        return ConventionalItemTags.STRINGS;
    }

    @Override
    public TagKey<Item> getIronIngotItemTag()
    {
        return ConventionalItemTags.IRON_INGOTS;
    }

    @Override
    public TagKey<Item> getWoodenRodsTag()
    {
        return ConventionalItemTags.WOODEN_RODS;
    }

    @Override
    public TagKey<Item> getCopperIngotItemTag()
    {
        return ConventionalItemTags.COPPER_INGOTS;
    }

    @Override
    public void setupCreativeTabDisplayItems(CreativeModeTab.Builder builder)
    {
        builder.displayItems((params, output) -> {
            Registration.get(Registries.BLOCK).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
                output.accept((ItemLike) entry.get());
            });
            Registration.get(Registries.ITEM).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).forEach(entry -> {
                output.accept((ItemLike) entry.get());
            });
        });
    }
}
