package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.SpawnParticleFunction;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessagePickpocketBackpack;
import com.mrcrayfish.backpacked.network.message.MessageRequestManagement;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import com.mrcrayfish.framework.api.event.ClientConnectionEvents;
import com.mrcrayfish.framework.api.event.InputEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class ClientEvents // TODO DONE
{
    public static void init()
    {
        TickEvents.END_CLIENT.register(ClientEvents::onClientTickEnd);
        ClientConnectionEvents.LOGGING_IN.register(ClientEvents::onPlayerLogin);
        InputEvents.KEY.register(ClientEvents::onKeyInput);
        InputEvents.CLICK.register(ClientEvents::onInteraction);
    }

    private static void onPlayerLogin(LocalPlayer player, MultiPlayerGameMode gameMode, Connection connection)
    {
        Config.updateBannedItemsList();
    }

    // Opens the backpack screen
    public static void onKeyInput(int key, int scanCode, int action, int modifiers)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player != null && mc.screen == null)
        {
            if(Keys.KEY_BACKPACK.isDown() && Keys.KEY_BACKPACK.consumeClick())
            {
                Network.getPlay().sendToServer(new MessageOpenBackpack());
            }
            if(Keys.KEY_MANAGEMENT.isDown() && Keys.KEY_MANAGEMENT.consumeClick())
            {
                Network.getPlay().sendToServer(new MessageRequestManagement());
            }
        }
    }

    private static void onClientTickEnd()
    {
        SpawnParticleFunction.clearSpawned();
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
                Optional<CosmeticProperties> optional = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player); // Just use properties to determine if backpack is equipped on client
                return !player.equals(mc.player) && optional.isPresent() && PickpocketUtil.canPickpocketEntity(player, mc.player);
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
                Network.getPlay().sendToServer(new MessagePickpocketBackpack(hitEntity.getId()));
                mc.player.swing(hand);
            }
            return true;
        }
        return false;
    }
}
