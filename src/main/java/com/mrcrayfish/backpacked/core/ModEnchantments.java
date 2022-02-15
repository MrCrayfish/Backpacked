package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.enchantment.FunnellingEnchantment;
import com.mrcrayfish.backpacked.enchantment.ImbuedHideEnchantment;
import com.mrcrayfish.backpacked.enchantment.LootedEnchantment;
import com.mrcrayfish.backpacked.enchantment.MarksmanEnchantment;
import com.mrcrayfish.backpacked.enchantment.RepairmanEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class ModEnchantments
{
    public static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Reference.MOD_ID);

    public static final RegistryObject<FunnellingEnchantment> FUNNELLING = REGISTER.register("funnelling", FunnellingEnchantment::new);
    public static final RegistryObject<RepairmanEnchantment> REPAIRMAN = REGISTER.register("repairman", RepairmanEnchantment::new);
    public static final RegistryObject<LootedEnchantment> LOOTED = REGISTER.register("looted", LootedEnchantment::new);
    public static final RegistryObject<ImbuedHideEnchantment> IMBUED_HIDE = REGISTER.register("imbued_hide", ImbuedHideEnchantment::new);
    public static final RegistryObject<MarksmanEnchantment> MARKSMAN = REGISTER.register("marksman", MarksmanEnchantment::new);
}
