package com.mrcrayfish.backpacked.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.backpack.loader.ModelMetaLoader;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackManagementScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackShelfScreen;
import com.mrcrayfish.backpacked.client.renderer.FirstPersonEffectsRenderer;
import com.mrcrayfish.backpacked.client.renderer.blockentity.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.packs.AddonRepositorySource;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Path;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class ClientBackpacked
{
    public ClientBackpacked(IEventBus bus)
    {
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onRegisterClientLoaders);
        bus.addListener(this::onRegisterMenuScreens);
        bus.addListener(this::onRegisterRenderers);
        bus.addListener(this::onAddLayers);
        bus.addListener(this::onRegisterAdditionalModels);
        bus.addListener(this::onFindPacks);
        NeoForge.EVENT_BUS.addListener(this::onRenderLevelStage);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ClientBootstrap.init();
            if(!FMLLoader.isProduction()) {
                NeoForge.EVENT_BUS.register(new PickpocketDebugRenderer());
            }
        });
    }

    private void onRegisterClientLoaders(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(new ModelMetaLoader());
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModContainers.BACKPACK.get(), BackpackScreen::new);
        event.register(ModContainers.MANAGEMENT.get(), BackpackManagementScreen::new);
        event.register(ModContainers.BACKPACK_SHELF.get(), BackpackShelfScreen::new);
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF.get(), ShelfRenderer::new);
    }

    private void onAddLayers(EntityRenderersEvent.AddLayers event)
    {
        addBackpackLayer(event.getSkin(PlayerSkin.Model.WIDE), event.getContext().getItemRenderer());
        addBackpackLayer(event.getSkin(PlayerSkin.Model.SLIM), event.getContext().getItemRenderer());

        EntityRenderer<?> renderer = event.getRenderer(EntityType.WANDERING_TRADER);
        if(renderer instanceof WanderingTraderRenderer traderRenderer)
        {
            traderRenderer.addLayer(new VillagerBackpackLayer<>(traderRenderer, event.getContext().getItemRenderer()));
        }
    }

    private static void addBackpackLayer(EntityRenderer<?> renderer, ItemRenderer itemRenderer)
    {
        if(renderer instanceof PlayerRenderer playerRenderer)
        {
            playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, itemRenderer));
        }
    }

    private void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event)
    {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> models = manager.listResources("models/backpacked", location -> location.getPath().endsWith(".json"));
        models.forEach((key, resource) -> {
            String path = key.getPath().substring("models/".length(), key.getPath().length() - ".json".length());
            ModelResourceLocation location = FrameworkClientAPI.createModelResourceLocation(key.getNamespace(), path);
            event.register(location);
        });
    }

    private void onFindPacks(AddPackFindersEvent event)
    {
        // Search the resource packs folder for any backpacked addons. This makes it compatible with CurseForge modpacks.
        if(event.getPackType() == PackType.SERVER_DATA)
        {
            Path gameDir = FMLLoader.getGamePath();
            Path addonDir = gameDir.resolve("resourcepacks");
            DirectoryValidator directoryValidator = LevelStorageSource.parseValidator(gameDir.resolve("allowed_symlinks.txt"));
            event.addRepositorySource(new AddonRepositorySource(addonDir, PackType.SERVER_DATA, PackSource.FEATURE, directoryValidator));
        }
    }

    private void onRenderLevelStage(RenderLevelStageEvent event)
    {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES)
            return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.level == null)
            return;

        if(!mc.options.getCameraType().isFirstPerson())
            return;

        PoseStack stack = event.getPoseStack();
        MultiBufferSource source = mc.renderBuffers().bufferSource();
        boolean frozen = mc.level.tickRateManager().isEntityFrozen(mc.player);
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(!frozen);
        FirstPersonEffectsRenderer.draw(mc.player, stack, source, partialTick);
    }
}
