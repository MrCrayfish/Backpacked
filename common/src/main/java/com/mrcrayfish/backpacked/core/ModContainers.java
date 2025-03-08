package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import com.mrcrayfish.backpacked.inventory.container.data.BackpackContainerData;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModContainers
{
    public static final RegistryEntry<MenuType<BackpackContainerMenu>> BACKPACK = RegistryEntry.menuTypeWithData(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack"),
        BackpackContainerData.STREAM_CODEC,
        BackpackContainerMenu::new
    );

    public static final RegistryEntry<MenuType<BackpackManagementMenu>> MANAGEMENT = RegistryEntry.menuType(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "management"),
            BackpackManagementMenu::new
    );
}
