package com.mrcrayfish.backpacked.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

/**
 * Author: MrCrayfish
 */
public class PickpocketDebugRenderer
{
    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderLevelStageEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(!mc.getEntityRenderDispatcher().shouldRenderHitBoxes())
            return;

        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
            return;

        if(!Config.PICKPOCKETING.enabled.get())
            return;

        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        Vec3 view = event.getCamera().getPosition();
        stack.translate(-view.x(), -view.y(), -view.z());
        MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
        for(Player player : mc.level.players())
        {
            // TODO restore this
            //if(BackpackHelper.getBackpackStack(player).isEmpty())
                //continue;

            if(player.isLocalPlayer())
                continue;

            stack.pushPose();

            boolean inReach = PickpocketUtil.inReachOfBackpack(player, mc.player, Config.PICKPOCKETING.maxReachDistance.get()) && PickpocketUtil.canSeeBackpack(player, mc.player);
            float boxRed = inReach ? 0.0F : 1.0F;
            float boxGreen = inReach ? 1.0F : 1.0F;
            float boxBlue = inReach ? 0.0F : 1.0F;
            float partialTick = event.getPartialTick().getGameTimeDeltaTicks();
            VertexConsumer builder = source.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(stack, builder, PickpocketUtil.getBackpackBox(player, partialTick), boxRed, boxGreen, boxBlue, 1.0F);

            float bodyRotation = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot);
            boolean inRange = PickpocketUtil.inRangeOfBackpack(player, mc.player);
            float lineRed = inRange ? 0.0F : 1.0F;
            float lineGreen = inRange ? 1.0F : 1.0F;
            float lineBlue = inRange ? 0.0F : 1.0F;
            Matrix4f matrix4f = stack.last().pose();
            Vec3 pos = player.getPosition(partialTick);
            Vec3 start = Vec3.directionFromRotation(0, bodyRotation + 180 - Config.PICKPOCKETING.maxRangeAngle.get().floatValue()).scale(Config.PICKPOCKETING.maxReachDistance.get());
            Vec3 end = Vec3.directionFromRotation(0, bodyRotation - 180 + Config.PICKPOCKETING.maxRangeAngle.get().floatValue()).scale(Config.PICKPOCKETING.maxReachDistance.get());
            builder.addVertex(matrix4f, (float) (pos.x + start.x),(float) (pos.y + start.y), (float) (pos.z + start.z)).setColor(lineRed, lineGreen, lineBlue, 1.0F).setNormal(0.0F, 1.0F, 0.0F);
            builder.addVertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).setColor(lineRed, lineGreen, lineBlue, 1.0F).setNormal(0.0F, 1.0F, 0.0F);
            builder.addVertex(matrix4f, (float) (pos.x + end.x),(float) (pos.y + end.y), (float) (pos.z + end.z)).setColor(lineRed, lineGreen, lineBlue, 1.0F).setNormal(0.0F, 1.0F, 0.0F);
            builder.addVertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).setColor(lineRed, lineGreen, lineBlue, 1.0F).setNormal(0.0F, 1.0F, 0.0F);

            stack.popPose();
        }
        source.endBatch(RenderType.lines());
        stack.popPose();
    }
}
