package com.mrcrayfish.backpacked.platform;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.mixin.common.PlayerRendererInvoker;
import com.mrcrayfish.backpacked.platform.services.IClientHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class FabricClientHelper implements IClientHelper
{
    @Override
    public BakedModel getBakedModel(ResourceLocation id)
    {
        BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(id);
        return model != null ? model : Minecraft.getInstance().getModelManager().getMissingModel();
    }

    @Override
    public void openConfigScreen()
    {
        if(!FabricLoader.getInstance().isModLoaded("configured"))
        {
            MutableComponent modName = Component.literal("Configured");
            modName.setStyle(modName.getStyle()
                    .withColor(ChatFormatting.YELLOW)
                    .withUnderlined(true)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("backpacked.chat.open_curseforge_page")))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured-fabric")));
            Component message = Component.translatable("backpacked.chat.install_configured", modName);
            Optional.ofNullable(Minecraft.getInstance().player).ifPresent(player -> player.displayClientMessage(message, false));
            return;
        }

        FabricLoader.getInstance().getModContainer(Constants.MOD_ID).ifPresent(container ->
        {
            try
            {
                Class<?> factoryClass = Class.forName("com.mrcrayfish.configured.integration.CatalogueConfigFactory");
                Method createConfigScreen = factoryClass.getDeclaredMethod("createConfigScreen", Screen.class, ModContainer.class);
                createConfigScreen.invoke(null, Minecraft.getInstance().screen, container);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void invokeRotationSetup(PlayerRenderer renderer, AbstractClientPlayer player, PoseStack stack, float scale, float bodyRot, float partialTick)
    {
        ((PlayerRendererInvoker) renderer).backpacked$setupRotations(player, stack, scale, bodyRot, partialTick);
    }

    @Override
    public void drawTooltip(GuiGraphics graphics, Font font, List<ClientTooltipComponent> list, int mouseX, int mouseY, ClientTooltipPositioner positioner)
    {
        graphics.renderTooltipInternal(font, list, mouseX, mouseY, positioner);
    }

    @Override
    public void setMousePos(double x, double y)
    {
        Window window = Minecraft.getInstance().getWindow();
        GLFW.glfwSetCursorPos(window.getWindow(), x, y);
        MouseHandler handler = Minecraft.getInstance().mouseHandler;
        handler.xpos = x;
        handler.ypos = y;
    }
}
