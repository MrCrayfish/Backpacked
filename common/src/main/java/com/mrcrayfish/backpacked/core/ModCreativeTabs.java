package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModCreativeTabs
{
    public static final RegistryEntry<CreativeModeTab> MAIN = RegistryEntry.creativeModeTab(Utils.id("creative_tab"), builder -> {
        builder.title(Component.translatable("itemGroup." + Constants.MOD_ID));
        builder.icon(() -> new ItemStack(ModItems.BACKPACK.get()));
        Services.PLATFORM.setupCreativeTabDisplayItems(builder); // Mojang made Output protected :(
    });
}
