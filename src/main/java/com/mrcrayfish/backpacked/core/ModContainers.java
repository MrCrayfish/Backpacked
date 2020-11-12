package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModContainers
{
    public static final DeferredRegister<ContainerType<?>> REGISTER = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Reference.MOD_ID);

    public static final RegistryObject<ContainerType<BackpackContainer>> BACKPACK = register("backpack", (IContainerFactory<BackpackContainer>) (windowId, playerInventory, data) -> new BackpackContainer(windowId, playerInventory, data.readVarInt()));

    private static <T extends Container> RegistryObject<ContainerType<T>> register(String id, ContainerType.IFactory<T> factory)
    {
        return REGISTER.register(id, () -> new ContainerType<>(factory));
    }
}