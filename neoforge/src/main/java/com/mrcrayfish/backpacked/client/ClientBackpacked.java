package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackManagementScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.client.backpack.loader.ModelMetaLoader;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.packs.AddonRepositorySource;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import net.minecraft.client.Minecraft;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Path;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientBackpacked
{
    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ClientBootstrap.init();
            if(!FMLLoader.isProduction()) {
                NeoForge.EVENT_BUS.register(new PickpocketDebugRenderer());
            }
        });
    }

    @SubscribeEvent
    private static void onRegisterClientLoaders(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(new ModelMetaLoader());
    }

    @SubscribeEvent
    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModContainers.BACKPACK.get(), BackpackScreen::new);
        event.register(ModContainers.MANAGEMENT.get(), BackpackManagementScreen::new);
    }

    @SubscribeEvent
    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF.get(), ShelfRenderer::new);
    }

    @SubscribeEvent
    private static void onAddLayers(EntityRenderersEvent.AddLayers event)
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

    @SubscribeEvent
    private static void onRegisterClientLoaders(ModelEvent.RegisterAdditional event)
    {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> models = manager.listResources("models/backpacked", location -> location.getPath().endsWith(".json"));
        models.forEach((key, resource) -> {
            String path = key.getPath().substring("models/".length(), key.getPath().length() - ".json".length());
            ModelResourceLocation location = FrameworkClientAPI.createModelResourceLocation(key.getNamespace(), path);
            event.register(location);
        });
    }

    @SubscribeEvent
    private static void onFindPacks(AddPackFindersEvent event)
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
}
