package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.toasts.UnlockBackpackToast;
import com.mrcrayfish.backpacked.core.ModSounds;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.UnlockableContainerScreen;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.network.message.*;
import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
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
        if(minecraft.level == null || minecraft.player == null)
            return;

        ItemStack stack = message.stack();
        if(stack.isEmpty())
            return;

        Vec3 pos = message.pos();
        ItemEntity entity = new ItemEntity(minecraft.level, pos.x, pos.y, pos.z, stack);
        minecraft.particleEngine.add(new ItemPickupParticle(minecraft.getEntityRenderDispatcher(), minecraft.renderBuffers(), minecraft.level, entity, minecraft.player));

        float pitch = 0.7F + 0.3F * minecraft.level.random.nextFloat();
        SimpleSoundInstance sound = new SimpleSoundInstance(ModSounds.AUGMENT_LOOTBOUND_TAKE_ITEM.get(), SoundSource.BLOCKS, 1F, pitch, SoundInstance.createUnseededRandom(), pos.x, pos.y, pos.z);
        minecraft.getSoundManager().play(sound);
    }
}
