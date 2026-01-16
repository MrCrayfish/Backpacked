package com.mrcrayfish.backpacked.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.backpack.loader.ModelMetaLoader;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackManagementScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackShelfScreen;
import com.mrcrayfish.backpacked.client.particle.FarmhandPlantParticleGroup;
import com.mrcrayfish.backpacked.client.renderer.FirstPersonEffectsRenderer;
import com.mrcrayfish.backpacked.client.renderer.blockentity.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.state.BackpackRenderState;
import com.mrcrayfish.backpacked.client.renderer.special.BackpackItemSpecialRenderer;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModParticleRenderTypes;
import com.mrcrayfish.backpacked.util.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;

/**
 * Author: MrCrayfish
 */
public class ClientHandler implements ClientModInitializer
{
    public static final RenderStateDataKey<BackpackRenderState> BACKPACK_RENDER_STATE_KEY = RenderStateDataKey.create(() -> Utils.id("backpack_render_state").toString());

    @Override
    public void onInitializeClient()
    {
        ClientBootstrap.earlyInit();
        ClientBootstrap.init();
        ModelLoadingPlugin.register(new BackpackedModelLoadingPlugin());
        MenuScreens.register(ModContainers.BACKPACK.get(), BackpackScreen::new);
        MenuScreens.register(ModContainers.MANAGEMENT.get(), BackpackManagementScreen::new);
        MenuScreens.register(ModContainers.BACKPACK_SHELF.get(), BackpackShelfScreen::new);
        BlockEntityRenderers.register(ModBlockEntities.SHELF.get(), ShelfRenderer::new);
        ParticleRendererRegistry.register(ModParticleRenderTypes.FARMHAND_PLANT, FarmhandPlantParticleGroup::new);
        SpecialModelRenderers.ID_MAPPER.put(Utils.id("backpack"), BackpackItemSpecialRenderer.Unbaked.MAP_CODEC);

        // Add backpack layers for player and wandering trader
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if(entityRenderer instanceof WanderingTraderRenderer renderer) {
                registrationHelper.register(new VillagerBackpackLayer(renderer, context.getItemModelResolver()));
            } else if(entityType == EntityType.PLAYER) {
                registrationHelper.register(new BackpackLayer((RenderLayerParent<AvatarRenderState, PlayerModel>) entityRenderer));
            }
        });

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(ModelMetaLoader.ID, new ModelMetaLoader());
        WorldRenderEvents.AFTER_ENTITIES.register(this::afterDrawEntities);
    }

    private void afterDrawEntities(WorldRenderContext context)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.level == null)
            return;

        if(!mc.options.getCameraType().isFirstPerson())
            return;

        PoseStack stack = context.matrices();
        MultiBufferSource source = mc.renderBuffers().bufferSource();
        boolean frozen = mc.level.tickRateManager().isEntityFrozen(mc.player);
        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(!frozen);
        FirstPersonEffectsRenderer.draw(mc.player, stack, source, partialTick);
    }
}
