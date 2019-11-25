package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.entity.player.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.ExtendedPlayerContainer;
import com.mrcrayfish.backpacked.network.PacketHandler;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import com.mrcrayfish.backpacked.proxy.ClientProxy;
import com.mrcrayfish.backpacked.proxy.CommonProxy;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Author: MrCrayfish
 */
@Mod(Reference.MOD_ID)
public class Backpacked
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public static final CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    private static Field inventoryField;
    private static Field containerField;

    public Backpacked()
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTextureStitch);
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        PacketHandler.init();
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        PROXY.setupClient();
    }

    /* Hooks into PlayerEntity constructor to allow manipulation of fields. Linked via ASM, do not remove! */
    public static void onPlayerInit(PlayerEntity player)
    {
        Backpacked.patchInventory(player);
    }

    private static void patchInventory(PlayerEntity player)
    {
        if(inventoryField == null)
        {
            inventoryField = getFieldAndSetAccessible(PlayerEntity.class, "field_71071_by");
        }
        if(containerField == null)
        {
            containerField = getFieldAndSetAccessible(PlayerEntity.class, "field_71069_bz");
        }
        try
        {
            ExtendedPlayerInventory inventory = new ExtendedPlayerInventory(player);
            inventoryField.set(player, inventory);

            ExtendedPlayerContainer container = new ExtendedPlayerContainer(inventory, !player.world.isRemote, player);
            containerField.set(player, container);
            player.openContainer = container;
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private static Field getFieldAndSetAccessible(Class clazz, String obfName)
    {
        Field field = ObfuscationReflectionHelper.findField(clazz, obfName);
        field.setAccessible(true);

        try
        {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }
        catch(IllegalAccessException | NoSuchFieldException e)
        {
            e.printStackTrace();
        }

        return field;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerRenderScreen(GuiContainerEvent.DrawBackground event)
    {
        ContainerScreen screen = event.getGuiContainer();
        if(screen instanceof InventoryScreen)
        {
            InventoryScreen inventoryScreen = (InventoryScreen) screen;
            int left = inventoryScreen.getGuiLeft();
            int top = inventoryScreen.getGuiTop();
            inventoryScreen.getMinecraft().getTextureManager().bindTexture(ContainerScreen.INVENTORY_BACKGROUND);
            Screen.blit(left + 76, top + 43, 18, 18, 76, 61, 18, 18, 256, 256);
        }
    }

    private void onTextureStitch(TextureStitchEvent.Pre event)
    {
        event.addSprite(new ResourceLocation(Reference.MOD_ID, "item/empty_backpack_slot"));
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        PlayerEntity oldPlayer = event.getOriginal();
        if(oldPlayer.inventory instanceof ExtendedPlayerInventory && event.getPlayer().inventory instanceof ExtendedPlayerInventory)
        {
            ((ExtendedPlayerInventory) event.getPlayer().inventory).copyBackpack((ExtendedPlayerInventory) oldPlayer.inventory);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        PlayerEntity player = event.getPlayer();
        if(player.inventory instanceof ExtendedPlayerInventory)
        {
            if(!((ExtendedPlayerInventory) player.inventory).getBackpackItems().get(0).isEmpty())
            {
                PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageUpdateBackpack(player.getEntityId(), true));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
            return;

        PlayerEntity player = event.player;
        if(!player.world.isRemote && player.inventory instanceof ExtendedPlayerInventory)
        {
            ExtendedPlayerInventory inventory = (ExtendedPlayerInventory) player.inventory;
            if(!inventory.backpackArray.get(0).equals(inventory.backpackInventory.get(0)))
            {
                PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), new MessageUpdateBackpack(player.getEntityId(), !inventory.backpackInventory.get(0).isEmpty()));
                inventory.backpackArray.set( 0, inventory.backpackInventory.get(0));
            }
        }
    }
}
