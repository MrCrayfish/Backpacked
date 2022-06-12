package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.common.command.arguments.BackpackArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class ModArgumentTypes
{
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTER = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, Reference.MOD_ID);

    public static final RegistryObject<SingletonArgumentInfo<BackpackArgument>> BACKPACK = REGISTER.register("backpack", () -> ArgumentTypeInfos.registerByClass(BackpackArgument.class, SingletonArgumentInfo.contextFree(BackpackArgument::backpacks)));
}
