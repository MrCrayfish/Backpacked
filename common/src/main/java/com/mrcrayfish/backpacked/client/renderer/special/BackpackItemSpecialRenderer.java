package com.mrcrayfish.backpacked.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.StandaloneModels;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.renderer.StandaloneModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class BackpackItemSpecialRenderer implements SpecialModelRenderer<BackpackItemSpecialRenderer.CosmeticData>
{
    private static final BackpackItemSpecialRenderer INSTANCE = new BackpackItemSpecialRenderer();

    public static ItemDisplayContext contextWhenExtracting = ItemDisplayContext.NONE;

    @Override
    public void submit(@Nullable CosmeticData data, PoseStack pose, SubmitNodeCollector collector, int light, int overlay, boolean unknown1, int unknown2)
    {
        if(data == null)
            return;

        pose.translate(0.5, 0.5, 0.5);
        data.display.getTransform(data.context).apply(false, pose.last());
        pose.translate(0.5, 0.5, 0.5);

        BackpackRenderer renderer = data.renderer;
        if(renderer != null)
        {
            BackpackRenderContext context = new BackpackRenderContext(
                Scene.ITEM,
                RenderMode.MODELS_ONLY,
                pose,
                data.baseModel,
                data.strapsModel,
                null,
                null,
                0,
                light,
                data.tickCount,
                data.partialTick,
                model -> {
                    pose.pushPose();
                    pose.translate(-0.5F, -0.5F, -0.5F);
                    StandaloneModelRenderer.submitDraw(collector, model, pose, 1.0F, 1.0F, 1.0F, light, overlay);
                    pose.popPose();
                });
            renderer.render(context);
        }
        else
        {
            FrameworkModelResource<FrameworkBakedModel> resource = StandaloneModels.getResource(data.baseModel);
            if(resource != null)
            {
                FrameworkBakedModel model = resource.getModel();
                if(model != null)
                {
                    pose.pushPose();
                    pose.translate(-0.5F, -0.5F, -0.5F);
                    StandaloneModelRenderer.submitDraw(collector, model, pose, 1.0F, 1.0F, 1.0F, light, overlay);
                    pose.popPose();
                }
            }
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer)
    {
        // Restores the old offset for items rendered on the ground
        // This means the backpack will match the Blockbench preview
        float offset = 0.3125F;
        consumer.accept(new Vector3f(0, offset, 0));
    }

    @Override
    public @Nullable CosmeticData extractArgument(ItemStack stack)
    {
        CosmeticProperties properties = stack.getOrDefault(ModDataComponents.COSMETIC_PROPERTIES.get(), CosmeticProperties.DEFAULT);
        Identifier cosmeticId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
        ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(cosmeticId);
        if(backpack == null)
            return null;

        // TODO This is safe for now since it runs on main thread, check back on this in a future update
        Minecraft minecraft = Minecraft.getInstance();
        float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        int tickCount = minecraft.player != null ? minecraft.player.tickCount : 0;

        ModelMeta meta = backpack.getModelMeta();
        BackpackRenderer renderer = meta.renderer().orElse(null);
        return new CosmeticData(renderer, backpack.getBaseModel(), backpack.getStrapsModel(), meta.display(), contextWhenExtracting, tickCount, partialTick);
    }

    public record CosmeticData(BackpackRenderer renderer, Identifier baseModel, Identifier strapsModel, ItemTransforms display, ItemDisplayContext context, int tickCount, float partialTick) { }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<BackpackItemSpecialRenderer.CosmeticData>
    {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<BackpackItemSpecialRenderer.CosmeticData> bake(BakingContext bakingContext)
        {
            return INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<BackpackItemSpecialRenderer.CosmeticData>> type()
        {
            return MAP_CODEC;
        }
    }
}
