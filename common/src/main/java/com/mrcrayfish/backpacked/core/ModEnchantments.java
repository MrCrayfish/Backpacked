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
    public static final ResourceKey<Enchantment> FUNNELLING = create("funnelling");
    public static final ResourceKey<Enchantment> REPAIRMAN = create("repairman");
    public static final ResourceKey<Enchantment> LOOTED = create("looted");
    public static final ResourceKey<Enchantment> IMBUED_HIDE = create("imbued_hide");
    public static final ResourceKey<Enchantment> MARKSMAN = create("marksman");

    private static ResourceKey<Enchantment> create(String name)
    {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name));
    }
}
