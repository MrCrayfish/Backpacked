package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers
{
    private static final List<ContainerType<?>> CONTAINER_TYPES = new ArrayList<>();

    public static final ContainerType<BackpackContainer> BACKPACK = register(new ResourceLocation(Reference.MOD_ID, "backpack"), BackpackContainer::new);

    private static <T extends Container> ContainerType<T> register(ResourceLocation name, ContainerType.IFactory<T> factory)
    {
        ContainerType<T> type = new ContainerType<>(factory);
        type.setRegistryName(name);
        CONTAINER_TYPES.add(type);
        return type;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void registerTypes(final RegistryEvent.Register<ContainerType<?>> event)
    {
        CONTAINER_TYPES.forEach(type -> event.getRegistry().register(type));
        CONTAINER_TYPES.clear();
    }
}
