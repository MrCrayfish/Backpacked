package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.FrameworkRegistry;
import com.mrcrayfish.framework.api.registry.RegistryContainer;

@RegistryContainer
public class ModRegistries
{
    public static final FrameworkRegistry<AugmentType<? extends Augment<?>>> AUGMENT_TYPES = FrameworkRegistry.<AugmentType<?>>builder(Utils.rl("augment_types")).build();
}
