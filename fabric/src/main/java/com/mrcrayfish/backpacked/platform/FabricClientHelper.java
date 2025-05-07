package com.mrcrayfish.backpacked.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.services.IClientHelper;
import com.mrcrayfish.backpacked.util.ReflectedMethod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class FabricClientHelper implements IClientHelper
{
    //private static final Method SETUP_ROTATIONS = findMethod(PlayerRenderer.class); new ReflectedMethod<>(, "net.minecraft.class_1007.method_4212", AbstractClientPlayer.class, PoseStack.class, float.class, float.class, float.class, float.class);

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
        //SETUP_ROTATIONS.invoke(renderer, player, stack, 0, bodyRot, partialTick, scale);
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
}
