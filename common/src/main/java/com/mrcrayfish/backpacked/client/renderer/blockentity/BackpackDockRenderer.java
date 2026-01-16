package com.mrcrayfish.backpacked.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.blockentity.BackpackDockBlockEntity;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.BakedModelRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public record BackpackDockRenderer(ItemRenderer itemRenderer) implements BlockEntityRenderer<BackpackDockBlockEntity>
{
    public BackpackDockRenderer(BlockEntityRendererProvider.Context context)
    {
        this(context.getItemRenderer());
    }

    @Override
    public void render(BackpackDockBlockEntity access, float partialTick, PoseStack pose, MultiBufferSource source, int light, int overlay)
    {
        ItemStack stack = access.getBackpack();
        if(stack.isEmpty())
            return;

        CosmeticProperties properties = stack.getOrDefault(ModDataComponents.COSMETIC_PROPERTIES.get(), CosmeticProperties.DEFAULT);
        ResourceLocation modelId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
        ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(modelId);
        if(backpack == null)
            return;

        pose.translate(0.5, 0.0, 0.5);
        pose.translate(0, 0.001, 0);
        pose.mulPose(access.getDirection().getRotation());
        pose.translate(-0.5, 0.0, -0.5);

        ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
        Vector3f offset = meta.shelfOffset();
        pose.translate(offset.x * 0.0625, offset.z * 0.0625, -offset.y * 0.0625);

        pose.translate(0.5, 3 * 0.0625, -3 * 0.0625);
        pose.mulPose(Axis.XP.rotationDegrees(-90F));

        int itemLight = LevelRenderer.getLightColor(access.getLevel(), access.getBlockPos().relative(access.getDirection()));
        meta.renderer().ifPresentOrElse(renderer -> {
            BackpackRenderContext context = new BackpackRenderContext(Scene.ON_SHELF, RenderMode.MODELS_ONLY, pose, source, itemLight, backpack, null, access.getLevel(), partialTick, model -> {
                BakedModelRenderer.drawBakedModel(model, pose, source, itemLight, OverlayTexture.NO_OVERLAY);
            }, 0);
            pose.pushPose();
            renderer.render(context);
            pose.popPose();
        }, () -> {
            BakedModel model = this.getModel(backpack.getBaseModel());
            BakedModelRenderer.drawBakedModel(model, pose, source, itemLight, OverlayTexture.NO_OVERLAY);
        });
    }

    private BakedModel getModel(ModelResourceLocation location)
    {
        return this.itemRenderer.getItemModelShaper().getModelManager().getModel(location);
    }
}
