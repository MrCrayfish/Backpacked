package com.mrcrayfish.backpacked.proxy;

import com.mrcrayfish.backpacked.client.ClientEvents;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.ModelBackpack;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public class ClientProxy extends CommonProxy
{
    public static final KeyBinding KEY_BACKPACK = new KeyBinding("key.backpack", GLFW.GLFW_KEY_B, "key.categories.inventory");

    @Override
    public void setupClient()
    {
        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
        this.addBackpackLayer(skinMap.get("default"));
        this.addBackpackLayer(skinMap.get("slim"));
        ClientRegistry.registerKeyBinding(KEY_BACKPACK);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        ScreenManager.registerFactory(Objects.requireNonNull(ModContainers.BACKPACK.get()), BackpackScreen::new);
    }

    private void addBackpackLayer(PlayerRenderer renderer)
    {
        List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> layers = ObfuscationReflectionHelper.getPrivateValue(LivingRenderer.class, renderer, "field_177097_h");
        if(layers != null)
        {
            layers.add(new BackpackLayer<>(renderer, new ModelBackpack<>()));
        }
    }

    public static void setPlayerBackpack(int entityId, ItemStack backpack)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.world != null)
        {
            Entity entity = minecraft.world.getEntityByID(entityId);
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
