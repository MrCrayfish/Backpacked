package com.mrcrayfish.backpacked.client.gui.particle;

import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import com.mrcrayfish.framework.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public final class ScreenParticles
{
    private final List<Particle2D> particles = new ArrayList<>();

    public void addParticle(Particle2D particle)
    {
        this.particles.add(particle);
    }

    public void tickParticles()
    {
        this.particles.removeIf(Particle2D::tick);
    }

    public void renderParticles(GuiGraphics graphics, float partialTick)
    {
        // Fixes particles not being smooth on Fabric
        if(Services.PLATFORM.getPlatform().isFabric())
        {
            partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        }
        float finalPartialTick = partialTick;
        this.particles.forEach(p -> p.render(graphics, finalPartialTick));
    }
}
