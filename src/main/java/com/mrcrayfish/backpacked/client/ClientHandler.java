package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.BackpackedButtonBindings;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.common.BackpackManager;
import com.mrcrayfish.backpacked.core.ModContainers;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class ClientHandler
{
    public static final KeyBinding KEY_BACKPACK = new KeyBinding("key.backpack", GLFW.GLFW_KEY_B, "key.categories.inventory");

    public static void setup()
    {
        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
        addBackpackLayer(skinMap.get("default"));
        addBackpackLayer(skinMap.get("slim"));
        ClientRegistry.registerKeyBinding(KEY_BACKPACK);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        ScreenManager.register(ModContainers.BACKPACK.get(), BackpackScreen::new);

        /* Only register controller events if Controllable is loaded otherwise it will crash */
        if(Backpacked.isControllableLoaded())
        {
            MinecraftForge.EVENT_BUS.register(new ControllerHandler());
            BackpackedButtonBindings.register();
        }

        BackpackManager.instance().getRegisteredBackpacks().forEach(backpack -> {
            BackpackLayer.registerModel(backpack.getId(), backpack.getModel());
        });
    }

    private static void addBackpackLayer(PlayerRenderer renderer)
    {
        renderer.addLayer(new BackpackLayer<>(renderer));
    }

    public static void createBackpackTooltip(ItemStack stack, List<ITextComponent> list)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player != null && Backpacked.getBackpackStack(mc.player).equals(stack))
        {
            CompoundNBT tag = stack.getTag();
            if(tag != null && !tag.getList("Items", Constants.NBT.TAG_COMPOUND).isEmpty())
            {
                mc.font.getSplitter().splitLines(BackpackItem.REMOVE_ITEMS_TOOLTIP, 150, Style.EMPTY).forEach(formattedText -> {
                    list.add(new StringTextComponent(formattedText.getString()).withStyle(TextFormatting.RED));
                });
            }
        }
    }
}
