package com.mrcrayfish.backpacked.client.gui.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple manager class to handle rendering particles on a screen. To implement this onto a screen,
 * create a new instance of this class as a member in a screen. Next, invoke {@link #tickParticles()}
 * somewhere in {@link Screen#tick()} or {@link AbstractContainerScreen#containerTick()} if a
 * container screen. To render the particles, invoke {@link #renderParticles(GuiGraphics)} in
 * {@link Screen#render(GuiGraphics, int, int, float)}, and ideally this should be invoked after
 * everything is drawn on the screen but before rendering tooltips.
 */
public final class ScreenParticles
{
    private final List<Particle2D> particles = new ArrayList<>();

    /**
     * Adds a new particle to display on the screen.
     *
     * @param particle a Particle2D instance
     */
    public void addParticle(Particle2D particle)
    {
        this.particles.add(particle);
    }

    /**
     * Ticks the particles on the screen, updating their position, rotation, etc. This method also
     * handles removing particles once they reach the end of their life.
     */
    public void tickParticles()
    {
        this.particles.removeIf(Particle2D::tick);
    }

    /**
     * Renders all particles onto the screen
     *
     * @param graphics a GuiGraphics instance
     */
    public void renderParticles(GuiGraphics graphics)
    {
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        this.particles.forEach(p -> p.render(graphics, partialTick));
    }
}
