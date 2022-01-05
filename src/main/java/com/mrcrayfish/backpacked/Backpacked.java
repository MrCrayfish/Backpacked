package com.mrcrayfish.backpacked;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.command.arguments.BackpackArgument;
import com.mrcrayfish.backpacked.core.ModCommands;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.datagen.RecipeGen;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.server.command.CurioArgumentType;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class Backpacked
{
    public static final ResourceLocation EMPTY_BACKPACK_SLOT = new ResourceLocation(Reference.MOD_ID, "item/empty_backpack_slot");

    private static boolean controllableLoaded = false;
    private static boolean curiosLoaded = false;
    private static List<ResourceLocation> bannedItemsList;

    public Backpacked()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::onTextureStitch));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.register(ClientHandler.getModelInstances()));
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onEnqueueIMC);
        bus.addListener(this::onConfigLoad);
        bus.addListener(this::onConfigReload);
        bus.addListener(this::onGatherData);
        bus.addListener(UnlockTracker::register);
        ModContainers.REGISTER.register(bus);
        ModItems.REGISTER.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModCommands());
        controllableLoaded = ModList.get().isLoaded("controllable");
        curiosLoaded = ModList.get().isLoaded("curios");
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        Network.init();
        ArgumentTypes.register("backpacked:backpack", BackpackArgument.class, new EmptyArgumentSerializer<>(BackpackArgument::backpacks));
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        ClientHandler.setup();
    }

    private void onEnqueueIMC(InterModEnqueueEvent event)
    {
        if(!curiosLoaded)
            return;

        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BACK.getMessageBuilder().build());
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new RecipeGen(generator));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerRenderScreen(ContainerScreenEvent.DrawBackground event)
    {
        if(curiosLoaded)
            return;

        AbstractContainerScreen<?> screen = event.getContainerScreen();
        if(screen instanceof InventoryScreen inventory)
        {
            int left = inventory.getGuiLeft();
            int top = inventory.getGuiTop();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
            Screen.blit(event.getPoseStack(), left + 76, top + 43, 7, 7, 18, 18, 256, 256);
        }
        else if(screen instanceof CreativeModeInventoryScreen inventory)
        {
            if(inventory.getSelectedTab() == CreativeModeTab.TAB_INVENTORY.getId())
            {
                int left = inventory.getGuiLeft();
                int top = inventory.getGuiTop();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
                Screen.blit(event.getPoseStack(), left + 126, top + 19, 7, 7, 18, 18, 256, 256);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if(event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
        {
            event.addSprite(EMPTY_BACKPACK_SLOT);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        if(curiosLoaded)
            return;

        Player oldPlayer = event.getOriginal();
        if(oldPlayer.getInventory() instanceof ExtendedPlayerInventory inventory1 && event.getPlayer().getInventory() instanceof ExtendedPlayerInventory inventory2)
        {
            inventory2.copyBackpack(inventory1);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(curiosLoaded)
            return;

        Player player = event.getPlayer();
        if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            ItemStack backpack = inventory.getBackpackItems().get(0);
            if(!backpack.isEmpty() && backpack.getItem() instanceof BackpackItem)
            {
                Network.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageUpdateBackpack(player.getId(), backpack));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(curiosLoaded)
            return;

        if(event.phase != TickEvent.Phase.START)
            return;

        Player player = event.player;
        if(!player.level.isClientSide && player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            if(!inventory.backpackArray.get(0).equals(inventory.backpackInventory.get(0)))
            {
                Network.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageUpdateBackpack(player.getId(), inventory.backpackInventory.get(0)));
                inventory.backpackArray.set(0, inventory.backpackInventory.get(0));
            }
        }
    }

    public static boolean isControllableLoaded()
    {
        return controllableLoaded;
    }

    public static boolean isCuriosLoaded()
    {
        return curiosLoaded;
    }

    public static ItemStack getBackpackStack(Player player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        if(Backpacked.isCuriosLoaded())
        {
            backpack.set(Curios.getBackpackStack(player));
        }
        if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            ItemStack stack = inventory.getBackpackItems().get(0);
            if(stack.getItem() instanceof BackpackItem)
            {
                backpack.set(stack);
            }
        }
        return backpack.get();
    }

    private void onConfigLoad(ModConfigEvent.Loading event)
    {
        ModConfig config = event.getConfig();
        if(config.getType() == ModConfig.Type.SERVER && config.getModId().equals(Reference.MOD_ID))
        {
            this.updateBannedItemsList();
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading event)
    {
        ModConfig config = event.getConfig();
        if(config.getType() == ModConfig.Type.SERVER && config.getModId().equals(Reference.MOD_ID))
        {
            this.updateBannedItemsList();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerLogin(ClientPlayerNetworkEvent.LoggedInEvent event)
    {
        this.updateBannedItemsList();
    }

    private void updateBannedItemsList()
    {
        bannedItemsList = ImmutableList.copyOf(Config.SERVER.bannedItems.get().stream().map(ResourceLocation::new).collect(Collectors.toList()));
    }

    public static List<ResourceLocation> getBannedItemsList()
    {
        return bannedItemsList;
    }
}
