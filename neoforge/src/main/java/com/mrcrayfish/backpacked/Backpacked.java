package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.client.ClientBootstrap;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import com.mrcrayfish.backpacked.common.backpack.loader.BackpackLoader;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.core.ModPointOfInterests;
import com.mrcrayfish.backpacked.datagen.BlockTagGen;
import com.mrcrayfish.backpacked.datagen.LootTableGen;
import com.mrcrayfish.backpacked.datagen.RecipeGen;
import com.mrcrayfish.backpacked.integration.YoureInGraveDangerSupport;
import com.mrcrayfish.framework.api.Environment;
import com.mrcrayfish.framework.api.util.TaskRunner;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.concurrent.CompletableFuture;

// TODO clean up this class

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class Backpacked
{
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Constants.MOD_ID);
    public static final DeferredHolder<PoiType, PoiType> SHELF = POI_TYPES.register("backpack_shelf", ModPointOfInterests.BACKPACK_SHELF::value);

    public Backpacked(IEventBus bus)
    {
        TaskRunner.runIf(Environment.CLIENT, () -> ClientBootstrap::earlyInit);
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onGatherData);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onEntityDropLoot);
        NeoForge.EVENT_BUS.addListener(this::onInteract);
        NeoForge.EVENT_BUS.addListener(this::onGetProjectile);
        NeoForge.EVENT_BUS.addListener(this::addReloadListener);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBlockDropLoot);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onLivingDrops);
        POI_TYPES.register(bus);

        if(ModList.get().isLoaded("yigd"))
        {
            YoureInGraveDangerSupport.init();
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(Bootstrap::init);
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

    private void onEntityDropLoot(LivingDropsEvent event)
    {
        if(event.getSource().getEntity() instanceof ServerPlayer player)
        {
            AugmentHandler.onLootDroppedByEntity(event.getDrops(), player);
        }
    }

    private void onBlockDropLoot(BlockDropsEvent event)
    {
        Entity breaker = event.getBreaker();
        if(breaker instanceof Player player)
        {
            AugmentHandler.onLootDroppedByBlock(event.getDrops(), player);
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
        if(event.getEntity() instanceof Player player)
        {
            ItemStack ammo = AugmentHandler.locateAmmunition(player, event.getProjectileWeaponItemStack(), event.getProjectileItemStack());
            if(!ammo.isEmpty())
            {
                event.setProjectileItemStack(ammo);
            }
        }
    }

    private void onLivingDrops(LivingDropsEvent event)
    {
        if(event.getEntity() instanceof ServerPlayer player)
        {
            if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))
                return;

            if(Config.BACKPACK.equipable.keepOnDeath.get())
                return;

            NonNullList<ItemStack> removed = BackpackHelper.removeAllBackpacks(player);
            for(int index = 0; index < removed.size(); index++)
            {
                ItemStack stack = removed.get(index);
                if(!stack.isEmpty())
                {
                    RecallAugment augment = BackpackHelper.findAugment(stack, ModAugmentTypes.RECALL.get());
                    if(augment != null && AugmentHandler.recallBackpack(player, index, stack, augment)) {
                        continue;
                    }
                    event.getDrops().add(this.createDrop(player, stack));
                }
            }
        }
    }

    private ItemEntity createDrop(Player player, ItemStack stack)
    {
        float deltaX = player.getRandom().nextFloat() * 0.5F;
        float deltaZ = player.getRandom().nextFloat() * (Mth.PI * 2);
        ItemEntity entity = new ItemEntity(player.level(), player.getX(), player.getEyeY() - 0.3F, player.getZ(), stack.copyAndClear());
        entity.setDeltaMovement(-Mth.sin(deltaZ) * deltaX, 0.2, Mth.cos(deltaZ) * deltaX);
        entity.setPickUpDelay(40);
        return entity;
    }
}
