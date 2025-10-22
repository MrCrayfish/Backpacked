package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModEnchantments
{
    public static final ResourceKey<Enchantment> REPAIRMAN = create("repairman");

    private static ResourceKey<Enchantment> create(String name)
    {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}
