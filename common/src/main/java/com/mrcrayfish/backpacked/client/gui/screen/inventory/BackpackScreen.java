package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.Keys;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BackpackScreen extends AbstractContainerScreen<BackpackContainerMenu>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/backpack.png");
    private static final Component CUSTOMISE_TOOLTIP = Component.translatable("backpacked.button.customise.tooltip");
    private static final Component CONFIG_TOOLTIP = Component.translatable("backpacked.button.config.tooltip");

    private final int cols;
    private final int rows;
    private final boolean owner;
    private boolean opened;

    public BackpackScreen(BackpackContainerMenu backpackContainerMenu, Inventory playerInventory, Component titleIn)
    {
        super(backpackContainerMenu, playerInventory, titleIn);
        this.cols = backpackContainerMenu.getCols();
        this.rows = backpackContainerMenu.getRows();
        this.owner = backpackContainerMenu.isOwner();
        this.imageWidth = 14 + Math.max(this.cols, 9) * 18;
        this.imageHeight = 114 + this.rows * 18;
        this.inventoryLabelX = Math.max(((this.cols * 18) - (9 * 18)) / 2, 0) + 7;
        this.inventoryLabelY = this.rows * 18 + 17 + 4;
    }

    @Override
    public void init()
    {
        super.init();
        if(!this.opened)
        {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_LEATHER, 0.75F, 1.0F));
            this.opened = true;
        }

        List<MiniButton> buttons = this.gatherButtons();
        for(int i = 0; i < buttons.size(); i++)
        {
            MiniButton button = buttons.get(i);
            switch(Config.CLIENT.buttonAlignment.get())
            {
                case LEFT -> {
                    int titleWidth = this.minecraft.font.width(this.title);
                    button.setX(this.leftPos + titleWidth + 8 + 3 + i * 13);
                }
                case RIGHT -> {
                    button.setX(this.leftPos + this.imageWidth - 7 - 10 - (buttons.size() - 1 - i) * 13);
                }
            }
            button.setY(this.topPos + 5);
            this.addRenderableWidget(button);
        }
    }

    private List<MiniButton> gatherButtons()
    {
        List<MiniButton> buttons = new ArrayList<>();
        boolean canCustomise = this.owner && !Config.SERVER.backpack.disableCustomisation.get();
        if(canCustomise)
        {
            MiniButton customiseButton = new MiniButton(0, 0, 225, 0, CustomiseBackpackScreen.GUI_TEXTURE, onPress -> {
                Network.getPlay().sendToServer(new MessageRequestCustomisation());
            });
            customiseButton.setTooltip(Tooltip.create(CUSTOMISE_TOOLTIP));
            buttons.add(customiseButton);
        }
        if(!Config.CLIENT.hideConfigButton.get())
        {
            MiniButton configButton = new MiniButton(0, 0, 235, 0, CustomiseBackpackScreen.GUI_TEXTURE, onPress -> this.openConfigScreen());
            configButton.setTooltip(Tooltip.create(CONFIG_TOOLTIP));
            buttons.add(configButton);
        }
        return buttons;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(graphics); //Draw background
        super.render(graphics, mouseX, mouseY, partialTicks); //Super
        this.renderTooltip(graphics, mouseX, mouseY); //Render hovered tooltips
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        this.drawBackgroundWindow(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    private void drawBackgroundWindow(GuiGraphics graphics, int x, int y, int width, int height)
    {
        // Backpack Inventory
        int backpackHeight = 17 + this.rows * 18;
        graphics.blit(GUI_TEXTURE, x,             y,          7, backpackHeight, 0, 0, 7, backpackHeight, 256, 256); /* Top left corner */
        graphics.blit(GUI_TEXTURE, x + width - 7, y,          7, backpackHeight, 8, 0, 7, backpackHeight, 256, 256); /* Top right corner */
        graphics.blit(GUI_TEXTURE, x + 7,         y, width - 14, backpackHeight, 7, 0, 1, backpackHeight, 256, 256); /* Top border */

        // Draw Backpack Slots
        int slotWidth = this.cols * 18;
        int slotHeight = this.rows * 18;
        int minSlotWidth = 9 * 18; //Player inventory will always have 9 columns
        int backpackStartX = Math.max((minSlotWidth - slotWidth) / 2, 0);
        graphics.blit(GUI_TEXTURE, backpackStartX + x + 7, y + 17, slotWidth, slotHeight, 15, 0, slotWidth, slotHeight, 256, 256);

        // Player Inventory
        graphics.blit(GUI_TEXTURE, x,             y + backpackHeight,          7, 97, 0, 143,  7, 97, 256, 256); /* Bottom left corner */
        graphics.blit(GUI_TEXTURE, x + width - 7, y + backpackHeight,          7, 97, 8, 143,  7, 97, 256, 256); /* Bottom right corner */
        graphics.blit(GUI_TEXTURE, x + 7,         y + backpackHeight, width - 14, 97, 7, 143,  1, 97, 256, 256); /* Bottom border */

        // Draw Player Inventory Slots
        int inventoryStartX = Math.max((slotWidth - minSlotWidth) / 2, 0);
        graphics.blit(GUI_TEXTURE, x + inventoryStartX + 7, y + backpackHeight + 14, 163, 76, 15, 157, 163, 76, 256, 256);
    }

    private void openConfigScreen()
    {
        ClientServices.SCREEN.openConfigScreen();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int action)
    {
        if(Keys.KEY_BACKPACK.matches(key, scanCode))
        {
            this.onClose();
            return true;
        }
        return super.keyPressed(key, scanCode, action);
    }
}
