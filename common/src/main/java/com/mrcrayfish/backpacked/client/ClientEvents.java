package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.ModelProperty;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageEntityBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import com.mrcrayfish.framework.api.event.ClientConnectionEvents;
import com.mrcrayfish.framework.api.event.InputEvents;
import com.mrcrayfish.framework.api.event.ScreenEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ClientEvents
{
    public static final ResourceLocation EMPTY_BACKPACK_SLOT = new ResourceLocation(Constants.MOD_ID, "item/empty_backpack_slot");
    private static boolean initializedSlot = false;

    public static void init()
    {
        TickEvents.END_CLIENT.register(ClientEvents::onClientTickEnd);
        ClientConnectionEvents.LOGGING_IN.register(ClientEvents::onPlayerLogin);
        ScreenEvents.AFTER_DRAW_CONTAINER_BACKGROUND.register(ClientEvents::onAfterDrawBackground);
        ScreenEvents.AFTER_DRAW_CONTAINER_BACKGROUND.register(ClientEvents::repositionSlot);
        ScreenEvents.INIT.register(ClientEvents::onScreenInit);
        InputEvents.KEY.register(ClientEvents::onKeyInput);
        InputEvents.CLICK.register(ClientEvents::onInteraction);
    }

    private static void onPlayerLogin(LocalPlayer player, MultiPlayerGameMode gameMode, Connection connection)
    {
        Config.updateBannedItemsList();
    }

    private static void onAfterDrawBackground(AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY)
    {
        if(Services.BACKPACK.isUsingThirdPartySlot())
            return;

        if(screen instanceof InventoryScreen inventory)
        {
            int left = ClientServices.SCREEN.getScreenLeftPos(inventory);
            int top = ClientServices.SCREEN.getScreenTopPos(inventory);
            graphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, left + 76, top + 43, 7, 7, 18, 18, 256, 256);
        }
        else if(screen instanceof CreativeModeInventoryScreen inventory)
        {
            if(inventory.isInventoryOpen())
            {
                int left = ClientServices.SCREEN.getScreenLeftPos(inventory);
                int top = ClientServices.SCREEN.getScreenTopPos(inventory);
                graphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, left + 126, top + 19, 7, 7, 18, 18, 256, 256);
            }
        }
    }

    // Opens the backpack screen
    public static void onKeyInput(int key, int scanCode, int action, int modifiers)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player != null && mc.screen == null)
        {
            LocalPlayer player = mc.player;
            if(Keys.KEY_BACKPACK.isDown() && Keys.KEY_BACKPACK.consumeClick())
            {
                if(!Services.BACKPACK.getBackpackStack(player).isEmpty())
                {
                    Network.getPlay().sendToServer(new MessageOpenBackpack());
                }
            }
        }
    }

    private static void onClientTickEnd()
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null)
            return;

        List<Player> players = mc.level.getEntities(EntityType.PLAYER, mc.player.getBoundingBox().inflate(16F), player -> true);
        for(Player player : players)
        {
            if(Services.BACKPACK.isUsingThirdPartySlot() && !Services.BACKPACK.isBackpackVisible(player))
                continue;

            ItemStack stack = Services.BACKPACK.getBackpackStack(player);
            if(stack.isEmpty())
                continue;

            if(!canShowBackpackEffects(stack))
                continue;

            /*String modelName = stack.getOrCreateTag().getString("BackpackModel");
            BackpackModel model = BackpackLayer.getModel(modelName).get();
            if(model == null)
                continue;*/

            //model.tickForPlayer(PickpocketUtil.getBackpackBox(player, 1.0F).getCenter(), player);
        }
    }

    public static boolean canShowBackpackEffects(ItemStack stack)
    {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(ModelProperty.SHOW_EFFECTS.getTagName(), Tag.TAG_BYTE))
        {
            return tag.getBoolean(ModelProperty.SHOW_EFFECTS.getTagName());
        }
        return true;
    }

    private static boolean onInteraction(boolean attack, boolean use, boolean pick, InteractionHand hand)
    {
        if(!use || hand != InteractionHand.MAIN_HAND)
            return false;

        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null || mc.gameMode == null)
            return false;

        double range = Config.SERVER.pickpocketing.maxReachDistance.get();
        List<LivingEntity> entities = new ArrayList<>();
        if(Config.SERVER.pickpocketing.enabled.get()) {
            entities.addAll(mc.level.getEntities(EntityType.PLAYER, mc.player.getBoundingBox().inflate(range), player -> {
                return !Services.BACKPACK.getBackpackStack(player).isEmpty() && !player.equals(mc.player) && PickpocketUtil.canPickpocketEntity(player, mc.player);
            }));
        }
        entities.addAll(mc.level.getEntities(EntityType.WANDERING_TRADER, mc.player.getBoundingBox().inflate(mc.gameMode.getPickRange()), entity -> {
            return TraderPickpocketing.get(entity).map(TraderPickpocketing::isBackpackEquipped).orElse(false) && PickpocketUtil.canPickpocketEntity(entity, mc.player, mc.gameMode.getPickRange());
        }));

        if(entities.isEmpty())
            return false;

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
            if(PickpocketUtil.canSeeBackpack(hitEntity, mc.player))
            {
                Network.getPlay().sendToServer(new MessageEntityBackpack(hitEntity.getId()));
                mc.player.swing(hand);
            }
            return true;
        }
        return false;
    }

    /*@SuppressWarnings("removal") // Probably be removed in 1.19.1
    public void onRenderWorldLastEvent(RenderLevelLastEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(!mc.getEntityRenderDispatcher().shouldRenderHitBoxes())
            return;

        if(!Config.SERVER.common.pickpocketBackpacks.get())
            return;

        PoseStack stack = event.getPoseStack();
        stack.pushPose();
        Vec3 view = mc.gameRenderer.getMainCamera().getPosition();
        stack.translate(-view.x(), -view.y(), -view.z());
        MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
        for(Player player : mc.level.players())
        {
            if(Services.BACKPACK.getBackpackStack(player).isEmpty())
                continue;

            if(player.isLocalPlayer())
                continue;

            boolean inReach = PickpocketUtil.inReachOfBackpack(player, mc.player, Config.SERVER.common.pickpocketMaxReachDistance.get()) && PickpocketUtil.canSeeBackpack(player, mc.player);
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
            Vec3 start = Vec3.directionFromRotation(0, bodyRotation + 180 - Config.SERVER.common.pickpocketMaxRangeAngle.get().floatValue()).scale(Config.SERVER.pickpocketMaxReachDistance.get());
            Vec3 end = Vec3.directionFromRotation(0, bodyRotation - 180 + Config.SERVER.common.pickpocketMaxRangeAngle.get().floatValue()).scale(Config.SERVER.pickpocketMaxReachDistance.get());
            builder.vertex(matrix4f, (float) (pos.x + start.x),(float) (pos.y + start.y), (float) (pos.z + start.z)).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) (pos.x + end.x),(float) (pos.y + end.y), (float) (pos.z + end.z)).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
            builder.vertex(matrix4f, (float) pos.x,(float) pos.y, (float) pos.z).color(lineRed, lineGreen, lineBlue, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        }
        source.endBatch(RenderType.lines());
        stack.popPose();
    }*/

    private static void onScreenInit(Screen screen)
    {
        // Fixes the slot repositioning after resizing window
        if(screen instanceof CreativeModeInventoryScreen)
        {
            initializedSlot = false;
        }
    }

    private static void repositionSlot(AbstractContainerScreen<?> screen, GuiGraphics graphics, int mouseX, int mouseY)
    {
        if(Services.BACKPACK.isUsingThirdPartySlot())
            return;

        if(!(screen instanceof CreativeModeInventoryScreen creativeScreen))
        {
            initializedSlot = false;
            return;
        }

        if(creativeScreen.isInventoryOpen())
        {
            if(!initializedSlot)
            {
                initializedSlot = true;
                List<Slot> slots = creativeScreen.getMenu().slots;
                Slot backpackSlot = slots.stream().filter(slot -> slot.container instanceof ExtendedPlayerInventory && slot.getContainerSlot() == 46).findFirst().orElse(null);
                if(backpackSlot != null)
                {
                    int index = slots.indexOf(backpackSlot);
                    Slot newSlot = ClientServices.SCREEN.createCreativeSlotWrapper(backpackSlot, index, 127, 20);
                    backpackSlot.index = index;
                    slots.set(index, newSlot);
                }
            }
        }
        else
        {
            initializedSlot = false;
        }
    }
}
