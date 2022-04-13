package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BackpackScreen extends AbstractContainerScreen<BackpackContainerMenu>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/backpack.png");
    private static final Component CUSTOMISE_TOOLTIP = new TranslatableComponent("backpacked.button.customise.tooltip");
    private static final Component CONFIG_TOOLTIP = new TranslatableComponent("backpacked.button.config.tooltip");

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
                    button.x = this.leftPos + titleWidth + 8 + 3 + i * 13;
                }
                case RIGHT -> {
                    button.x = this.leftPos + this.imageWidth - 7 - 10 - (buttons.size() - 1 - i) * 13;
                }
            }
            button.y = this.topPos + 5;
            this.addRenderableWidget(button);
        }
    }

    private List<MiniButton> gatherButtons()
    {
        List<MiniButton> buttons = new ArrayList<>();
        boolean canCustomise = this.owner && !Config.SERVER.disableCustomisation.get();
        if(canCustomise)
        {
            buttons.add(new MiniButton(0, 0, 225, 0, CustomiseBackpackScreen.GUI_TEXTURE, onPress -> {
                Network.getPlayChannel().sendToServer(new MessageRequestCustomisation());
            }, (button, matrixStack, mouseX, mouseY) -> {
                this.renderTooltip(matrixStack, CUSTOMISE_TOOLTIP, mouseX, mouseY);
            }));
        }
        if(!Config.CLIENT.hideConfigButton.get())
        {
            buttons.add(new MiniButton(0, 0, 235, 0, CustomiseBackpackScreen.GUI_TEXTURE, onPress -> {
                this.openConfigScreen();
            }, (button, matrixStack, mouseX, mouseY) -> {
                this.renderTooltip(matrixStack, CONFIG_TOOLTIP, mouseX, mouseY);
            }));
        }
        return buttons;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack); //Draw background
        super.render(matrixStack, mouseX, mouseY, partialTicks); //Super
        this.renderTooltip(matrixStack, mouseX, mouseY); //Render hovered tooltips

        this.children().forEach(widget ->
        {
            if(widget instanceof Button button && button.isHoveredOrFocused())
            {
                button.renderToolTip(matrixStack, mouseX, mouseY);
            }
        });
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        this.drawBackgroundWindow(matrixStack, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY)
    {
        this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 0x404040);
        this.font.draw(matrixStack, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040);
    }

    private void drawBackgroundWindow(PoseStack poseStack, int x, int y, int width, int height)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        // Backpack Inventory
        int backpackHeight = 17 + this.rows * 18;
        blit(poseStack, x,             y,          7, backpackHeight, 0, 0, 7, backpackHeight, 256, 256); /* Top left corner */
        blit(poseStack, x + width - 7, y,          7, backpackHeight, 8, 0, 7, backpackHeight, 256, 256); /* Top right corner */
        blit(poseStack, x + 7,         y, width - 14, backpackHeight, 7, 0, 1, backpackHeight, 256, 256); /* Top border */

        // Draw Backpack Slots
        int slotWidth = this.cols * 18;
        int slotHeight = this.rows * 18;
        int minSlotWidth = 9 * 18; //Player inventory will always have 9 columns
        int backpackStartX = Math.max((minSlotWidth - slotWidth) / 2, 0);
        blit(poseStack, backpackStartX + x + 7, y + 17, slotWidth, slotHeight, 15, 0, slotWidth, slotHeight, 256, 256);

        // Player Inventory
        blit(poseStack, x,             y + backpackHeight,          7, 97, 0, 143,  7, 97, 256, 256); /* Bottom left corner */
        blit(poseStack, x + width - 7, y + backpackHeight,          7, 97, 8, 143,  7, 97, 256, 256); /* Bottom right corner */
        blit(poseStack, x + 7,         y + backpackHeight, width - 14, 97, 7, 143,  1, 97, 256, 256); /* Bottom border */

        // Draw Player Inventory Slots
        int inventoryStartX = Math.max((slotWidth - minSlotWidth) / 2, 0);
        blit(poseStack, x + inventoryStartX + 7, y + backpackHeight + 14, 163, 76, 15, 157, 163, 76, 256, 256);
    }

    private void openConfigScreen()
    {
        ModList.get().getModContainerById(Reference.MOD_ID).ifPresent(container ->
        {
            Screen screen = container.getCustomExtension(ConfigGuiHandler.ConfigGuiFactory.class).map(function -> function.screenFunction().apply(this.minecraft, null)).orElse(null);
            if(screen != null)
            {
                this.minecraft.setScreen(screen);
            }
            else if(this.minecraft != null && this.minecraft.player != null)
            {
                TextComponent modName = new TextComponent("Configured");
                modName.setStyle(modName.getStyle()
                        .withColor(ChatFormatting.YELLOW)
                        .withUnderlined(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("backpacked.chat.open_curseforge_page")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured")));
                Component message = new TranslatableComponent("backpacked.chat.install_configured", modName);
                this.minecraft.player.displayClientMessage(message, false);
            }
        });
    }
}
