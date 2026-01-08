package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import net.minecraft.client.particle.ParticleRenderType;

@RegistryContainer(clientOnly = true)
public class ModParticleRenderTypes
{
    public static final ParticleRenderType FARMHAND_PLANT = new ParticleRenderType(Utils.id("backpacked_farmhand_plant").toString());
}
