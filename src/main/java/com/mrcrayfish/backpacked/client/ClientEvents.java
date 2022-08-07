package com.mrcrayfish.backpacked.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageEntityBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import com.mrcrayfish.backpacked.util.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    public static final ResourceLocation EMPTY_BACKPACK_SLOT = new ResourceLocation(Reference.MOD_ID, "item/empty_backpack_slot");
    private static CreativeModeTab currentTab = null;

    @SubscribeEvent
    public void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event)
    {
        Backpacked.updateBannedItemsList();
    }

    @SubscribeEvent
    public void onPlayerRenderScreen(ContainerScreenEvent.Render.Background event)
    {
        if(Backpacked.isCuriosLoaded())
            return;

        AbstractContainerScreen<?> screen = event.getContainerScreen();
        if(screen instanceof InventoryScreen inventory)
        {
            int left = inventory.getGuiLeft();
            int top = inventory.getGuiTop();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
            Screen.blit(event.getPoseStack(), left + 76, top + 43, 7, 7, 18, 18, 256, 256);
        }
        else if(screen instanceof CreativeModeInventoryScreen inventory)
        {
            if(inventory.getSelectedTab() == CreativeModeTab.TAB_INVENTORY.getId())
            {
                int left = inventory.getGuiLeft();
                int top = inventory.getGuiTop();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
                Screen.blit(event.getPoseStack(), left + 126, top + 19, 7, 7, 18, 18, 256, 256);
            }
        }
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if(event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
        {
            event.addSprite(EMPTY_BACKPACK_SLOT);
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen instanceof BackpackScreen)
        {
            if(event.getAction() == GLFW.GLFW_PRESS && event.getKey() == ClientHandler.KEY_BACKPACK.getKey().getValue())
            {
                minecraft.player.closeContainer();
            }
        }
        else if(minecraft.player != null && minecraft.screen == null)
        {
            LocalPlayer player = minecraft.player;
            if(ClientHandler.KEY_BACKPACK.isDown() && ClientHandler.KEY_BACKPACK.consumeClick())
            {
                if(!Backpacked.getBackpackStack(player).isEmpty())
                {
                    Network.getPlayChannel().sendToServer(new MessageOpenBackpack());
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTickEnd(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null)
            return;

        List<Player> players = mc.level.getEntities(EntityType.PLAYER, mc.player.getBoundingBox().inflate(16F), player -> true);
        for(Player player : players)
        {
            if(Backpacked.isCuriosLoaded() && !Curios.isBackpackVisible(player))
                continue;

            ItemStack stack = Backpacked.getBackpackStack(player);
            if(stack.isEmpty())
                continue;

            if(!canShowBackpackEffects(stack))
                continue;

            String modelName = stack.getOrCreateTag().getString("BackpackModel");
            BackpackModel model = BackpackLayer.getModel(modelName).get();
            if(model == null)
                continue;

            model.tickForPlayer(PickpocketUtil.getBackpackBox(player, 1.0F).getCenter(), player);
        }
    }

    public static boolean canShowBackpackEffects(ItemStack stack)
    {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(BackpackModelProperty.SHOW_EFFECTS.getTagName(), Tag.TAG_BYTE))
        {
            return tag.getBoolean(BackpackModelProperty.SHOW_EFFECTS.getTagName());
        }
        return true;
    }

    @SubscribeEvent
    public void onRightClick(InputEvent.InteractionKeyMappingTriggered event)
    {
        if(event.isUseItem())
        {
            this.performBackpackRaytrace(event);
        }
    }

    private void performBackpackRaytrace(InputEvent.InteractionKeyMappingTriggered event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null || mc.gameMode == null)
            return;

        double range = Config.SERVER.pickpocketMaxReachDistance.get();
        List<LivingEntity> entities = new ArrayList<>();
        if(Config.SERVER.pickpocketBackpacks.get()) {
            entities.addAll(mc.level.getEntities(EntityType.PLAYER, mc.player.getBoundingBox().inflate(range), player -> {
                return !Backpacked.getBackpackStack(player).isEmpty() && !player.equals(mc.player) && PickpocketUtil.canPickpocketEntity(player, mc.player);
            }));
        }
        entities.addAll(mc.level.getEntities(EntityType.WANDERING_TRADER, mc.player.getBoundingBox().inflate(mc.gameMode.getPickRange()), entity -> {
            return PickpocketChallenge.get(entity).map(PickpocketChallenge::isBackpackEquipped).orElse(false) && PickpocketUtil.canPickpocketEntity(entity, mc.player, mc.gameMode.getPickRange());
        }));

        if(entities.isEmpty())
            return;

        Vec3 start = mc.player.getEyePosition(1.0F);
        Vec3 end = mc.player.getViewVector(1.0F).scale(mc.gameMode.getPickRange()).add(start);

        double closestDistance = Double.MAX_VALUE;
        LivingEntity hitEntity = null;
        for(LivingEntity entity : entities)
        {
            AABB box = PickpocketUtil.getBackpackBox(entity, 1.0F);
            Optional<Vec3> optionalHitVec = box.clip(start, end);
            if(optionalHitVec.isEmpty())
                continue;

            double distance = start.distanceTo(optionalHitVec.get());
            if(distance < closestDistance)
            {
                closestDistance = distance;
                hitEntity = entity;
            }
        }

        if(hitEntity != null)
        {
            event.setCanceled(true);
            event.setSwingHand(false);
            if(PickpocketUtil.canSeeBackpack(hitEntity, mc.player))
            {
                Network.getPlayChannel().sendToServer(new MessageEntityBackpack(hitEntity.getId()));
                event.setSwingHand(true);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("removal") // Probably be removed in 1.19.1
    public void onRenderWorldLastEvent(RenderLevelLastEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(!mc.getEntityRenderDispatcher().shouldRenderHitBoxes())
            return;

        if(!Config.SERVER.pickpocketBackpacks.get())
            return;

        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        Vec3 view = mc.gameRenderer.getMainCamera().getPosition();
        stack.translate(-view.x(), -view.y(), -view.z());
        MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
        for(Player player : mc.level.players())
        {
            if(Backpacked.getBackpackStack(player).isEmpty())
                continue;

            if(player.isLocalPlayer())
                continue;

            boolean inReach = PickpocketUtil.inReachOfBackpack(player, mc.player, Config.SERVER.pickpocketMaxReachDistance.get()) && PickpocketUtil.canSeeBackpack(player, mc.player);
            float boxRed = inReach ? 0.0F : 1.0F;
            float boxGreen = inReach ? 1.0F : 1.0F;
            float boxBlue = inReach ? 0.0F : 1.0F;
            VertexConsumer builder = source.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(stack, builder, PickpocketUtil.getBackpackBox(player, event.getPartialTick()), boxRed, boxGreen, boxBlue, 1.0F);

            float bodyRotation = Mth.lerp(event.getPartialTick(), player.yBodyRotO, player.yBodyRot);
            boolean inRange = PickpocketUtil.inRangeOfBackpack(player, mc.player);
            float lineRed = inRange ? 0.0F : 1.0F;
            float lineGreen = inRange ? 1.0F : 1.0F;
            float lineBlue = inRange ? 0.0F : 1.0F;
            Matrix4f matrix4f = stack.last().pose();
            Vec3 pos = player.getPosition(event.getPartialTick());
            Vec3 start = Vec3.directionFromRotation(0, bodyRotation + 180 - Config.SERVER.pickpocketMaxRangeAngle.get().floatValue()).scale(Config.SERVER.pickpocketMaxReachDistance.get());
            Vec3 end = Vec3.directionFromRotation(0, bodyRotation - 180 + Config.SERVER.pickpocketMaxRangeAngle.get().floatValue()).scale(Config.SERVER.pickpocketMaxReachDistance.get());
            builder.vertex(matrix4f, (float) (pos.x + start.x),(float) (pos.y + start.y), (float) (pos.z + start.z)).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) (pos.x + end.x),(float) (pos.y + end.y), (float) (pos.z + end.z)).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        }
        source.endBatch(RenderType.lines());
        stack.popPose();
    }

    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init event)
    {
        // Fixes the slot repositioning after resizing window
        if(event.getScreen() instanceof CreativeModeInventoryScreen)
        {
            currentTab = null;
        }
    }

    @SubscribeEvent
    public void onRenderTickStart(TickEvent.RenderTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
            return;

        if(Backpacked.isCuriosLoaded())
            return;

        Minecraft mc = Minecraft.getInstance();
        if(!(mc.screen instanceof CreativeModeInventoryScreen screen))
        {
            currentTab = null;
            return;
        }

        CreativeModeTab tab = CreativeModeTab.TABS[screen.getSelectedTab()];
        if(currentTab == null || currentTab != tab)
        {
            currentTab = tab;
            if(currentTab == CreativeModeTab.TAB_INVENTORY)
            {
                List<Slot> slots = screen.getMenu().slots;
                slots.stream().filter(slot -> slot.container instanceof ExtendedPlayerInventory && slot.getSlotIndex() == 41).findFirst().ifPresent(slot -> {
                    ReflectionHelper.repositionSlot(slot, 127, 20);
                });
            }
        }
    }
}
