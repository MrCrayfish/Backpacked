package com.mrcrayfish.backpacked.platform;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.services.IClientHelper;
import com.mrcrayfish.backpacked.util.ReflectedMethod;
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
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class NeoForgeClientHelper implements IClientHelper
{
    private static final ReflectedMethod<PlayerRenderer, Void> SETUP_ROTATIONS = new ReflectedMethod<>(PlayerRenderer.class, "setupRotations", AbstractClientPlayer.class, PoseStack.class, float.class, float.class, float.class, float.class);

    @Override
    public void openConfigScreen()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ModList.get().getModContainerById(Constants.MOD_ID).ifPresent(container ->
        {
            Screen screen = container.getCustomExtension(IConfigScreenFactory.class).map(function -> function.createScreen(container, null)).orElse(null);
            if(screen != null)
            {
                minecraft.setScreen(screen);
            }
            else if(minecraft.player != null)
            {
                MutableComponent modName = Component.literal("Configured");
                modName.setStyle(modName.getStyle()
                        .withColor(ChatFormatting.YELLOW)
                        .withUnderlined(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("backpacked.chat.open_curseforge_page")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured")));
                Component message = Component.translatable("backpacked.chat.install_configured", modName);
                minecraft.player.displayClientMessage(message, false);
            }
        });
    }

    @Override
    public void invokeRotationSetup(PlayerRenderer renderer, AbstractClientPlayer player, PoseStack stack, float scale, float bodyRot, float partialTick)
    {
        SETUP_ROTATIONS.invoke(renderer, player, stack, 0, bodyRot, partialTick, scale);
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
