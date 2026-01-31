package com.mrcrayfish.backpacked.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.backpacked.common.augment.data.Farmhand;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FarmhandPlantParticle extends Particle
{
    private final EntityRenderDispatcher dispatcher;
    private final RenderBuffers buffers;
    private final Vec3 start;
    private final Vec3 end;
    private final Vec3 control;
    private final Entity entity;
    private int life = 0;

    public FarmhandPlantParticle(EntityRenderDispatcher dispatcher, RenderBuffers buffers, ClientLevel level, ItemStack stack, Vec3 start, Vec3 end)
    {
        super(level, start.x, start.y, start.z);
        this.dispatcher = dispatcher;
        this.buffers = buffers;
        this.start = start;
        this.end = end;
        this.control = new Vec3(
            start.x + (end.x - start.x) / 2,
            Math.max(start.y, end.y) + 1,
            start.z + (end.z - start.z) / 2
        );
        this.hasPhysics = false;
        this.entity = new ItemEntity(level, 0, 0, 0, stack);
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
    public void render(VertexConsumer consumer, Camera camera, float partialTick)
    {
        float time = (this.life + partialTick) / (float) Farmhand.PLANT_TIME;
        float inverse = 1 - time;
        Vec3 pos = this.start.scale(inverse * inverse * inverse);
        pos = pos.add(this.control.scale(3 * inverse * inverse * time));
        pos = pos.add(this.control.scale(3 * inverse * time * time));
        pos = pos.add(this.end.scale(time * time * time));
        Vec3 cameraPos = camera.getPosition();
        MultiBufferSource.BufferSource source = this.buffers.bufferSource();
        double posX = pos.x - cameraPos.x();
        double posY = pos.y - cameraPos.y();
        double posZ = pos.z - cameraPos.z();
        int light = this.dispatcher.getPackedLightCoords(this.entity, partialTick);
        this.dispatcher.render(this.entity, posX, posY, posZ, this.entity.getYRot(), 0, new PoseStack(), source, light);
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.CUSTOM;
    }
}
