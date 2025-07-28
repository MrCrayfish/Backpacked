package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.ExperienceCostTooltip;
import com.mrcrayfish.backpacked.common.backpack.UnlockedSlots;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageUnlockSlot;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class UnlockableContainerScreen<T extends AbstractContainerMenu & UnlockableController> extends AbstractContainerScreen<T>
{
    private static final ResourceLocation ICON_LOCK = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock");
    private static final int UNLOCK_TIME = 20;

    private final Player player;
    private @Nullable UnlockableSlot hoveredLockedSlot;
    private UnlockableSlot clickedLockedSlot;
    private int heldUnlockTime;

    public UnlockableContainerScreen(T menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.player = inventory.player;
    }

    @Override
    protected void containerTick()
    {
        if(this.clickedLockedSlot != null)
        {
            // Cancel if the user moves the mouse off the locked slot
            if(this.hoveredLockedSlot != this.clickedLockedSlot)
            {
                this.clickedLockedSlot = null;
                return;
            }
            if(this.heldUnlockTime-- <= 0)
            {
                Network.PLAY.sendToServer(new MessageUnlockSlot(this.clickedLockedSlot.getContainerSlot()));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.3F, 0.5F));
                this.clickedLockedSlot = null;
            }
            else if(this.heldUnlockTime % 2 == 0)
            {
                float pitch = 0.9F + 0.4F * (UNLOCK_TIME - this.heldUnlockTime) / (float) UNLOCK_TIME;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, pitch, 0.25F));
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.hoveredLockedSlot = null;
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);

        if(this.clickedLockedSlot != null)
        {
            int progressX = this.leftPos + this.clickedLockedSlot.x;
            int progressY = this.topPos + this.clickedLockedSlot.y;
            int progressWidth = (int) (16 * (UNLOCK_TIME - this.heldUnlockTime) / (float) UNLOCK_TIME);
            graphics.fill(progressX, progressY, progressX + progressWidth, progressY + 16, 0x88A7FF4C);
        }

        for(Slot slot : this.getMenu().slots)
        {
            if(slot instanceof UnlockableSlot lockedSlot)
            {
                if(this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY))
                {
                    this.hoveredLockedSlot = lockedSlot;
                }
                if(!lockedSlot.isUnlocked())
                {
                    graphics.blitSprite(ICON_LOCK, this.leftPos + slot.x + 2, this.topPos + slot.y + 2, 12, 12);
                }
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if(this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked())
        {
            UnlockedSlots slots = this.getMenu().getUnlockedSlots();
            int experienceLevelCost = slots.nextInventorySlotUnlockCost();
            List<ClientTooltipComponent> components = new ArrayList<>();
            components.add(new ExperienceCostTooltip(experienceLevelCost));
            Component unlockHint = this.player.experienceLevel >= experienceLevelCost || this.player.isCreative()
                    ? Component.translatable("backpacked.gui.hold_to_unlock")
                    : Component.translatable("backpacked.gui.not_enough_exp").withStyle(ChatFormatting.RED);
            components.add(new ClientTextTooltip(unlockHint.getVisualOrderText()));
            ClientServices.CLIENT.drawTooltip(graphics, this.font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
            return;
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(button == 0 && this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked())
        {
            UnlockedSlots slots = this.getMenu().getUnlockedSlots();
            if(slots.isUnlockable(this.hoveredLockedSlot.getContainerSlot()))
            {
                int experienceLevelCost = slots.nextInventorySlotUnlockCost();
                if(this.player.experienceLevel >= experienceLevelCost || this.player.isCreative())
                {
                    this.heldUnlockTime = UNLOCK_TIME;
                    this.clickedLockedSlot = this.hoveredLockedSlot;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(button == 0 && this.clickedLockedSlot != null)
        {
            this.clickedLockedSlot = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
