package com.mrcrayfish.backpacked;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.client.ClientEvents;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import com.mrcrayfish.backpacked.core.ModArgumentTypes;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModBlocks;
import com.mrcrayfish.backpacked.core.ModCommands;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModSounds;
import com.mrcrayfish.backpacked.datagen.BlockTagGen;
import com.mrcrayfish.backpacked.datagen.LootTableGen;
import com.mrcrayfish.backpacked.datagen.RecipeGen;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
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
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class Backpacked
{

    public static final EnchantmentCategory ENCHANTMENT_TYPE = EnchantmentCategory.create("backpack", item -> item instanceof BackpackItem);
    private static boolean controllableLoaded = false;
    private static boolean curiosLoaded = false;
    private static Set<ResourceLocation> bannedItemsList;
    public static final CreativeModeTab TAB = new CreativeModeTab("backpacked")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ModItems.BACKPACK.get());
        }
    }.setEnchantmentCategories(ENCHANTMENT_TYPE);

    public Backpacked()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(ClientHandler::onRegisterLayers);
            bus.addListener(ClientHandler::onRegisterRenderers);
            bus.addListener(ClientHandler::onRegisterKeyMappings);
            bus.addListener(ClientEvents::onTextureStitch);
            bus.register(ClientHandler.getModelInstances());
        });
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onEnqueueIMC);
        bus.addListener(this::onConfigLoad);
        bus.addListener(this::onConfigReload);
        bus.addListener(this::onGatherData);
        bus.addListener(UnlockTracker::register);
        bus.addListener(PickpocketChallenge::register);
        ModArgumentTypes.REGISTER.register(bus);
        ModContainers.REGISTER.register(bus);
        ModEnchantments.REGISTER.register(bus);
        ModItems.REGISTER.register(bus);
        ModBlocks.REGISTER.register(bus);
        ModBlockEntities.REGISTER.register(bus);
        ModSounds.REGISTER.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModCommands());
        MinecraftForge.EVENT_BUS.register(new WanderingTraderEvents());
        controllableLoaded = ModList.get().isLoaded("controllable");
        curiosLoaded = ModList.get().isLoaded("curios");
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        Network.init();
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
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new LootTableGen(generator));
        generator.addProvider(event.includeServer(), new RecipeGen(generator));
        generator.addProvider(event.includeServer(), new BlockTagGen(generator, existingFileHelper));
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

    public static ItemStack getBackStack(Player player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        if(Backpacked.isCuriosLoaded())
        {
            backpack.set(Curios.getBackStack(player));
        }
        if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            ItemStack stack = inventory.getBackpackItems().get(0);
            if(!stack.isEmpty())
            {
                backpack.set(stack);
            }
        }
        return backpack.get();
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

    public static boolean setBackpackStack(Player player, ItemStack stack)
    {
        if(!(stack.getItem() instanceof BackpackItem) && !stack.isEmpty())
            return false;

        if(Backpacked.isCuriosLoaded())
        {
            Curios.setBackpackStack(player, stack);
            return true;
        }
        else if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            inventory.getBackpackItems().set(0, stack.copy());
            return true;
        }
        return false;
    }

    private void onConfigLoad(ModConfigEvent.Loading event)
    {
        ModConfig config = event.getConfig();
        if(config.getType() == ModConfig.Type.SERVER && config.getModId().equals(Reference.MOD_ID))
        {
            updateBannedItemsList();
        }
    }

    private void onConfigReload(ModConfigEvent.Reloading event)
    {
        ModConfig config = event.getConfig();
        if(config.getType() == ModConfig.Type.SERVER && config.getModId().equals(Reference.MOD_ID))
        {
            updateBannedItemsList();
        }
    }

    public static void updateBannedItemsList()
    {
        bannedItemsList = ImmutableSet.copyOf(Config.SERVER.bannedItems.get().stream().map(ResourceLocation::new).collect(Collectors.toSet()));
    }

    public static Set<ResourceLocation> getBannedItemsList()
    {
        return bannedItemsList;
    }
}
