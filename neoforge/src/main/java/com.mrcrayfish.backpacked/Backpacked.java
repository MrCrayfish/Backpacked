package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.client.ClientBootstrap;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.datagen.BlockTagGen;
import com.mrcrayfish.backpacked.datagen.LootTableGen;
import com.mrcrayfish.backpacked.datagen.RecipeGen;
import com.mrcrayfish.backpacked.enchantment.LootedEnchantment;
import com.mrcrayfish.backpacked.integration.CuriosBackpack;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.util.EnvironmentHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.common.capability.ItemizedCurioCapability;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class Backpacked
{
    public static final EnchantmentCategory ENCHANTMENT_TYPE = EnchantmentCategory.create("backpack", item -> item instanceof BackpackItem);

    private static boolean curiosLoaded = false;

    public Backpacked(IEventBus bus)
    {
        EnvironmentHelper.runOn(Environment.CLIENT, () -> ClientBootstrap::earlyInit);
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onGatherData);
        bus.addListener(this::onRegisterCapabilities);
        NeoForge.EVENT_BUS.addListener(this::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(this::onStartTracking);
        NeoForge.EVENT_BUS.addListener(this::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onDropLoot);
        NeoForge.EVENT_BUS.addListener(this::onInteract);
        curiosLoaded = ModList.get().isLoaded("curios");
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            Bootstrap.init();
            if(isCuriosLoaded()) {
                CuriosApi.registerCurio(ModItems.BACKPACK.get(), new CuriosBackpack());
            }
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

    private void onPlayerClone(PlayerEvent.Clone event)
    {
        if(curiosLoaded)
            return;

        Player oldPlayer = event.getOriginal();
        if(oldPlayer.getInventory() instanceof ExtendedPlayerInventory inventory1 && event.getEntity().getInventory() instanceof ExtendedPlayerInventory inventory2)
        {
            inventory2.copyBackpack(inventory1);
        }
    }

    private void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(curiosLoaded)
            return;

        Player player = event.getEntity();
        if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            ItemStack backpack = inventory.getBackpackItems().get(0);
            if(!backpack.isEmpty() && backpack.getItem() instanceof BackpackItem)
            {
                Network.getPlay().sendToTrackingEntity(() -> player, new MessageUpdateBackpack(player.getId(), backpack, false));
            }
        }
    }

    private void onPlayerTick(TickEvent.PlayerTickEvent event)
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
                Network.getPlay().sendToTrackingEntity(() -> player, new MessageUpdateBackpack(player.getId(), inventory.backpackInventory.get(0), false));
                inventory.backpackArray.set(0, inventory.backpackInventory.get(0));
            }
        }
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

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SHELF.get(), (entity, context) -> new InvWrapper(entity));
    }

    public static boolean isCuriosLoaded()
    {
        return curiosLoaded;
    }
}
