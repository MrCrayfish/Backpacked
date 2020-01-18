package com.mrcrayfish.backpacked.proxy;

import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ClientProxy extends CommonProxy
{
    public static final KeyBinding KEY_BACKPACK = new KeyBinding("key.backpack", Keyboard.KEY_B, "key.categories.inventory");

    @Override
    public void setupClient()
    {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        this.addBackpackLayer(skinMap.get("default"));
        this.addBackpackLayer(skinMap.get("slim"));
        ClientRegistry.registerKeyBinding(KEY_BACKPACK);
    }

    private void addBackpackLayer(RenderPlayer renderer)
    {
        renderer.addLayer(new BackpackLayer(renderer));
    }

    public static void setPlayerBackpack(int entityId, boolean wearing)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        if(minecraft.world != null)
        {
            Entity entity = minecraft.world.getEntityByID(entityId);
            if(entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) entity;
                if(player.inventory instanceof ExtendedPlayerInventory)
                {
                    ((ExtendedPlayerInventory) player.inventory).getBackpackItems().set(0, wearing ? new ItemStack(ModItems.BACKPACK) : ItemStack.EMPTY);
                }
            }
        }
    }

    public static void openBackpackWindow(int windowId)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player != null)
        {
            Minecraft.getMinecraft().displayGuiScreen(new BackpackScreen(player.inventory));
            player.openContainer.windowId = windowId;
        }
    }

    @Override
    public void patchPlayerInventory(EntityPlayer player)
    {
        this.patchInventory(Minecraft.getMinecraft(), player);
    }
}
