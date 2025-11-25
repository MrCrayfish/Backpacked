package com.mrcrayfish.backpacked.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Allows particles to be spawned when in first person since the player model is not drawn
 */
public class FirstPersonEffectsRenderer
{
    public static void draw(AbstractClientPlayer player, PoseStack pose, MultiBufferSource source, float partialTick)
    {
        Minecraft mc = Minecraft.getInstance();

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        double playerX = Mth.lerp(partialTick, player.xOld, player.getX()) - cameraPos.x();
        double playerY = Mth.lerp(partialTick, player.yOld, player.getY()) - cameraPos.y();
        double playerZ = Mth.lerp(partialTick, player.zOld, player.getZ()) - cameraPos.z();

        PlayerRenderer renderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
        Vec3 offset = renderer.getRenderOffset(player, partialTick);
        double renderX = playerX + offset.x();
        double renderY = playerY + offset.y();
        double renderZ = playerZ + offset.z();

        pose.pushPose();
        pose.translate(renderX, renderY, renderZ);
        setupTransforms(player, renderer, pose, partialTick);
        setupBodyRotations(player, renderer, pose);
        if(!player.isSpectator())
        {
            int light = mc.getEntityRenderDispatcher().getPackedLightCoords(player, partialTick);
            renderBackpack(player, pose, source, light, partialTick);
        }
        pose.popPose();
    }

    /**
     * Sets up the transform required to mimic the rendering of the body model part
     *
     * @param player the local player instance
     * @param renderer the renderer of the player
     * @param stack the current pose stack
     * @param partialTick the current partial tick
     */
    private static void setupTransforms(AbstractClientPlayer player, PlayerRenderer renderer, PoseStack stack, float partialTick)
    {
        float scale = player.getScale();
        stack.scale(scale, scale, scale);

        float bodyRot = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);
        ClientServices.CLIENT.invokeRotationSetup(renderer, player, stack, scale, bodyRot, partialTick);

        stack.scale(-1, -1, 1);
        stack.scale(0.9375F, 0.9375F, 0.9375F);
        stack.translate(0, -1.501F, 0);

        // Fixes the position when fall flying
        float defaultEyeHeight = player.getEyeHeight(Pose.STANDING);
        float deltaEyeHeight = player.getEyeHeight() - defaultEyeHeight;
        stack.translate(0, -deltaEyeHeight, 0);
    }

    /**
     * Animate the body model part and apply transform. Only used for crouch animation.
     *
     * @param player the local player
     * @param renderer the renderer of the player
     */
    private static void setupBodyRotations(AbstractClientPlayer player, PlayerRenderer renderer, PoseStack pose)
    {
        ModelPart body = renderer.getModel().body;
        body.yRot = 0;
        body.xRot = player.isCrouching() ? 0.5F : 0;
        body.y = player.isCrouching() ? 3.2F : 0;
        body.translateAndRotate(pose);
    }

    private static void renderBackpack(AbstractClientPlayer player, PoseStack pose, MultiBufferSource source, int light, float partialTick)
    {
        Optional<CosmeticProperties> propertiesOptional = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player);
        if(propertiesOptional.isEmpty())
            return;

        CosmeticProperties properties = propertiesOptional.get();
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        if(chestStack.getItem() == Items.ELYTRA && !properties.showWithElytra())
            return;

        if(!Services.BACKPACK.isBackpackVisible(player))
            return;

        ResourceLocation cosmeticId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
        ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(cosmeticId);
        if(backpack == null)
            return;

        // Apply transforms to fix rotation and inverted model
        pose.mulPose(Axis.YP.rotationDegrees(180.0F));
        pose.scale(1.05F, -1.05F, -1.05F);
        int offset = !chestStack.isEmpty() ? 3 : 2;
        pose.translate(0, -0.06, offset * 0.0625);

        ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
        meta.renderer().ifPresent(renderer -> {
            renderer.render(new BackpackRenderContext(Scene.ON_ENTITY, RenderMode.EFFECTS_ONLY, pose, source, light, backpack, player, player.level(), partialTick, model -> {}, player.tickCount));
        });
    }
}
