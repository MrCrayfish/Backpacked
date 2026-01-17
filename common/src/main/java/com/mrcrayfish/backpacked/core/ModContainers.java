package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.BackpackManagementMenu;
import com.mrcrayfish.backpacked.inventory.container.BackpackShelfMenu;
import com.mrcrayfish.backpacked.inventory.container.data.BackpackContainerData;
import com.mrcrayfish.backpacked.inventory.container.data.ManagementContainerData;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.world.inventory.MenuType;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModContainers
{
    public static final RegistryEntry<MenuType<BackpackContainerMenu>> BACKPACK = RegistryEntry.menuTypeWithData(
            Utils.id("backpack"),
            BackpackContainerData.STREAM_CODEC,
            BackpackContainerMenu::new
    );

    public static final RegistryEntry<MenuType<BackpackManagementMenu>> MANAGEMENT = RegistryEntry.menuTypeWithData(
            Utils.id("management"),
            ManagementContainerData.STREAM_CODEC,
            BackpackManagementMenu::new
    );

    public static final RegistryEntry<MenuType<BackpackShelfMenu>> BACKPACK_SHELF = RegistryEntry.menuTypeWithData(
            Utils.id("backpack_shelf"),
            ManagementContainerData.STREAM_CODEC,
            BackpackShelfMenu::new
    );
}
