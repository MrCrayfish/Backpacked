package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class ModSounds
{
    public static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);

    public static final RegistryObject<SoundEvent> ITEM_BACKPACK_PLACE = register("item.backpack.place");

    private static RegistryObject<SoundEvent> register(String id)
    {
        return REGISTER.register(id, () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, id)));
    }
}
