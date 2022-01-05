package com.mrcrayfish.backpacked.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class BackpackScreen extends ContainerScreen<BackpackContainer>
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final ITextComponent CUSTOMISE_TOOLTIP = new TranslationTextComponent("backpacked.button.customise.tooltip");
    private static final ITextComponent CONFIG_TOOLTIP = new TranslationTextComponent("backpacked.button.config.tooltip");

    private final int rows;
    private final boolean owner;
    private boolean opened;

    public BackpackScreen(BackpackContainer backpackContainer, PlayerInventory playerInventory, ITextComponent titleIn)
    {
        super(backpackContainer, playerInventory, titleIn);
        this.rows = backpackContainer.getRows();
        this.owner = backpackContainer.isOwner();
        this.imageHeight = 114 + this.rows * 18;
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
        int titleWidth = minecraft.font.width(this.title);
        if(this.owner)
        {
            this.addButton(new MiniButton(this.leftPos + titleWidth + 8 + 3, this.topPos + 5, 225, 0, CustomiseBackpackScreen.GUI_TEXTURE, onPress -> {
                Network.getPlayChannel().sendToServer(new MessageRequestCustomisation());
            }, (button, matrixStack, mouseX, mouseY) -> {
                this.renderTooltip(matrixStack, CUSTOMISE_TOOLTIP, mouseX, mouseY);
            }));
        }
        this.addButton(new MiniButton(this.leftPos + titleWidth + 8 + 3 + (this.owner ? 13 : 0), this.topPos + 5, 235, 0, CustomiseBackpackScreen.GUI_TEXTURE, onPress -> {
            this.openConfigScreen();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, CONFIG_TOOLTIP, mouseX, mouseY);
        }));
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
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.rows * 18 + 17);
        this.blit(matrixStack, this.leftPos, this.topPos + this.rows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 0x404040);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), 8.0F, (float) (this.imageHeight - 96 + 2), 0x404040);
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
