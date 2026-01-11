package com.mrcrayfish.backpacked.platform;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.client.renderer.entity.state.BackpackRenderState;
import com.mrcrayfish.backpacked.platform.services.IClientHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class FabricClientHelper implements IClientHelper
{
    private static final Method SETUP_ROTATIONS = findMethod(AvatarRenderer.class, "net.minecraft.class_1007", "method_4212", "(Lnet/minecraft/class_10055;Lnet/minecraft/class_4587;FF)V", AvatarRenderState.class, PoseStack.class, float.class, float.class);

    @Override
    public void openConfigScreen()
    {
        if(!FabricLoader.getInstance().isModLoaded("configured"))
        {
            MutableComponent modName = Component.literal("Configured");
            modName.setStyle(modName.getStyle()
                    .withColor(ChatFormatting.YELLOW)
                    .withUnderlined(true)
                    .withHoverEvent(new HoverEvent.ShowText(Component.translatable("backpacked.chat.open_curseforge_page")))
                    .withClickEvent(new ClickEvent.OpenUrl(URI.create("https://www.curseforge.com/minecraft/mc-mods/configured-fabric"))));
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
    public void invokeRotationSetup(AvatarRenderer<AbstractClientPlayer> renderer, AvatarRenderState state, PoseStack stack, float bodyRot, float scale)
    {
        try
        {
            SETUP_ROTATIONS.invoke(renderer, state, stack, bodyRot, scale);
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Method findMethod(Class<?> targetClass, String className, String methodName, String methodDesc, Class<?>... types)
    {
        try
        {
            MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
            Method method = targetClass.getDeclaredMethod(resolver.mapMethodName("intermediary", className, methodName, methodDesc), types);
            method.setAccessible(true);
            return method;
        }
        catch(NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void drawTooltip(GuiGraphics graphics, Font font, List<ClientTooltipComponent> list, int mouseX, int mouseY, ClientTooltipPositioner positioner)
    {
        graphics.setTooltipForNextFrameInternal(font, list, mouseX, mouseY, positioner, null, false);
    }

    @Override
    public void setMousePos(double x, double y)
    {
        Window window = Minecraft.getInstance().getWindow();
        GLFW.glfwSetCursorPos(window.handle(), x, y);
        MouseHandler handler = Minecraft.getInstance().mouseHandler;
        handler.xpos = x;
        handler.ypos = y;
    }

    @Override
    public void submitGuiElementRenderState(GuiGraphics graphics, GuiElementRenderState state)
    {
        graphics.guiRenderState.submitGuiElement(state);
    }

    @Override
    public void submitGuiPipRenderState(GuiGraphics graphics, PictureInPictureRenderState state)
    {
        graphics.guiRenderState.submitPicturesInPictureState(state);
    }

    @Override
    public @Nullable BackpackRenderState getBackpackRenderState(EntityRenderState state)
    {
        return state.getData(ClientHandler.BACKPACK_RENDER_STATE_KEY);
    }
}
