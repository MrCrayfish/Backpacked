package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.client.ClientBootstrap;
import com.mrcrayfish.backpacked.common.EnchantmentHandler;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.loader.BackpackLoader;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModTags;
import com.mrcrayfish.backpacked.datagen.BlockTagGen;
import com.mrcrayfish.backpacked.datagen.LootTableGen;
import com.mrcrayfish.backpacked.datagen.RecipeGen;
import com.mrcrayfish.backpacked.integration.BackpackAccessory;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.util.TaskRunner;
import io.wispforest.accessories.api.AccessoriesAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.IntStream;

// TODO clean up this class

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class Backpacked
{
    public Backpacked(IEventBus bus)
    {
        TaskRunner.runIf(Environment.CLIENT, () -> ClientBootstrap::earlyInit);
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onGatherData);
        bus.addListener(this::onRegisterCapabilities);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onDropLoot);
        NeoForge.EVENT_BUS.addListener(this::onInteract);
        NeoForge.EVENT_BUS.addListener(this::onGetProjectile);
        NeoForge.EVENT_BUS.addListener(this::addReloadListener);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBlockDrops);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            Bootstrap.init();
            AccessoriesAPI.registerAccessory(ModItems.BACKPACK.get(), new BackpackAccessory());
        });
    }

    private void onGatherData(GatherDataEvent event)
    {
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new LootTableGen(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new BlockTagGen(packOutput, lookupProvider, existingFileHelper));
    }

    private void addReloadListener(AddReloadListenerEvent event)
    {
        event.addListener(new BackpackLoader(event.getServerResources().getRegistryLookup()));
    }

    private void onDropLoot(LivingDropsEvent event)
    {
        if(EnchantmentHandler.onDropLoot(event.getDrops(), event.getSource()))
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

    private void onGetProjectile(LivingGetProjectileEvent event)
    {
        if(event.getProjectileItemStack().isEmpty() && event.getEntity() instanceof Player player)
        {
            ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
            if(backpack.isEmpty())
                return;

            HolderLookup<Enchantment> lookup = player.level().holderLookup(Registries.ENCHANTMENT);
            if(backpack.getEnchantmentLevel(lookup.getOrThrow(ModEnchantments.MARKSMAN)) <= 0)
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

    private void onBlockDrops(BlockDropsEvent event)
    {
        BlockState state = event.getState();
        Entity breaker = event.getBreaker();
        if(state.is(ModTags.Blocks.FUNNELLING) && breaker instanceof ServerPlayer serverPlayer)
        {
            if(EnchantmentHandler.onBreakBlock(state, event.getLevel(), event.getPos(), event.getBlockEntity(), serverPlayer, event.getTool()))
            {
                event.setCanceled(true);
                if(event.getDroppedExperience() > 0)
                {
                    state.getBlock().popExperience(event.getLevel(), event.getPos(), event.getDroppedExperience());
                }
            }
        }
    }
}
