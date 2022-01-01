package com.mrcrayfish.backpacked.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessagePlayerBackpack;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen instanceof BackpackScreen)
        {
            if(event.getAction() == GLFW.GLFW_PRESS && event.getKey() == ClientHandler.KEY_BACKPACK.getKey().getValue())
            {
                minecraft.player.closeContainer();
            }
        }
        else if(minecraft.player != null && minecraft.screen == null)
        {
            ClientPlayerEntity player = minecraft.player;
            if(ClientHandler.KEY_BACKPACK.isDown() && ClientHandler.KEY_BACKPACK.consumeClick())
            {
                if(!Backpacked.getBackpackStack(player).isEmpty())
                {
                    Network.getPlayChannel().sendToServer(new MessageOpenBackpack());
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTickEnd(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null)
            return;

        List<PlayerEntity> players = mc.level.getEntities(EntityType.PLAYER, mc.player.getBoundingBox().inflate(16F), player -> true);
        for(PlayerEntity player : players)
        {
            if(Backpacked.isCuriosLoaded() && !Curios.isBackpackVisible(player))
                continue;

            ItemStack stack = Backpacked.getBackpackStack(player);
            if(stack.isEmpty())
                continue;

            if(!canShowBackpackEffects(stack))
                continue;

            String modelName = stack.getOrCreateTag().getString("BackpackModel");
            BackpackModel model = BackpackLayer.getModel(modelName);
            model.tickForPlayer(PickpocketUtil.getBackpackBox(player, 1.0F).getCenter(), player);
        }
    }

    public static boolean canShowBackpackEffects(ItemStack stack)
    {
        CompoundNBT tag = stack.getOrCreateTag();
        if(tag.contains(BackpackModelProperty.SHOW_EFFECTS.getTagName(), Constants.NBT.TAG_BYTE))
        {
            return tag.getBoolean(BackpackModelProperty.SHOW_EFFECTS.getTagName());
        }
        return true;
    }

    @SubscribeEvent
    public void onRightClick(InputEvent.ClickInputEvent event)
    {
        if(event.isUseItem())
        {
            if(Config.SERVER.pickpocketBackpacks.get() && this.performBackpackRaytrace())
            {
                event.setCanceled(true);
            }
        }
    }

    private boolean performBackpackRaytrace()
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null || mc.gameMode == null)
            return false;

        double range = Config.SERVER.pickpocketMaxReachDistance.get();
        List<PlayerEntity> players = mc.level.getEntities(EntityType.PLAYER, mc.player.getBoundingBox().inflate(range), player -> {
            return !Backpacked.getBackpackStack(player).isEmpty() && !player.equals(mc.player) && PickpocketUtil.canPickpocketPlayer(player, mc.player);
        });

        if(players.isEmpty())
            return false;

        Vector3d start = mc.player.getEyePosition(1.0F);
        Vector3d end = mc.player.getViewVector(1.0F).scale(mc.gameMode.getPickRange()).add(start);

        double closestDistance = Double.MAX_VALUE;
        PlayerEntity hitPlayer = null;
        for(PlayerEntity player : players)
        {
            AxisAlignedBB box = PickpocketUtil.getBackpackBox(player, 1.0F);
            Optional<Vector3d> optionalHitVec = box.clip(start, end);
            if(!optionalHitVec.isPresent())
                continue;

            double distance = start.distanceTo(optionalHitVec.get());
            if(distance < closestDistance)
            {
                closestDistance = distance;
                hitPlayer = player;
            }
        }

        if(hitPlayer != null && PickpocketUtil.canSeeBackpack(hitPlayer, mc.player))
        {
            Network.getPlayChannel().sendToServer(new MessagePlayerBackpack(hitPlayer.getId()));
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderWorldLastEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(!mc.getEntityRenderDispatcher().shouldRenderHitBoxes())
            return;

        if(!Config.SERVER.pickpocketBackpacks.get())
            return;

        MatrixStack stack = event.getMatrixStack();
        stack.pushPose();
        Vector3d view = mc.gameRenderer.getMainCamera().getPosition();
        stack.translate(-view.x(), -view.y, -view.z());
        IRenderTypeBuffer.Impl source = mc.renderBuffers().bufferSource();
        for(PlayerEntity player : mc.level.players())
        {
            if(Backpacked.getBackpackStack(player).isEmpty())
                continue;

            if(player.isLocalPlayer())
                continue;

            boolean inReach = PickpocketUtil.inReachOfBackpack(player, mc.player) && PickpocketUtil.canSeeBackpack(player, mc.player);
            float boxRed = inReach ? 0.0F : 1.0F;
            float boxGreen = inReach ? 1.0F : 1.0F;
            float boxBlue = inReach ? 0.0F : 1.0F;
            IVertexBuilder builder = source.getBuffer(RenderType.lines());
            WorldRenderer.renderLineBox(stack, builder, PickpocketUtil.getBackpackBox(player, event.getPartialTicks()), boxRed, boxGreen, boxBlue, 1.0F);

            float bodyRotation = MathHelper.lerp(event.getPartialTicks(), player.yBodyRotO, player.yBodyRot);
            boolean inRange = PickpocketUtil.inRangeOfBackpack(player, mc.player);
            float lineRed = inRange ? 0.0F : 1.0F;
            float lineGreen = inRange ? 1.0F : 1.0F;
            float lineBlue = inRange ? 0.0F : 1.0F;
            Matrix4f matrix4f = stack.last().pose();
            Vector3d pos = player.getPosition(event.getPartialTicks());
            Vector3d start = Vector3d.directionFromRotation(0, bodyRotation + 180 - Config.SERVER.pickpocketMaxRangeAngle.get().floatValue()).scale(Config.SERVER.pickpocketMaxReachDistance.get());
            Vector3d end = Vector3d.directionFromRotation(0, bodyRotation - 180 + Config.SERVER.pickpocketMaxRangeAngle.get().floatValue()).scale(Config.SERVER.pickpocketMaxReachDistance.get());
            builder.vertex(matrix4f, (float) (pos.x + start.x),(float) (pos.y + start.y), (float) (pos.z + start.z)).color(lineRed, lineGreen, lineBlue, 1.0F).endVertex();
            builder.vertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).color(lineRed, lineGreen, lineBlue, 1.0F).endVertex();
            builder.vertex(matrix4f, (float) (pos.x + end.x),(float) (pos.y + end.y), (float) (pos.z + end.z)).color(lineRed, lineGreen, lineBlue, 1.0F).endVertex();
            builder.vertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).color(lineRed, lineGreen, lineBlue, 1.0F).endVertex();
        }
        source.endBatch(RenderType.lines());
        stack.popPose();
    }
}
