package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.augment.menu.RecallMenu;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.UnlockableContainerScreen;
import com.mrcrayfish.backpacked.client.gui.toasts.UnlockBackpackToast;
import com.mrcrayfish.backpacked.client.particle.FarmhandPlantParticle;
import com.mrcrayfish.backpacked.core.ModSounds;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.network.message.*;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ClientPlayHandler
{
    public static void handleUnlockBackpack(MessageUnlockBackpack message)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        Player player = mc.player;
        UnlockManager.getTracker(player).ifPresent(impl ->
        {
            ClientBackpack backpack = ClientRegistry.instance().getBackpack(message.cosmeticId());
            if(backpack != null)
            {
                impl.unlockBackpack(message.cosmeticId());
                mc.getToasts().addToast(new UnlockBackpackToast(backpack));
            }
        });
    }

    public static void handleSyncUnlockTracker(MessageSyncUnlockTracker message)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;

        Player player = mc.player;
        UnlockManager.getTracker(player).ifPresent(impl -> {
            message.unlockedBackpacks().forEach(impl::unlockBackpack);
        });
    }

    public static void handleOpenCustomisation(MessageOpenCustomisation message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player == null)
            return;

        minecraft.setScreen(new CustomiseBackpackScreen(message.backpackIndex(), message.progressMap(), message.properties(), message.showCosmeticWarning(), message.completionProgressMap()));
    }

    public static void handleSyncVillagerBackpack(MessageSyncVillagerBackpack message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null)
            return;

        Entity entity = minecraft.level.getEntity(message.entityId());
        if(entity instanceof WanderingTrader trader)
        {
            TraderPickpocketing.get(trader).ifPresent(data -> data.setBackpackEquipped(true));
        }
    }

    @SuppressWarnings("ConstantValue")
    public static void handleUnlockSlot(MessageSyncUnlockSlot message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player == null)
            return;

        AbstractContainerMenu menu = minecraft.player.containerMenu;
        if(menu == null)
            return;

        boolean changed = false;
        List<Integer> slotIndexes = message.unlockedSlotIndexes();
        for(int slotIndex : slotIndexes)
        {
            if(slotIndex < 0 || slotIndex >= menu.slots.size())
                continue;

            Slot slot = menu.getSlot(slotIndex);
            if(!(slot instanceof UnlockableSlot unlockableSlot))
                continue;

            if(unlockableSlot.unlock(minecraft.player))
            {
                changed = true;
            }
        }

        if(changed && minecraft.screen instanceof UnlockableContainerScreen<?> screen)
        {
            screen.onSlotUnlocked(slotIndexes);
        }
    }

    public static void handleLootboundTakeItem(MessageLootboundTakeItem message, MessageContext context)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null)
            return;

        Entity player = minecraft.level.getEntity(message.entityId());
        if(player == null)
            return;

        ItemStack stack = message.stack();
        if(stack.isEmpty())
            return;

        Vec3 pos = message.pos();
        ItemEntity entity = new ItemEntity(minecraft.level, pos.x, pos.y, pos.z, stack);
        minecraft.particleEngine.add(new ItemPickupParticle(minecraft.getEntityRenderDispatcher(), minecraft.renderBuffers(), minecraft.level, entity, player));

        if(message.sound())
        {
            float pitch = 0.7F + 0.3F * minecraft.level.random.nextFloat();
            SimpleSoundInstance sound = new SimpleSoundInstance(ModSounds.AUGMENT_LOOTBOUND_TAKE_ITEM.get(), SoundSource.BLOCKS, 1F, pitch, SoundInstance.createUnseededRandom(), pos.x, pos.y, pos.z);
            minecraft.getSoundManager().play(sound);
        }
    }

    public static void handleSyncAugmentChange(MessageSyncAugmentChange message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.screen instanceof BackpackScreen screen)
        {
            screen.updateAugment(message.position(), message.augment());
        }
    }

    public static void handleFarmhandPlant(MessageFarmhandPlant message, MessageContext context)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null || minecraft.player == null)
            return;

        ItemStack stack = message.stack();
        if(stack.isEmpty())
            return;

        var level = minecraft.level;
        if(!(level.getEntity(message.entityId()) instanceof Player player))
            return;

        var dispatcher = minecraft.getEntityRenderDispatcher();
        var renderBuffers = minecraft.renderBuffers();
        var start = new Vec3(player.getX(), player.getY(0.65), player.getZ()).add(Vec3.directionFromRotation(0, player.yBodyRot + 180).scale(0.25));
        var end = message.pos().getBottomCenter();
        minecraft.particleEngine.add(new FarmhandPlantParticle(dispatcher, renderBuffers, level, stack, start, end));
        minecraft.level.playSound(null, start.x, start.y, start.z, ModSounds.AUGMENT_LOOTBOUND_TAKE_ITEM.get(), SoundSource.PLAYERS, 1F, 0.5F);
    }

    public static void handleMessageShelfPlaceAnimation(MessageShelfPlaceAnimation message, MessageContext context)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null)
            return;

        var level = minecraft.level;
        if(!(level.getBlockEntity(message.pos()) instanceof ShelfBlockEntity shelf))
            return;

        shelf.playAnimation();
    }

    public static void handleMessageResponseShelfKey(MessageResponseShelfKey message, MessageContext context)
    {
        RecallMenu.ShelfStatus.handle(message.backpackIndex(), message.position(), message.valid());
    }

    public static void handleUnlockAugmentBay(MessageSyncUnlockAugmentBay message)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.player == null)
            return;

        if(!(minecraft.player.containerMenu instanceof BackpackContainerMenu menu))
            return;

        if(!menu.getAugmentBayController().unlockSlot(message.position().ordinal()))
            return;

        if(minecraft.screen instanceof BackpackScreen screen)
        {
            screen.onAugmentBayUnlocked(message.position());
        }
    }
}
