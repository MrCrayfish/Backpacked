package com.mrcrayfish.backpacked.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.common.augment.data.Farmhand;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FarmhandPlantParticleGroup extends ParticleGroup<FarmhandPlantParticle>
{
    public FarmhandPlantParticleGroup(ParticleEngine engine)
    {
        super(engine);
    }

    @Override
    public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick)
    {
        return new Submitter(this.particles.stream().map(particle -> {
            float time = (particle.life + partialTick) / (float) Farmhand.PLANT_TIME;
            float inverse = 1 - time;
            Vec3 pos = particle.start.scale(inverse * inverse * inverse);
            pos = pos.add(particle.control.scale(3 * inverse * inverse * time));
            pos = pos.add(particle.control.scale(3 * inverse * time * time));
            pos = pos.add(particle.end.scale(time * time * time));
            Vec3 cameraPos = camera.position();
            double posX = pos.x - cameraPos.x();
            double posY = pos.y - cameraPos.y();
            double posZ = pos.z - cameraPos.z();
            return new State(particle.itemRenderState, posX, posY, posZ);
        }).toList());
    }

    private record State(EntityRenderState state, double x, double y, double z) {}

    private record Submitter(List<State> states) implements ParticleGroupRenderState
    {
        @Override
        public void submit(SubmitNodeCollector collector, CameraRenderState camera)
        {
            PoseStack pose = new PoseStack();
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            for(State state : this.states)
            {
                dispatcher.submit(state.state, camera, state.x, state.y, state.z, pose, collector);
            }
        }
    }
}
