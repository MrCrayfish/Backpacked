package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.enchantment.FunnellingEnchantment;
import com.mrcrayfish.backpacked.enchantment.ImbuedHideEnchantment;
import com.mrcrayfish.backpacked.enchantment.LootedEnchantment;
import com.mrcrayfish.backpacked.enchantment.MarksmanEnchantment;
import com.mrcrayfish.backpacked.enchantment.RepairmanEnchantment;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModEnchantments
{
    public static final RegistryEntry<FunnellingEnchantment> FUNNELLING = RegistryEntry.enchantment(new ResourceLocation(Constants.MOD_ID, "funnelling"), FunnellingEnchantment::new);
    public static final RegistryEntry<RepairmanEnchantment> REPAIRMAN = RegistryEntry.enchantment(new ResourceLocation(Constants.MOD_ID, "repairman"), RepairmanEnchantment::new);
    public static final RegistryEntry<LootedEnchantment> LOOTED = RegistryEntry.enchantment(new ResourceLocation(Constants.MOD_ID, "looted"), LootedEnchantment::new);
    public static final RegistryEntry<ImbuedHideEnchantment> IMBUED_HIDE = RegistryEntry.enchantment(new ResourceLocation(Constants.MOD_ID, "imbued_hide"), ImbuedHideEnchantment::new);
    public static final RegistryEntry<MarksmanEnchantment> MARKSMAN = RegistryEntry.enchantment(new ResourceLocation(Constants.MOD_ID, "marksman"), MarksmanEnchantment::new);
}
