package com.mrcrayfish.backpacked.proxy;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.BackpackedButtonBindings;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.BackpackModels;
import com.mrcrayfish.backpacked.client.ClientEvents;
import com.mrcrayfish.backpacked.client.ControllerHandler;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.StandardBackpackModel;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ClientProxy extends CommonProxy
{
    public static final KeyBinding KEY_BACKPACK = new KeyBinding("key.backpack", GLFW.GLFW_KEY_B, "key.categories.inventory");

    @Override
    public void setupClient()
    {
        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
        this.addBackpackLayer(skinMap.get("default"));
        this.addBackpackLayer(skinMap.get("slim"));
        ClientRegistry.registerKeyBinding(KEY_BACKPACK);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        ScreenManager.register(ModContainers.BACKPACK.get(), BackpackScreen::new);

        /* Only register controller events if Controllable is loaded otherwise it will crash */
        if(Backpacked.isControllableLoaded())
        {
            MinecraftForge.EVENT_BUS.register(new ControllerHandler());
            BackpackedButtonBindings.register();
        }

        BackpackLayer.registerModel(new ResourceLocation(Reference.MOD_ID, "classic"), BackpackModels.CLASSIC);
        BackpackLayer.registerModel(new ResourceLocation(Reference.MOD_ID, "bamboo_basket"), BackpackModels.BAMBOO_BASKET);
        BackpackLayer.registerModel(new ResourceLocation(Reference.MOD_ID, "rocket"), BackpackModels.ROCKET);
        BackpackLayer.registerModel(new ResourceLocation(Reference.MOD_ID, "mini_chest"), BackpackModels.MINI_CHEST);
        BackpackLayer.registerModel(new ResourceLocation(Reference.MOD_ID, "trash_can"), BackpackModels.TRASH_CAN);
    }

    private void addBackpackLayer(PlayerRenderer renderer)
    {
        renderer.addLayer(new BackpackLayer<>(renderer));
    }

    public static void setPlayerBackpack(int entityId, ItemStack backpack)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level != null)
        {
            Entity entity = minecraft.level.getEntity(entityId);
            if(entity instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity) entity;
                if(player.inventory instanceof ExtendedPlayerInventory)
                {
                    ((ExtendedPlayerInventory) player.inventory).getBackpackItems().set(0, backpack);
                }
            }
        }
    }
}
