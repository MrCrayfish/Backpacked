package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import com.mrcrayfish.framework.Registration;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class NeoForgePlatformHelper implements IPlatformHelper
{
    @Override
    public boolean isModLoaded(String modId)
    {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isBuiltinOrModResourcePack(PackLocationInfo info)
    {
        if(info.source() == PackSource.BUILT_IN) return true;
        if(info.id().equals("mod_resources")) return true;
        if(info.id().equals("mod_data")) return true;
        if(info.knownPackInfo().stream().anyMatch(pack -> pack.namespace().equals("minecraft"))) return true;
        return false;
    }

    @Override
    public Predicate<ItemStack> getValidProjectiles(ItemStack weapon)
    {
        if(weapon.getItem() instanceof ProjectileWeaponItem item)
        {
            return item.getAllSupportedProjectiles(weapon);
        }
        return stack -> false;
    }

    @Override
    public boolean isRepairable(ItemStack stack)
    {
        return stack.isValidRepairItem(stack);
    }

    @Override
    public void generateCreativeTabOutput(CreativeModeTab.DisplayItemsGenerator generator, CreativeModeTab.ItemDisplayParameters parameters, Consumer<ItemStack> consumer)
    {
        generator.accept(parameters, (stack, visibility) -> consumer.accept(stack));
    }

    @Override
    public TagKey<Item> getLeatherItemTag()
    {
        return Tags.Items.LEATHERS;
    }

    @Override
    public TagKey<Item> getStringItemTag()
    {
        return Tags.Items.STRINGS;
    }

    @Override
    public TagKey<Item> getIronIngotItemTag()
    {
        return Tags.Items.INGOTS_IRON;
    }

    @Override
    public TagKey<Item> getWoodenRodsTag()
    {
        return Tags.Items.RODS_WOODEN;
    }

    @Override
    public TagKey<Item> getCopperIngotItemTag()
    {
        return Tags.Items.INGOTS_COPPER;
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
