package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class CommonEvents
{
    @SubscribeEvent
    public static void onPickupItem(EntityItemPickupEvent event)
    {
        if(Config.SERVER.lockBackpackIntoSlot.get() && event.getEntityLiving() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            ItemEntity entity = event.getItem();
            ItemStack stack = entity.getItem();
            if(!(stack.getItem() instanceof BackpackItem))
                return;

            if(!Backpacked.getBackpackStack(player).isEmpty())
                return;

            CompoundNBT tag = stack.getTag();
            if(tag == null || tag.getList("Items", Constants.NBT.TAG_COMPOUND).isEmpty())
                return;

            if(Backpacked.setBackpackStack(player, stack))
            {
                ((ServerWorld) entity.level).getChunkSource().broadcast(entity, new SCollectItemPacket(entity.getId(), player.getId(), stack.getCount()));
                event.setCanceled(true);
                event.getItem().kill();
            }
        }
    }
}