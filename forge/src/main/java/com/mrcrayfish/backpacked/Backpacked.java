package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.client.ClientBootstrap;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.loader.BackpackLoader;
import com.mrcrayfish.backpacked.common.backpack.BackpackLoader;
import com.mrcrayfish.backpacked.data.pickpocket.ForgePickpocketChallenge;
import com.mrcrayfish.backpacked.data.tracker.ForgeUnlockTracker;
import com.mrcrayfish.backpacked.datagen.BlockTagGen;
import com.mrcrayfish.backpacked.datagen.LootTableGen;
import com.mrcrayfish.backpacked.datagen.RecipeGen;
import com.mrcrayfish.backpacked.enchantment.LootedEnchantment;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class Backpacked
{
    public static final EnchantmentCategory ENCHANTMENT_TYPE = EnchantmentCategory.create("backpack", item -> item instanceof BackpackItem);

    private static boolean controllableLoaded = false;
    private static boolean curiosLoaded = false;

    public Backpacked()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientHandler::onRegisterClientLoaders);
            bus.addListener(ClientHandler::onRegisterLayerDefinitions);
            bus.addListener(ClientHandler::onRegisterRenderers);
            bus.addListener(ClientHandler::onAddLayers);
            ClientBootstrap.earlyInit();
        });
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onGatherData);
        MinecraftForge.EVENT_BUS.register(this);
        controllableLoaded = ModList.get().isLoaded("controllable");
        curiosLoaded = ModList.get().isLoaded("curios");
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(Bootstrap::init);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ClientBootstrap.init();
            ClientHandler.init();
        });
    }

    private void onGatherData(GatherDataEvent event)
    {
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new LootTableGen(packOutput));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput));
        generator.addProvider(event.includeServer(), new BlockTagGen(packOutput, lookupProvider, existingFileHelper));
    }

    @SubscribeEvent
    public void addReloadListener(AddReloadListenerEvent event)
    {
        event.addListener(new BackpackLoader());
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        if(curiosLoaded)
            return;

        Player oldPlayer = event.getOriginal();
        if(oldPlayer.getInventory() instanceof ExtendedPlayerInventory inventory1 && event.getEntity().getInventory() instanceof ExtendedPlayerInventory inventory2)
        {
            inventory2.copyBackpack(inventory1);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(curiosLoaded)
            return;

        Player player = event.getEntity();
        if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            ItemStack backpack = inventory.getBackpackItems().get(0);
            if(!backpack.isEmpty() && backpack.getItem() instanceof BackpackItem)
            {
                Network.getPlay().sendToTracking(() -> player, new MessageUpdateBackpack(player.getId(), backpack));
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
        if(!player.level().isClientSide && player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            if(!inventory.backpackArray.get(0).equals(inventory.backpackInventory.get(0)))
            {
                Network.getPlay().sendToTracking(() -> player, new MessageUpdateBackpack(player.getId(), inventory.backpackInventory.get(0)));
                inventory.backpackArray.set(0, inventory.backpackInventory.get(0));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDropLoot(LivingDropsEvent event)
    {
        if(LootedEnchantment.onDropLoot(event.getDrops(), event.getSource()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event)
    {
        if(WanderingTraderEvents.onInteract(event.getTarget(), event.getEntity()))
        {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
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
}
