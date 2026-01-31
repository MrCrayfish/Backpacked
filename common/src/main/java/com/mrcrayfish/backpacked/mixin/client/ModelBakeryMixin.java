package com.mrcrayfish.backpacked.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
@Mixin(ModelBakery.class)
public class ModelBakeryMixin
{
    @Unique
    private static final Predicate<ResourceLocation> BACKPACKED_MODEL_TEST = location -> location.getPath().startsWith("models/backpacked");

    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void backpacked$OnLoadModels(BlockColors colors, ProfilerFiller filler, Map<ResourceLocation, BlockModel> models, Map<ResourceLocation, List<ModelBakery.LoadedJson>> data, CallbackInfo ci)
    {
        // Load the json models from the backpack directory
        ModelBakery bakery = (ModelBakery) (Object) this;
        models.forEach((key, value) -> {
            if(BACKPACKED_MODEL_TEST.test(key)) {
                String path = key.getPath().substring("models/".length(), key.getPath().length() - ".json".length());
                ResourceLocation location = new ResourceLocation(key.getNamespace(), path);
                UnbakedModel model = bakery.getModel(location);
                this.unbakedCache.put(location, model);
                this.topLevelModels.put(location, model);
            }
        });
    }
}
