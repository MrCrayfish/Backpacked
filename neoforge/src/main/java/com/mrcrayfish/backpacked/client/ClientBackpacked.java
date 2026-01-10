package com.mrcrayfish.backpacked.client;

import com.google.common.reflect.TypeToken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.backpack.loader.ModelMetaLoader;
import com.mrcrayfish.backpacked.client.gui.pip.GuiBackpackRenderState;
import com.mrcrayfish.backpacked.client.gui.pip.GuiBackpackRenderer;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackManagementScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackShelfScreen;
import com.mrcrayfish.backpacked.client.particle.FarmhandPlantParticleGroup;
import com.mrcrayfish.backpacked.client.renderer.FirstPersonEffectsRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.LevelDataState;
import com.mrcrayfish.backpacked.client.renderer.backpack.LivingEntityDataState;
import com.mrcrayfish.backpacked.client.renderer.blockentity.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.state.BackpackRenderState;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModParticleRenderTypes;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.packs.AddonRepositorySource;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import com.mrcrayfish.framework.api.client.model.NeoForgeModelResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class ClientBackpacked
{
    public static final ContextKey<Boolean> WEARING_BACKPACK_KEY = new ContextKey<>(Utils.id("wearing_backpack"));
    public static final ContextKey<BackpackRenderState> BACKPACK_RENDER_STATE_KEY = new ContextKey<>(Utils.id("backpack_render_state"));

    public ClientBackpacked(IEventBus bus)
    {
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onRegisterClientLoaders);
        bus.addListener(this::onRegisterMenuScreens);
        bus.addListener(this::onRegisterRenderers);
        bus.addListener(this::onAddLayers);
        bus.addListener(this::onRegisterAdditionalModels);
        bus.addListener(this::onFindPacks);
        bus.addListener(this::onRegisterModifyRenderState);
        bus.addListener(this::onRegisterParticleGroup);
        bus.addListener(this::onRegisterPipRenderers);
        NeoForge.EVENT_BUS.addListener(this::onRenderLevelStage);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ClientBootstrap.init();
            if(!FMLEnvironment.isProduction()) {
                NeoForge.EVENT_BUS.register(new PickpocketDebugRenderer());
            }
        });
    }

    private void onRegisterClientLoaders(AddClientReloadListenersEvent event)
    {
        event.addListener(ModelMetaLoader.ID, new ModelMetaLoader());
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
        addBackpackLayer(event.getPlayerRenderer(PlayerModelType.WIDE));
        addBackpackLayer(event.getPlayerRenderer(PlayerModelType.SLIM));

        EntityRenderer<WanderingTrader, ?> renderer = event.getRenderer(EntityType.WANDERING_TRADER);
        if(renderer instanceof WanderingTraderRenderer traderRenderer)
        {
            traderRenderer.addLayer(new VillagerBackpackLayer(traderRenderer, event.getContext().getItemModelResolver()));
        }
    }

    private static void addBackpackLayer(@Nullable AvatarRenderer<AbstractClientPlayer> renderer)
    {
        if(renderer != null)
        {
            renderer.addLayer(new BackpackLayer(renderer));
        }
    }

    private void onRegisterAdditionalModels(ModelEvent.RegisterStandalone event)
    {
        Map<Identifier, FrameworkModelResource<FrameworkBakedModel>> loadedModels = new HashMap<>();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Map<Identifier, Resource> models = manager.listResources("models/backpacked", location -> location.getPath().endsWith(".json"));
        models.forEach((id, resource) -> {
            String path = id.getPath().substring("models/".length(), id.getPath().length() - ".json".length());
            Identifier standaloneId = Identifier.fromNamespaceAndPath(id.getNamespace(), path);
            FrameworkModelResource<FrameworkBakedModel> modelResource = FrameworkModelResource.create(standaloneId);
            var key = ((NeoForgeModelResource<FrameworkBakedModel>) modelResource).standaloneKey();
            var baker = ((NeoForgeModelResource<FrameworkBakedModel>) modelResource).unbakedModel();
            event.register(key, baker);
            loadedModels.put(standaloneId, modelResource);
        });
        StandaloneModels.init(loadedModels);
    }

    private void onFindPacks(AddPackFindersEvent event)
    {
        // Search the resource packs folder for any backpacked addons. This makes it compatible with CurseForge modpacks.
        if(event.getPackType() == PackType.SERVER_DATA)
        {
            Path gameDir = FMLLoader.getCurrent().getGameDir();
            Path addonDir = gameDir.resolve("resourcepacks");
            DirectoryValidator directoryValidator = LevelStorageSource.parseValidator(gameDir.resolve("allowed_symlinks.txt"));
            event.addRepositorySource(new AddonRepositorySource(addonDir, PackType.SERVER_DATA, PackSource.FEATURE, directoryValidator));
        }
    }

    private void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.level == null)
            return;

        if(!mc.options.getCameraType().isFirstPerson())
            return;

        PoseStack stack = event.getPoseStack();
        MultiBufferSource source = mc.renderBuffers().bufferSource();
        boolean frozen = mc.level.tickRateManager().isEntityFrozen(mc.player);
        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(!frozen); // TODO 1.21.11 test
        FirstPersonEffectsRenderer.draw(mc.player, stack, source, partialTick);
    }

    private void onRegisterModifyRenderState(RegisterRenderStateModifiersEvent event)
    {
        // Appends the wearing backpack state of a wandering trader
        event.registerEntityModifier(WanderingTraderRenderer.class, (trader, state) -> {
            TraderPickpocketing.get(trader).ifPresent(data -> {
                state.setRenderData(WEARING_BACKPACK_KEY, data.isBackpackEquipped());
            });
        });

        // Appends backpack data to players so the layer can use to submit draws
        event.registerEntityModifier(new TypeToken<AvatarRenderer<?>>() {}, (entity, state) -> {
            if(entity instanceof AbstractClientPlayer player) {
                CosmeticProperties properties = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player).orElse(null);
                if(properties == null)
                    return;

                Identifier cosmeticId = properties.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic());
                ClientBackpack backpack = ClientRegistry.instance().getBackpackOrDefault(cosmeticId);
                if(backpack == null)
                    return;

                float partialTick = state.ageInTicks - (int) state.ageInTicks; // Inferred
                BackpackRenderState backpackRenderState = new BackpackRenderState();
                ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
                backpackRenderState.bobbing = meta.bobbing();
                backpackRenderState.renderer = meta.renderer().orElse(null);
                backpackRenderState.baseModel = backpack.getBaseModel();
                backpackRenderState.strapsModel = backpack.getStrapsModel();
                backpackRenderState.entityData = LivingEntityDataState.create(player, partialTick);
                backpackRenderState.levelData = LevelDataState.create(player.level(), partialTick);
                backpackRenderState.visible = Services.BACKPACK.isBackpackVisible(player);
                backpackRenderState.cosmeticProperties = properties;
                backpackRenderState.horizontalDelta = player.getDeltaMovement().horizontalDistance();
                state.setRenderData(BACKPACK_RENDER_STATE_KEY, backpackRenderState);
            }
        });
    }

    private void onRegisterParticleGroup(RegisterParticleGroupsEvent event)
    {
        event.register(ModParticleRenderTypes.FARMHAND_PLANT, FarmhandPlantParticleGroup::new);
    }

    private void onRegisterPipRenderers(RegisterPictureInPictureRenderersEvent event)
    {
        event.register(GuiBackpackRenderState.class, GuiBackpackRenderer::new);
    }
}
