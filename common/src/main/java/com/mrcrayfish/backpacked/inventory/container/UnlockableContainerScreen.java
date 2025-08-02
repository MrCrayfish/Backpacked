package com.mrcrayfish.backpacked.inventory.container;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.ExperienceCostTooltip;
import com.mrcrayfish.backpacked.client.gui.particle.Particle2D;
import com.mrcrayfish.backpacked.client.gui.particle.ScreenParticles;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public abstract class UnlockableContainerScreen<T extends AbstractContainerMenu & UnlockableController> extends AbstractContainerScreen<T>
{
    private static final ResourceLocation ICON_LOCK = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock");
    private static final int UNLOCK_TIME = 20;

    private final ScreenParticles screenParticles = new ScreenParticles();
    private final RandomSource random = RandomSource.create();

    private final Player player;
    private @Nullable UnlockableSlot hoveredLockedSlot;
    private UnlockableSlot clickedLockedSlot;
    private int heldUnlockTime;
    private UnlockableSlot lastUnlockedSlot;
    protected boolean hideLockedSlots;

    public UnlockableContainerScreen(T menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title);
        this.player = inventory.player;
    }

    public void setHideLockedSlots(boolean hideLockedSlots)
    {
        this.hideLockedSlots = hideLockedSlots;
    }

    @Override
    protected void containerTick()
    {
        this.screenParticles.tickParticles();

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
                this.lastUnlockedSlot = this.clickedLockedSlot;
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
        this.screenParticles.renderParticles(graphics, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);

        if(this.clickedLockedSlot != null && !this.hideLockedSlots)
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

                    if(this.hoveredLockedSlot != lockedSlot || this.hideLockedSlots)
                    {
                        graphics.fill(this.leftPos + slot.x, this.topPos + slot.y, this.leftPos + slot.x + 16, this.topPos + slot.y + 16, 0x88A89A8A);
                    }

                    if(this.hideLockedSlots)
                    {
                        graphics.fill(this.leftPos + slot.x - 1, this.topPos + slot.y - 1, this.leftPos + slot.x + 17, this.topPos + slot.y + 17, 0xAAEFDBC4);
                    }
                }
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if(this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked() && this.menu.getCarried().isEmpty() && !this.hideLockedSlots)
        {
            int experienceLevelCost = this.getMenu().getNextUnlockCost();
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
        if(button == 0 && this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked() && this.menu.getCarried().isEmpty() && !this.hideLockedSlots)
        {
            if(this.getMenu().canUnlockSlot(this.hoveredLockedSlot.getContainerSlot()))
            {
                int experienceLevelCost = this.getMenu().getNextUnlockCost();
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

    protected boolean canUnlockNextSlot()
    {
        int experienceLevelCost = this.getMenu().getNextUnlockCost();
        return this.player.experienceLevel >= experienceLevelCost || this.player.isCreative();
    }

    public void onSlotUnlocked()
    {
        if(this.lastUnlockedSlot != null)
        {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.3F, 0.5F));

            int slotX = this.leftPos + this.lastUnlockedSlot.x;
            int slotY = this.topPos + this.lastUnlockedSlot.y;

            Particle2D top = new Particle2D(slotX + 2, slotY + 2, 12, 6)
                .setLife(50)
                .setTexture(0F, 0F, 1F, 0.5F, ICON_LOCK)
                .setMotion(new Vector2d(this.random.nextIntBetweenInclusive(-20, 20), -100))
                .setRotationSpeed(this.random.nextIntBetweenInclusive(-180, 180))
                .setGravity(new Vector2d(0, 12))
                .setFriction(0.025)
                .setStartScale(1F, 40)
                .setEndScale(0F);
            this.screenParticles.addParticle(top);

            Particle2D bottom = new Particle2D(slotX + 2, slotY + 8, 12, 6)
                .setLife(50)
                .setTexture(0F, 0.5F, 1F, 1F, ICON_LOCK)
                .setMotion(new Vector2d(this.random.nextIntBetweenInclusive(-20, 20), 50))
                .setRotationSpeed(this.random.nextIntBetweenInclusive(-180, 180))
                .setGravity(new Vector2d(0, 12))
                .setFriction(0.025)
                .setStartScale(1F, 40)
                .setEndScale(0F);
            this.screenParticles.addParticle(bottom);

            this.lastUnlockedSlot = null;
        }
    }
}
