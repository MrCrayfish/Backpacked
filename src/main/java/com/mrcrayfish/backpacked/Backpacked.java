package com.mrcrayfish.backpacked;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.mrcrayfish.backpacked.asm.BackpackedPlugin;
import com.mrcrayfish.backpacked.client.ClientEvents;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModModels;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.ExtendedPlayerContainer;
import com.mrcrayfish.backpacked.network.PacketHandler;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import com.mrcrayfish.backpacked.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MrCrayfish
 */
//@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = Reference.ACCEPTED_MC_VERSIONS)
public class Backpacked extends DummyModContainer
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);
    public static final ResourceLocation EMPTY_BACKPACK_SLOT = new ResourceLocation(Reference.MOD_ID, "items/empty_backpack_slot");

    public static Backpacked instance;
    public static CommonProxy proxy;

    public Backpacked()
    {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = Reference.MOD_ID;
        meta.name = Reference.MOD_NAME;
        meta.version = Reference.MOD_VERSION;
        meta.authorList = Collections.singletonList("MrCrayfish");
        meta.url = "https://mrcrayfish.com/mod?id=backpacked";
        instance = this;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Override
    public File getSource()
    {
        return BackpackedPlugin.LOCATION;
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        return this.getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    @Override
    public Map<String, String> getCustomModProperties()
    {
        return super.getCustomModProperties();
    }

    @Subscribe
    public void onModConstruct(FMLConstructionEvent event)
    {
        ConfigManager.sync(this.getModId(), Config.Type.INSTANCE);
        try
        {
            ClassLoader mcl = Loader.instance().getModClassLoader();
            Side side = FMLCommonHandler.instance().getEffectiveSide();
            switch(side)
            {
                case CLIENT:
                    proxy = (CommonProxy) Class.forName(Reference.CLIENT_PROXY, true, mcl).newInstance();
                    break;
                case SERVER:
                    proxy = (CommonProxy) Class.forName(Reference.COMMON_PROXY, true, mcl).newInstance();
                    break;
            }
        }
        catch(IllegalAccessException | InstantiationException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        MinecraftForge.EVENT_BUS.register(new ModItems());
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(new ModModels());
            MinecraftForge.EVENT_BUS.register(new ClientEvents());
        }
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BackpackConfig());
    }

    @Subscribe
    public void init(FMLInitializationEvent event)
    {
        proxy.setupClient();
        PacketHandler.init();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if(event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks())
        {
            event.getMap().registerSprite(EMPTY_BACKPACK_SLOT);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        EntityPlayer oldPlayer = event.getOriginal();
        if(oldPlayer.inventory instanceof ExtendedPlayerInventory && event.getEntityPlayer().inventory instanceof ExtendedPlayerInventory)
        {
            ((ExtendedPlayerInventory) event.getEntityPlayer().inventory).copyBackpack((ExtendedPlayerInventory) oldPlayer.inventory);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if(player.inventory instanceof ExtendedPlayerInventory)
        {
            if(!((ExtendedPlayerInventory) player.inventory).getBackpackItems().get(0).isEmpty())
            {
                PacketHandler.INSTANCE.sendToAllTracking(new MessageUpdateBackpack(player.getEntityId(), true), player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
            return;

        EntityPlayer player = event.player;
        if(!player.world.isRemote && player.inventory instanceof ExtendedPlayerInventory)
        {
            ExtendedPlayerInventory inventory = (ExtendedPlayerInventory) player.inventory;
            if(!inventory.backpackArray.get(0).equals(inventory.backpackInventory.get(0)))
            {
                PacketHandler.INSTANCE.sendToAllTracking(new MessageUpdateBackpack(player.getEntityId(), true), player);
                inventory.backpackArray.set( 0, inventory.backpackInventory.get(0));
            }
        }
    }

    /*
     * Hooks into EntityPlayer constructor to allow manipulation of fields.
     * Linked via ASM, do not remove!
     */
    @SuppressWarnings("unused")
    public static void onPlayerInit(EntityPlayer player)
    {
        Backpacked.patchInventory(player);
    }

    private static void patchInventory(EntityPlayer player)
    {
        player.inventory = new ExtendedPlayerInventory(player);
        player.inventoryContainer = new ExtendedPlayerContainer(player.inventory, !player.world.isRemote, player);
        player.openContainer = player.inventoryContainer;
    }

    /*
     * Fixes the backpack slot in the creative inventory to be positioned correctly.
     * Linked via ASM, do not remove!
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    public static void patchCreativeSlots(GuiContainerCreative.ContainerCreative creativeContainer)
    {
        creativeContainer.inventorySlots.stream().filter(slot -> slot.inventory instanceof ExtendedPlayerInventory && slot.getSlotIndex() == 41).findFirst().ifPresent(slot ->
        {
            slot.xPos = 127;
            slot.yPos = 20;
        });
    }

    /*
     * Fixes an issue in net.minecraft.network.play.client.CCreativeInventoryActionPacket where a
     * slot index flag excludes the backpack slot. Linked via ASM, do not remove!
     */
    @SuppressWarnings("unused")
    public static int getCreativeSlotMax(EntityPlayerMP player)
    {
        if(player.inventory instanceof ExtendedPlayerInventory)
        {
            return 46;
        }
        return 45;
    }

    /*
     * Hook for missing event found in newer versions of Forge. Allows you to draw
     * on the background of of the GUI. Drawing slots on the foreground layer causes
     * lighting issues and doesn't work correctly in general.
     * Linked via ASM, do not remove!
     */
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    public static void drawBackgroundLayer(GuiContainer screen)
    {
        if(screen instanceof GuiInventory)
        {
            GuiInventory guiInventory = (GuiInventory) screen;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int left = guiInventory.getGuiLeft();
            int top = guiInventory.getGuiTop();
            guiInventory.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
            guiInventory.drawTexturedModalRect(left + 76, top + 43, 76, 61, 18, 18);
        }
        else if(screen instanceof GuiContainerCreative)
        {
            GuiContainerCreative guiContainerCreative = (GuiContainerCreative) screen;
            if(guiContainerCreative.getSelectedTabIndex() == CreativeTabs.INVENTORY.getTabIndex())
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                int left = guiContainerCreative.getGuiLeft();
                int top = guiContainerCreative.getGuiTop();
                guiContainerCreative.mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
                guiContainerCreative.drawTexturedModalRect(left + 126, top + 19, 76, 61, 18, 18);
            }
        }
    }

    public static ItemStack getBackpackStack(EntityPlayer player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        if(player.inventory instanceof ExtendedPlayerInventory)
        {
            ExtendedPlayerInventory inventory = (ExtendedPlayerInventory) player.inventory;
            backpack.set(inventory.getBackpackItems().get(0));
        }
        return backpack.get();
    }
}
