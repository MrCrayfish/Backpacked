package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.BackpackedButtonBindings;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.ShelfRenderer;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.VillagerBackpackLayer;
import com.mrcrayfish.backpacked.common.BackpackManager;
import com.mrcrayfish.backpacked.core.ModBlockEntities;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ClientHandler
{
    public static final KeyMapping KEY_BACKPACK = new KeyMapping("key.backpack", GLFW.GLFW_KEY_B, "key.categories.inventory");
    public static final ModelInstances MODELS = new ModelInstances();

    public static void setup()
    {
        ClientRegistry.registerKeyBinding(KEY_BACKPACK);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MenuScreens.register(ModContainers.BACKPACK.get(), BackpackScreen::new);

        /* Only register controller events if Controllable is loaded otherwise it will crash */
        if(Backpacked.isControllableLoaded())
        {
            MinecraftForge.EVENT_BUS.register(new ControllerHandler());
            BackpackedButtonBindings.register();
        }

        BackpackManager.instance().getRegisteredBackpacks().forEach(backpack -> {
            BackpackLayer.registerModel(backpack.getId(), () -> (BackpackModel) backpack.getModelSupplier().get());
        });
    }

    public static ModelInstances getModelInstances()
    {
        return MODELS;
    }

    public static void onRegisterLayers(EntityRenderersEvent.AddLayers event)
    {
        addBackpackLayer(event.getSkin("default"));
        addBackpackLayer(event.getSkin("slim"));

        EntityRenderer<?> renderer = event.getRenderer(EntityType.WANDERING_TRADER);
        if(renderer instanceof WanderingTraderRenderer traderRenderer)
        {
            traderRenderer.addLayer(new VillagerBackpackLayer<>(traderRenderer));
        }
    }

    private static void addBackpackLayer(LivingEntityRenderer<?, ?> renderer)
    {
        if(renderer instanceof PlayerRenderer playerRenderer)
        {
            playerRenderer.addLayer(new BackpackLayer<>(playerRenderer));
        }
    }

    public static void createBackpackTooltip(ItemStack stack, List<Component> list)
    {
        if(!Config.SERVER.lockBackpackIntoSlot.get())
            return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player != null && Backpacked.getBackpackStack(mc.player).equals(stack))
        {
            CompoundTag tag = stack.getTag();
            if(tag != null && !tag.getList("Items", Tag.TAG_COMPOUND).isEmpty())
            {
                mc.font.getSplitter().splitLines(BackpackItem.REMOVE_ITEMS_TOOLTIP, 150, Style.EMPTY).forEach(formattedText -> {
                    list.add(new TextComponent(formattedText.getString()).withStyle(ChatFormatting.RED));
                });
            }
        }
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF.get(), ShelfRenderer::new);
    }
}
