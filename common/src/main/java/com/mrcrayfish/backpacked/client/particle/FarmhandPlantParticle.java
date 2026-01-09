package com.mrcrayfish.backpacked.client.particle;

import com.mrcrayfish.backpacked.common.augment.data.Farmhand;
import com.mrcrayfish.backpacked.core.ModParticleRenderTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;

public class FarmhandPlantParticle extends Particle
{
    protected final EntityRenderState itemRenderState;
    protected final Vec3 start;
    protected final Vec3 end;
    protected final Vec3 control;
    protected int life = 0;

    public FarmhandPlantParticle(EntityRenderState state, ClientLevel level, Vec3 start, Vec3 end)
    {
        super(level, start.x, start.y, start.z);
        this.itemRenderState = state;
        this.start = start;
        this.end = end;
        this.control = new Vec3(
            start.x + (end.x - start.x) / 2,
            Math.max(start.y, end.y) + 1,
            start.z + (end.z - start.z) / 2
        );
        this.hasPhysics = false;
    }

    @Override
    public void tick()
    {
        if(this.life >= Farmhand.PLANT_TIME)
        {
            this.remove();
        }
        this.life++;
    }

    @Override
    public ParticleRenderType getGroup()
    {
        return ModParticleRenderTypes.FARMHAND_PLANT;
    }
}
