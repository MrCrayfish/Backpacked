package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModSounds
{
    public static final RegistryEntry<SoundEvent> ITEM_BACKPACK_PLACE = RegistryEntry.soundEvent(new ResourceLocation(Constants.MOD_ID, "item.backpack.place"), id -> () -> SoundEvent.createVariableRangeEvent(id));
}
