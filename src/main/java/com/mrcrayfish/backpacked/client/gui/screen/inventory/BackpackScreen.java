package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.gui.screen.CustomiseBackpackScreen;
import com.mrcrayfish.backpacked.client.gui.screen.widget.MiniButton;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageRequestCustomisation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BackpackScreen extends ContainerScreen<BackpackContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/backpack.png");
    private static final ITextComponent CUSTOMISE_TOOLTIP = new TranslationTextComponent("backpacked.button.customise.tooltip");
    private static final ITextComponent CONFIG_TOOLTIP = new TranslationTextComponent("backpacked.button.config.tooltip");

    private final int cols;
    private final int rows;
    private final boolean owner;
    private boolean opened;

    public BackpackScreen(BackpackContainer backpackContainer, PlayerInventory playerInventory, ITextComponent titleIn)
    {
        super(backpackContainer, playerInventory, titleIn);
        this.cols = backpackContainer.getCols();
        this.rows = backpackContainer.getRows();
        this.owner = backpackContainer.isOwner();
        this.imageWidth = 14 + Math.max(this.cols, 9) * 18;
        this.imageHeight = 114 + this.rows * 18;
        this.inventoryLabelX = Math.max(((this.cols * 18) - (9 * 18)) / 2, 0) + 7;
        this.inventoryLabelY = this.rows * 18 + 17 + 4;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height)
    {
        super.init(minecraft, width, height);
        if(!this.opened)
        {
            minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.ARMOR_EQUIP_LEATHER, 0.75F, 1.0F));
            this.opened = true;
        }

        List<MiniButton> buttons = this.gatherButtons();
        for(int i = 0; i < buttons.size(); i++)
        {
            MiniButton button = buttons.get(i);
            button.x = this.leftPos + this.imageWidth - 7 - 10 - (buttons.size() - 1 - i) * 13;
            button.y = this.topPos + 5;
            this.addButton(button);
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack); //Draw background
        super.render(matrixStack, mouseX, mouseY, partialTicks); //Super
        this.renderTooltip(matrixStack, mouseX, mouseY); //Render hovered tooltips
        this.buttons.forEach(widget -> {
            if(widget.isHovered()) {
                widget.renderToolTip(matrixStack, mouseX, mouseY);
            }
        });
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        this.drawBackgroundWindow(matrixStack, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        //this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.rows * 18 + 17);
        //this.blit(matrixStack, this.leftPos, this.topPos + this.rows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 0x404040);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), this.inventoryLabelX, this.inventoryLabelY, 0x404040);
    }

    private void drawBackgroundWindow(MatrixStack matrixStack, int x, int y, int width, int height)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        Minecraft.getInstance().getTextureManager().bind(GUI_TEXTURE);

        // Backpack Inventory
        int backpackHeight = 17 + this.rows * 18;
        blit(matrixStack, x,             y,          7, backpackHeight, 0, 0, 7, backpackHeight, 256, 256); /* Top left corner */
        blit(matrixStack, x + width - 7, y,          7, backpackHeight, 8, 0, 7, backpackHeight, 256, 256); /* Top right corner */
        blit(matrixStack, x + 7,         y, width - 14, backpackHeight, 7, 0, 1, backpackHeight, 256, 256); /* Top border */

        // Draw Backpack Slots
        int slotWidth = this.cols * 18;
        int slotHeight = this.rows * 18;
        int minSlotWidth = 9 * 18; //Player inventory will always have 9 columns
        int backpackStartX = Math.max((minSlotWidth - slotWidth) / 2, 0);
        blit(matrixStack, backpackStartX + x + 7, y + 17, slotWidth, slotHeight, 15, 0, slotWidth, slotHeight, 256, 256);

        // Player Inventory
        blit(matrixStack, x,             y + backpackHeight,          7, 97, 0, 143,  7, 97, 256, 256); /* Bottom left corner */
        blit(matrixStack, x + width - 7, y + backpackHeight,          7, 97, 8, 143,  7, 97, 256, 256); /* Bottom right corner */
        blit(matrixStack, x + 7,         y + backpackHeight, width - 14, 97, 7, 143,  1, 97, 256, 256); /* Bottom border */

        // Draw Player Inventory Slots
        int inventoryStartX = Math.max((slotWidth - minSlotWidth) / 2, 0);
        blit(matrixStack, x + inventoryStartX + 7, y + backpackHeight + 14, 163, 76, 15, 157, 163, 76, 256, 256);
    }

    private void openConfigScreen()
    {
        ModList.get().getModContainerById(Reference.MOD_ID).ifPresent(container ->
        {
            Screen screen = container.getCustomExtension(ExtensionPoint.CONFIGGUIFACTORY).map(function -> function.apply(this.minecraft, null)).orElse(null);
            if(screen != null)
            {
                this.minecraft.setScreen(screen);
            }
            else if(this.minecraft != null && this.minecraft.player != null)
            {
                StringTextComponent modName = new StringTextComponent("Configured");
                modName.setStyle(modName.getStyle()
                        .withColor(TextFormatting.YELLOW)
                        .withUnderlined(true)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("backpacked.chat.open_curseforge_page")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/configured")));
                ITextComponent message = new TranslationTextComponent("backpacked.chat.install_configured", modName);
                this.minecraft.player.displayClientMessage(message, false);
            }
        });
    }
}
