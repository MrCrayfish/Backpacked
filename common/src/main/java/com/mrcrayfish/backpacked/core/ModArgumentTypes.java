package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.common.command.arguments.BackpackArgument;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModArgumentTypes
{
    public static final RegistryEntry<SingletonArgumentInfo<BackpackArgument>> BACKPACK = RegistryEntry.commandArgumentType(Utils.rl("backpack"), BackpackArgument.class, () -> SingletonArgumentInfo.contextFree(BackpackArgument::backpacks));
}
