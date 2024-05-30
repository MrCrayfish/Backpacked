package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.client.ClientBootstrap;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.loader.BackpackLoader;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.datagen.BlockTagGen;
import com.mrcrayfish.backpacked.datagen.LootTableGen;
import com.mrcrayfish.backpacked.datagen.RecipeGen;
import com.mrcrayfish.backpacked.enchantment.LootedEnchantment;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class Backpacked
{
    public static final EnchantmentCategory ENCHANTMENT_TYPE = EnchantmentCategory.create("backpack", item -> item instanceof BackpackItem);

    private static boolean controllableLoaded = false;

    public Backpacked()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientHandler::onRegisterClientLoaders);
            bus.addListener(ClientHandler::onRegisterRenderers);
            bus.addListener(ClientHandler::onAddLayers);
            ClientBootstrap.earlyInit();
        });
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onGatherData);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onDropLoot);
        MinecraftForge.EVENT_BUS.addListener(this::onInteract);
        MinecraftForge.EVENT_BUS.addListener(this::onGetProjectile);
        controllableLoaded = ModList.get().isLoaded("controllable");
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

    private void addReloadListener(AddReloadListenerEvent event)
    {
        event.addListener(new BackpackLoader());
    }

    private void onDropLoot(LivingDropsEvent event)
    {
        if(LootedEnchantment.onDropLoot(event.getDrops(), event.getSource()))
        {
            event.setCanceled(true);
        }
    }

    private void onInteract(PlayerInteractEvent.EntityInteract event)
    {
        if(WanderingTraderEvents.onInteract(event.getTarget(), event.getEntity()))
        {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private void onGetProjectile(LivingGetProjectileEvent event)
    {
        if(event.getProjectileItemStack().isEmpty() && event.getEntity() instanceof Player player)
        {
            ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
            if(backpack.isEmpty())
                return;

            if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MARKSMAN.get(), backpack) <= 0)
                return;

            BackpackInventory inventory = ((BackpackedInventoryAccess) player).backpacked$GetBackpackInventory();
            if(inventory == null)
                return;

            Predicate<ItemStack> predicate = ((ProjectileWeaponItem) event.getProjectileWeaponItemStack().getItem()).getSupportedHeldProjectiles();
            ItemStack projectile = IntStream.range(0, inventory.getContainerSize())
                .mapToObj(inventory::getItem)
                .filter(predicate)
                .findFirst()
                .orElse(ItemStack.EMPTY);

            if(!projectile.isEmpty())
            {
                event.setProjectileItemStack(projectile);
            }
        }
    }

    public static boolean isControllableLoaded()
    {
        return controllableLoaded;
    }
}
