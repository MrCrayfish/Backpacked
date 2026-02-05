package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.client.ClientBootstrap;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import com.mrcrayfish.backpacked.common.backpack.loader.BackpackLoader;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.datagen.LootTableGen;
import com.mrcrayfish.backpacked.datagen.RecipeGen;
import net.minecraft.core.NonNullList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Author: MrCrayfish
 */
@Mod(Constants.MOD_ID)
public class Backpacked
{
    public Backpacked()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientHandler::onRegisterClientLoaders);
            bus.addListener(ClientHandler::onRegisterRenderers);
            bus.addListener(ClientHandler::onAddLayers);
            bus.addListener(ClientHandler::onRegisterAdditional);
            bus.addListener(ClientHandler::onFindPacks);
            ClientBootstrap.earlyInit();
        });
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onGatherData);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onEntityDropLoot);
        MinecraftForge.EVENT_BUS.addListener(this::onInteract);
        MinecraftForge.EVENT_BUS.addListener(this::onGetProjectile);
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListener);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onLivingDrops);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ClientBootstrap.init();
            ClientHandler.init();
        });
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        event.enqueueWork(Bootstrap::init);
    }

    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        generator.addProvider(event.includeServer(), new LootTableGen(packOutput));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput));
    }

    private void addReloadListener(AddReloadListenerEvent event)
    {
        event.addListener(new BackpackLoader());
    }

    private void onEntityDropLoot(LivingDropsEvent event)
    {
        if(event.getSource().getEntity() instanceof ServerPlayer player)
        {
            AugmentHandler.onLootDroppedByEntity(event.getDrops(), player);
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
