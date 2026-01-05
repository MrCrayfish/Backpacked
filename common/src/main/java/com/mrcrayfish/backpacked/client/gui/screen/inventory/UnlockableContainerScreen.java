package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.ExperienceCostTooltip;
import com.mrcrayfish.backpacked.client.gui.ItemCostTooltip;
import com.mrcrayfish.backpacked.client.gui.particle.Particle2D;
import com.mrcrayfish.backpacked.client.gui.particle.ScreenParticles;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.CustomContainerScreen;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.inventory.container.slot.UnlockableSlot;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageUnlockSlot;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class UnlockableContainerScreen<T extends AbstractContainerMenu> extends CustomContainerScreen<T>
{
    private static final Component HOLD_TO_UNLOCK = Component.translatable("backpacked.gui.hold_to_unlock");
    private static final ResourceLocation ICON_LOCK = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock");
    private static final ResourceLocation ICON_LOCK_OUTLINED = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock_outlined");
    private static final ResourceLocation EXP_ORB = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/exp_orb");
    private static final int UNLOCK_TIME = 20;

    private final ScreenParticles screenParticles = new ScreenParticles();
    private final RandomSource random = RandomSource.create();

    private final Player player;
    protected final Set<UnlockableSlot> selectedSlots = new LinkedHashSet<>();
    private @Nullable UnlockableSlot lastAddedUnlockableSlot;
    private @Nullable UnlockableSlot hoveredLockedSlot;
    private int heldUnlockTime;
    private int totalUnlockTime;
    protected boolean hideLockedSlots;
    protected boolean preventNextRelease;

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

        if(!this.selectedSlots.isEmpty())
        {
            if(this.hoveredLockedSlot == null)
            {
                this.selectedSlots.clear();
                this.lastAddedUnlockableSlot = null;
                return;
            }

            if(this.heldUnlockTime-- <= 0)
            {
                List<Integer> slotIndexes = this.selectedSlots.stream().map(slot -> slot.index).toList();
                Network.PLAY.sendToServer(new MessageUnlockSlot(slotIndexes));
                this.selectedSlots.clear();
                this.lastAddedUnlockableSlot = null;
            }
            else if(this.heldUnlockTime % 2 == 0)
            {
                float pitch = 0.7F + 0.6F * (this.totalUnlockTime - this.heldUnlockTime) / (float) Math.max(1, this.totalUnlockTime);
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
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.screenParticles.renderParticles(graphics, partialTicks);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);

        if(!this.selectedSlots.isEmpty())
        {
            int progressWidth = (int) (16 * (this.totalUnlockTime - this.heldUnlockTime) / (float) Math.max(1, this.totalUnlockTime));
            for(UnlockableSlot slot : this.selectedSlots)
            {
                int progressX = this.leftPos + slot.x;
                int progressY = this.topPos + slot.y;
                graphics.fill(progressX, progressY, progressX + progressWidth, progressY + 16, 0x88A7FF4C);
            }
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
                    if(this.selectedSlots.contains(slot))
                    {
                        graphics.blitSprite(ICON_LOCK_OUTLINED, this.leftPos + slot.x + 1, this.topPos + slot.y + 1, 14, 14);
                    }
                    else
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
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if(this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked() && this.menu.getCarried().isEmpty() && (!this.hideLockedSlots || !this.selectedSlots.isEmpty()))
        {
            List<ClientTooltipComponent> components = this.createUnlockTooltip(this.hoveredLockedSlot);
            ClientServices.CLIENT.drawTooltip(graphics, this.font, components, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE);
            return;
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    protected List<ClientTooltipComponent> createUnlockTooltip(UnlockableSlot slot)
    {
        Component hintText = HOLD_TO_UNLOCK;
        int nextCost = slot.getNextUnlockCost(Math.max(1, this.selectedSlots.size()));
        boolean canAfford = slot.canAffordToUnlock(this.player, Math.max(1, this.selectedSlots.size())); // TODO this call is somewhat expensive if looking for items
        List<ClientTooltipComponent> components = new ArrayList<>();
        switch(slot.getController().getCostModel().getPaymentType())
        {
            case EXPERIENCE ->
            {
                if(!canAfford)
                {
                    hintText = Component.translatable("backpacked.gui.not_enough_exp").withStyle(ChatFormatting.RED);
                }
                components.add(new ExperienceCostTooltip(nextCost));
                components.add(new ClientTextTooltip(hintText.getVisualOrderText()));
            }
            case ITEM ->
            {
                if(!canAfford)
                {
                    hintText = Component.translatable("backpacked.gui.missing_items").withStyle(ChatFormatting.RED);
                }
                components.add(new ItemCostTooltip(slot.getController().getPaymentItem(), nextCost));
                components.add(new ClientTextTooltip(hintText.getVisualOrderText()));
            }
        }
        return components;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(!this.hasPopupMenu() && button == 0 && this.hoveredLockedSlot != null && !this.hoveredLockedSlot.isUnlocked() && !this.hideLockedSlots)
        {
            if(this.isHoldingUnlockToken())
            {
                Network.PLAY.sendToServer(new MessageUnlockSlot(List.of(this.hoveredLockedSlot.index)));
                this.preventNextRelease = true;
                return true;
            }
            else if(this.menu.getCarried().isEmpty() && this.hoveredLockedSlot.canAffordToUnlock(this.player, 1))
            {
                this.addSlotToSelected(this.hoveredLockedSlot);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if(!this.hasPopupMenu() && button == 0 && this.lastAddedUnlockableSlot != null && this.hoveredLockedSlot != null && this.lastAddedUnlockableSlot != this.hoveredLockedSlot && !this.hoveredLockedSlot.isUnlocked() && !this.isHoldingUnlockToken())
        {
            if(!this.selectedSlots.contains(this.hoveredLockedSlot))
            {
                if(this.hoveredLockedSlot.canAffordToUnlock(this.player, this.selectedSlots.size() + 1))
                {
                    this.addSlotToSelected(this.hoveredLockedSlot);
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    protected void addSlotToSelected(UnlockableSlot slot)
    {
        this.selectedSlots.add(slot);
        this.lastAddedUnlockableSlot = slot;
        this.totalUnlockTime = Mth.clamp(UNLOCK_TIME + 4 * (this.selectedSlots.size() - 1), 20, 80);
        this.heldUnlockTime = this.totalUnlockTime;

        float pitch = 0.6F + 0.4F * this.random.nextFloat();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.COPPER_HIT, pitch, 0.6F));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.preventNextRelease)
        {
            this.preventNextRelease = false;
            if(this.isHoldingUnlockToken())
            {
                return true;
            }
        }
        if(button == 0 && !this.selectedSlots.isEmpty())
        {
            this.selectedSlots.clear();
            this.lastAddedUnlockableSlot = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void onSlotUnlocked(List<Integer> slotIndexes)
    {
        boolean playSound = false;
        for(int slotIndex : slotIndexes)
        {
            if(slotIndex < 0 || slotIndex >= this.menu.slots.size())
                continue;

            Slot slot = this.menu.getSlot(slotIndex);
            if(!(slot instanceof UnlockableSlot))
                return;

            int slotX = this.leftPos + slot.x;
            int slotY = this.topPos + slot.y;
            this.spawnSlotUnlockedParticles(slotX, slotY);
            playSound = true;
        }

        if(playSound)
        {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.3F, 0.25F));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.CHAIN_BREAK, 1.3F, 0.7F));
        }
    }

    protected void spawnSlotUnlockedParticles(int slotX, int slotY)
    {
        Particle2D top = new Particle2D(slotX + 2, slotY + 2, 12, 6)
            .setLife(50)
            .setSprite(0F, 0F, 1F, 0.5F, ICON_LOCK)
            .setMotion(new Vector2d(this.random.nextIntBetweenInclusive(-20, 20), -100))
            .setRotationSpeed(this.random.nextIntBetweenInclusive(-180, 180))
            .setGravity(new Vector2d(0, 12))
            .setFriction(0.025)
            .setStartScale(1F, 40)
            .setEndScale(0F);
        this.screenParticles.addParticle(top);

        Particle2D bottom = new Particle2D(slotX + 2, slotY + 8, 12, 6)
            .setLife(50)
            .setSprite(0F, 0.5F, 1F, 1F, ICON_LOCK)
            .setMotion(new Vector2d(this.random.nextIntBetweenInclusive(-20, 20), 50))
            .setRotationSpeed(this.random.nextIntBetweenInclusive(-180, 180))
            .setGravity(new Vector2d(0, 12))
            .setFriction(0.025)
            .setStartScale(1F, 40)
            .setEndScale(0F);
        this.screenParticles.addParticle(bottom);

        for(int i = 0; i < 10; i++)
        {
            Particle2D damageParticle = new Particle2D(slotX + 7, slotY + 7, 2, 2)
                .setLife(20)
                .setSprite(0.45F, 0.5F, 0.55F, 0.6F, ICON_LOCK)
                .setMotion(new Vector2d(Mth.cos(2 * Mth.PI * this.random.nextFloat()), Mth.sin(2 * Mth.PI * this.random.nextFloat())).mul(this.random.nextIntBetweenInclusive(50, 100)))
                .setRotationSpeed(180)
                .setGravity(new Vector2d(0, 12))
                .setFriction(0.05)
                .setStartScale(1F)
                .setEndScale(0F);
            this.screenParticles.addParticle(damageParticle);
        }

        if(Config.CLIENT.glitterBomb.get())
        {
            for(int i = 0; i < 200; i++)
            {
                Particle2D expOrbParticle = new Particle2D(slotX + 6, slotY + 6, 4, 4)
                    .setLife(50)
                    .setSprite(0F, 0F, 1F, 1F, EXP_ORB)
                    .setMotion(new Vector2d(Mth.cos(2 * Mth.PI * this.random.nextFloat()), Mth.sin(2 * Mth.PI * this.random.nextFloat())).mul(this.random.nextIntBetweenInclusive(1, 500)))
                    .setRotationSpeed(180)
                    .setGravity(new Vector2d(0, 20))
                    .setFriction(0.05)
                    .setStartScale(1F)
                    .setEndScale(0F);
                this.screenParticles.addParticle(expOrbParticle);
            }
        }
    }

    protected boolean isHoldingUnlockToken()
    {
        return this.menu.getCarried().is(ModItems.UNLOCK_TOKEN.get());
    }
}
