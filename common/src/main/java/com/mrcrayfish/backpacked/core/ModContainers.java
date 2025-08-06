package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import com.mrcrayfish.backpacked.inventory.container.BackpackShelfMenu;
import com.mrcrayfish.backpacked.inventory.container.data.BackpackContainerData;
import com.mrcrayfish.backpacked.inventory.container.data.ManagementContainerData;
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

    public static final RegistryEntry<MenuType<BackpackManagementMenu>> MANAGEMENT = RegistryEntry.menuTypeWithData(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "management"),
            ManagementContainerData.STREAM_CODEC,
            BackpackManagementMenu::new
    );

    public static final RegistryEntry<MenuType<BackpackShelfMenu>> BACKPACK_SHELF = RegistryEntry.menuTypeWithData(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack_shelf"),
            ManagementContainerData.STREAM_CODEC,
            BackpackShelfMenu::new
    );
}
