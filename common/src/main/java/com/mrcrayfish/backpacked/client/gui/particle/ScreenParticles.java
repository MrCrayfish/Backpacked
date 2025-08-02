package com.mrcrayfish.backpacked.client.gui.particle;

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
        this.particles.forEach(p -> p.render(graphics, partialTick));
    }
}
