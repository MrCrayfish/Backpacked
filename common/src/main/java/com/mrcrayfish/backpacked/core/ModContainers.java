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
        Utils.rl("backpack"),
        (windowId, playerInventory, buf) -> {
            BackpackContainerData data = BackpackContainerData.decode(buf);
            return new BackpackContainerMenu(windowId, playerInventory, data);
        });

    public static final RegistryEntry<MenuType<BackpackManagementMenu>> MANAGEMENT = RegistryEntry.menuTypeWithData(
        Utils.rl("management"),
        (windowId, playerInventory, buf) -> {
            ManagementContainerData data = ManagementContainerData.decode(buf);
            return new BackpackManagementMenu(windowId, playerInventory, data);
        });

    public static final RegistryEntry<MenuType<BackpackShelfMenu>> BACKPACK_SHELF = RegistryEntry.menuTypeWithData(
        Utils.rl("backpack_shelf"),
        (windowId, playerInventory, buf) -> {
            ManagementContainerData data = ManagementContainerData.decode(buf);
            return new BackpackShelfMenu(windowId, playerInventory, data);
        });
}
