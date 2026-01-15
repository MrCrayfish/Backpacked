package com.mrcrayfish.backpacked.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BackpackItemSpecialRenderer extends BlockEntityWithoutLevelRenderer
{
    public BackpackItemSpecialRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet set)
    {
        super(dispatcher, set);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext display, PoseStack pose, MultiBufferSource source, int light, int overlay)
    {
        renderBackpack(stack, display, pose, source, light);
    }

    public static void renderBackpack(ItemStack stack, ItemDisplayContext display, PoseStack pose, MultiBufferSource source, int light)
    {
        if(stack.is(ModItems.BACKPACK.get()))
        {
            pose.translate(0.5F, 0.5F, 0.5F);
            CosmeticProperties properties = stack.getOrDefault(ModDataComponents.COSMETIC_PROPERTIES.get(), CosmeticProperties.DEFAULT);
            ResourceLocation cosmetic = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
            ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(cosmetic);
            if(backpack != null)
            {
                Minecraft mc = Minecraft.getInstance();
                int ticks = mc.player != null ? mc.player.tickCount : 0;
                float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(false);
                ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
                meta.getItemTransform(display).apply(false, pose);
                meta.renderer().ifPresentOrElse(renderer -> {
                    BackpackRenderContext context = new BackpackRenderContext(Scene.ITEM, RenderMode.MODELS_ONLY, pose, source, light, backpack, null, null, partialTick, model -> {
                        BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
                    }, ticks);
                    pose.pushPose();
                    renderer.render(context);
                    pose.popPose();
                }, () -> {
                    BakedModel model = getModel(backpack.getBaseModel());
                    BakedModelRenderer.drawBakedModel(model, pose, source, light, OverlayTexture.NO_OVERLAY);
                });
            }
        }
    }

    private static BakedModel getModel(ModelResourceLocation location)
    {
        return Minecraft.getInstance().getModelManager().getModel(location);
    }
}
