package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModDataComponents
{
    public static final RegistryEntry<DataComponentType<BackpackProperties>> BACKPACK_PROPERTIES = RegistryEntry.dataComponentType(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack_properties"), builder -> {
        return builder.persistent(BackpackProperties.CODEC).networkSynchronized(BackpackProperties.STREAM_CODEC);
    });
}
