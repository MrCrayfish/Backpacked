package com.mrcrayfish.backpacked.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

/**
 * Author: MrCrayfish
 */
public class ForgeDebugClientEvents
{
    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderLevelStageEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(!mc.getEntityRenderDispatcher().shouldRenderHitBoxes())
            return;

        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
            return;

        if(!Config.SERVER.pickpocketing.enabled.get())
            return;

        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        Vec3 view = event.getCamera().getPosition();
        stack.translate(-view.x(), -view.y(), -view.z());
        MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
        for(Player player : mc.level.players())
        {
            if(Services.BACKPACK.getBackpackStack(player).isEmpty())
                continue;

            if(player.isLocalPlayer())
                continue;

            stack.pushPose();

            boolean inReach = PickpocketUtil.inReachOfBackpack(player, mc.player, Config.SERVER.pickpocketing.maxReachDistance.get()) && PickpocketUtil.canSeeBackpack(player, mc.player);
            float boxRed = inReach ? 0.0F : 1.0F;
            float boxGreen = inReach ? 1.0F : 1.0F;
            float boxBlue = inReach ? 0.0F : 1.0F;
            VertexConsumer builder = source.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(stack, builder, PickpocketUtil.getBackpackBox(player, event.getPartialTick()), boxRed, boxGreen, boxBlue, 1.0F);

            float bodyRotation = Mth.lerp(event.getPartialTick(), player.yBodyRotO, player.yBodyRot);
            boolean inRange = PickpocketUtil.inRangeOfBackpack(player, mc.player);
            float lineRed = inRange ? 0.0F : 1.0F;
            float lineGreen = inRange ? 1.0F : 1.0F;
            float lineBlue = inRange ? 0.0F : 1.0F;
            Matrix4f matrix4f = stack.last().pose();
            Vec3 pos = player.getPosition(event.getPartialTick());
            Vec3 start = Vec3.directionFromRotation(0, bodyRotation + 180 - Config.SERVER.pickpocketing.maxRangeAngle.get().floatValue()).scale(Config.SERVER.pickpocketing.maxReachDistance.get());
            Vec3 end = Vec3.directionFromRotation(0, bodyRotation - 180 + Config.SERVER.pickpocketing.maxRangeAngle.get().floatValue()).scale(Config.SERVER.pickpocketing.maxReachDistance.get());
            builder.vertex(matrix4f, (float) (pos.x + start.x),(float) (pos.y + start.y), (float) (pos.z + start.z)).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) (pos.x + end.x),(float) (pos.y + end.y), (float) (pos.z + end.z)).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();

            stack.popPose();
        }
        source.endBatch(RenderType.lines());
        stack.popPose();
    }
}
