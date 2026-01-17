package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.sounds.SoundEvent;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModSounds
{
    public static final RegistryEntry<SoundEvent> ITEM_BACKPACK_PLACE = RegistryEntry.soundEvent(Utils.id("item.backpack.place"), id -> () -> SoundEvent.createVariableRangeEvent(id));
    public static final RegistryEntry<SoundEvent> AUGMENT_LOOTBOUND_TAKE_ITEM = RegistryEntry.soundEvent(Utils.id("augment.backpacked.lootbound.take_item"), id -> () -> SoundEvent.createVariableRangeEvent(id));
}
