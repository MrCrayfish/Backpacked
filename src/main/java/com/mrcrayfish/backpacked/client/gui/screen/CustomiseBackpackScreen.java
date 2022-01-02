package com.mrcrayfish.backpacked.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ClientEvents;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CheckBox;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.BackpackManager;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class CustomiseBackpackScreen extends Screen
{
    public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/customise_backpack.png");
    private static final ITextComponent SHOW_EFFECTS_TOOLTIP = new TranslationTextComponent("backpacked.button.show_effects.tooltip");
    private static final ITextComponent SHOW_WITH_ELYTRA_TOOLTIP = new TranslationTextComponent("backpacked.button.show_with_elytra.tooltip");
    private static final ITextComponent LOCKED = new TranslationTextComponent("backpacked.gui.locked").withStyle(TextFormatting.RED, TextFormatting.BOLD);

    private final int windowWidth;
    private final int windowHeight;
    private int windowLeft;
    private int windowTop;
    private float windowRotationX = -35F;
    private float windowRotationY = 10;
    private boolean windowGrabbed;
    private boolean scrollGrabbed;
    private int mouseClickedX, mouseClickedY;
    private Button resetButton;
    private Button saveButton;
    private CheckBox showWithElytraButton;
    private CheckBox showEffectsButton;
    private String displayBackpackModel = null;
    private boolean displayShowWithElytra;
    private boolean displayShowEffects;
    private final List<BackpackModelEntry> models;
    private int scroll;

    public CustomiseBackpackScreen(Map<ResourceLocation, ITextComponent> progressMap)
    {
        super(new TranslationTextComponent("backpacked.title.customise_backpack"));
        this.windowWidth = 201;
        this.windowHeight = 166;
        Comparator<BackpackModelEntry> compareUnlock = Comparator.comparing(e -> !e.backpack.isUnlocked(Minecraft.getInstance().player));
        Comparator<BackpackModelEntry> compareLabel = Comparator.comparing(e -> e.label.getString());
        List<BackpackModelEntry> models = BackpackManager.instance().getRegisteredBackpacks()
                .stream()
                .map(backpack -> new BackpackModelEntry(backpack, progressMap))
                .sorted(compareUnlock.thenComparing(compareLabel))
                .collect(Collectors.toList());
        this.models = ImmutableList.copyOf(models);
    }

    @Override
    protected void init()
    {
        super.init();
        if(this.displayBackpackModel == null)
        {
            this.displayBackpackModel = this.getBackpackModel();
            this.displayShowWithElytra = this.getLocalBackpackProperty(BackpackModelProperty.SHOW_WITH_ELYTRA);
            this.displayShowEffects = this.getLocalBackpackProperty(BackpackModelProperty.SHOW_EFFECTS);
        }
        this.windowLeft = (this.width - this.windowWidth) / 2;
        this.windowTop = (this.height - this.windowHeight) / 2;
        this.resetButton = this.addButton(new Button(this.windowLeft + 7, this.windowTop + 114, 71, 20, new TranslationTextComponent("backpacked.button.reset"), onPress -> {
            this.displayBackpackModel = "";
        }));
        this.saveButton = this.addButton(new Button(this.windowLeft + 7, this.windowTop + 137, 71, 20, new TranslationTextComponent("backpacked.button.save"), onPress -> {
            Network.getPlayChannel().sendToServer(new MessageBackpackCosmetics(new ResourceLocation(this.displayBackpackModel), this.displayShowWithElytra, this.displayShowEffects));
        }));
        this.showWithElytraButton = this.addButton(new CheckBox(this.windowLeft + 160, this.windowTop + 6, StringTextComponent.EMPTY, onPress -> {
            this.displayShowWithElytra = !this.displayShowWithElytra;
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, SHOW_WITH_ELYTRA_TOOLTIP, mouseX, mouseY);
        }));
        this.showEffectsButton = this.addButton(new CheckBox(this.windowLeft + 186, this.windowTop + 6, StringTextComponent.EMPTY, onPress -> {
            this.displayShowEffects = !this.displayShowEffects;
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, SHOW_EFFECTS_TOOLTIP, mouseX, mouseY);
        }));
        ItemStack backpack = Backpacked.getBackpackStack(this.minecraft.player);
        if(!backpack.isEmpty())
        {
            this.showWithElytraButton.setChecked(BackpackLayer.canRenderWithElytra(backpack));
            this.showEffectsButton.setChecked(ClientEvents.canShowBackpackEffects(backpack));
        }
        this.updateButtons();
    }

    private void updateButtons()
    {
        this.resetButton.active = !this.getBackpackModel().isEmpty();
        this.saveButton.active = this.needsToSave();
    }

    private boolean needsToSave()
    {
        if(!this.displayBackpackModel.equals(this.getBackpackModel()))
        {
            return true;
        }
        else if (this.getLocalBackpackProperty(BackpackModelProperty.SHOW_EFFECTS) != this.displayShowEffects)
        {
            return true;
        }
        return this.getLocalBackpackProperty(BackpackModelProperty.SHOW_WITH_ELYTRA) != this.displayShowWithElytra;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void tick()
    {
        super.tick();
        this.updateButtons();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        this.blit(matrixStack, this.windowLeft, this.windowTop, 0, 0, this.windowWidth, this.windowHeight);
        super.render(matrixStack, mouseX, mouseY, partialTick);

        // Draw player in window
        if(this.minecraft.player != null)
        {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            ScreenUtil.scissor(this.windowLeft + 8, this.windowTop + 18, 69, 92);
            this.renderPlayer(this.windowLeft + 42, this.windowTop + this.windowHeight / 2, mouseX, mouseY, this.minecraft.player);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        // Draw title
        this.font.draw(matrixStack, this.title, this.windowLeft + 8, this.windowTop + 6, 4210752);

        // Draw scroll bar
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        boolean canScroll = this.models.size() > 7;
        int scroll = (canScroll ? this.scroll : 0) + (this.scrollGrabbed ? mouseY - this.mouseClickedY : 0);
        scroll = MathHelper.clamp(scroll, 0, 123);
        this.blit(matrixStack, this.windowLeft + 181, this.windowTop + 18 + scroll, 201 + (!canScroll ? 12 : 0), 0, 12, 15);

        // Draw backpack items
        int startIndex = (int) (Math.max(0, this.models.size() - 7) * MathHelper.clamp((scroll + 15.0) / 123.0, 0.0, 1.0));
        for(int i = startIndex; i < this.models.size() && i < startIndex + 7; i++)
        {
            this.drawBackpackItem(matrixStack, this.windowLeft + 82, this.windowTop + 17 + (i - startIndex) * 20, mouseX, mouseY, this.models.get(i));
        }

        int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
        if(hoveredIndex != -1)
        {
            BackpackModelEntry entry = this.models.get(hoveredIndex);
            if(!entry.getBackpack().isUnlocked(this.minecraft.player))
            {
                this.renderTooltip(matrixStack, entry.getUnlockTooltip(), mouseX, mouseY);
                return;
            }
        }

        this.buttons.forEach(widget -> {
            if(widget.isHovered()) {
                widget.renderToolTip(matrixStack, mouseX, mouseY);
            }
        });
    }

    private void drawBackpackItem(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, BackpackModelEntry entry)
    {
        boolean unlocked = entry.getBackpack().isUnlocked(this.minecraft.player);
        boolean selected = unlocked && entry.getId().equals(this.displayBackpackModel);
        boolean hovered = unlocked && !selected && ScreenUtil.isPointInArea(mouseX, mouseY, x, y, 97, 20);

        // Draw background for item
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        int offset = (unlocked ? 0 : 60) + (selected ? 20 : 0) + (hovered ? 40 : 0);
        this.blit(matrixStack, x, y, 0, 166 + offset, 97, 20);

        // Draw label. TODO convert dumb values into readable hex
        int color = selected ? 4226832 : (hovered ? 16777088 : (unlocked ? 6839882 : 0x4E1C1C));
        this.font.draw(matrixStack, entry.getLabel(), x + 20, y + 6, color);

        // Draw backpack model
        drawBackpackModel(matrixStack, entry.getBackpack().getModel(), x + 8, y + 4, 20);
    }

    public static void drawBackpackModel(MatrixStack matrixStack, BackpackModel model, int x, int y, float scale)
    {
        matrixStack.pushPose();
        matrixStack.translate(x, y, 50);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-10F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(35F));
        matrixStack.scale(scale, scale, scale);
        RenderHelper.setupForFlatItems();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.pushMatrix();
        IRenderTypeBuffer.Impl source = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        IVertexBuilder builder = source.getBuffer(model.renderType(model.getTextureLocation()));
        model.getStraps().visible = false;
        ModelRenderer bag = model.getBag();
        bag.setPos(0, 0, 0);
        bag.render(matrixStack, builder, 15728880, OverlayTexture.NO_OVERLAY);
        source.endBatch();
        RenderSystem.popMatrix();
        matrixStack.popPose();
    }

    private int getHoveredIndex(int mouseX, int mouseY)
    {
        if(ScreenUtil.isPointInArea(mouseX, mouseY, this.windowLeft + 82, this.windowTop + 17, 97, 140))
        {
            int startIndex = (int) (Math.max(0, this.models.size() - 7) * MathHelper.clamp((this.scroll + 15.0) / 123.0, 0.0, 1.0));
            int displayIndex = (mouseY - this.windowTop - 17) / 20;
            int actualIndex = startIndex + displayIndex;
            if(actualIndex >= 0 && actualIndex < this.models.size())
            {
                return actualIndex;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + 82, this.windowTop + 17, 97, 140))
        {
            if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                int hoveredIndex = this.getHoveredIndex((int) mouseX, (int) mouseY);
                if(hoveredIndex != -1)
                {
                    BackpackModelEntry entry = this.models.get(hoveredIndex);
                    if(entry.getBackpack().isUnlocked(this.minecraft.player))
                    {
                        this.displayBackpackModel = entry.getId();
                        this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                }
            }
        }
        else if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + 8, this.windowTop + 18, 69, 92))
        {
            if(!this.windowGrabbed && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                this.windowGrabbed = true;
                this.mouseClickedX = (int) mouseX;
                this.mouseClickedY = (int) mouseY;
                return true;
            }
        }
        else if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + 181, this.windowTop + 18 + this.scroll, 12, 15))
        {
            if(!this.scrollGrabbed && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                this.scrollGrabbed = true;
                this.mouseClickedY = (int) mouseY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.windowGrabbed)
        {
            if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                this.windowRotationX += (mouseX - this.mouseClickedX);
                this.windowRotationY += (mouseY - this.mouseClickedY);
                this.windowGrabbed = false;
            }
        }
        if(this.scrollGrabbed)
        {
            if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                this.scroll += (mouseY - this.mouseClickedY);
                this.scroll = MathHelper.clamp(this.scroll, 0, 123);
                this.scrollGrabbed = false;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + 82, this.windowTop + 17, 112, 140))
        {
            int startIndex = (int) (Math.max(0, this.models.size() - 7) * MathHelper.clamp((this.scroll + 15.0) / 123.0, 0.0, 1.0));
            int newIndex = startIndex - (int) Math.signum(scroll);
            this.scrollToIndex(newIndex);
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    private void scrollToIndex(int index)
    {
        this.scroll = (int) (123.0 * ((double) index / (double) Math.max(this.models.size() - 7, 1)));
        this.scroll = MathHelper.clamp(this.scroll, 0, 123);
    }

    private String getBackpackModel()
    {
        ItemStack stack = Backpacked.getBackpackStack(this.minecraft.player);
        if(!stack.isEmpty())
        {
            return stack.getOrCreateTag().getString("BackpackModel");
        }
        return "";
    }

    private void setLocalBackpackModel(String model)
    {
        ItemStack stack = Backpacked.getBackpackStack(this.minecraft.player);
        if(!stack.isEmpty())
        {
            stack.getOrCreateTag().putString("BackpackModel", model);
        }
    }

    private boolean getLocalBackpackProperty(BackpackModelProperty property)
    {
        ItemStack stack = Backpacked.getBackpackStack(this.minecraft.player);
        if(!stack.isEmpty())
        {
            CompoundNBT tag = stack.getOrCreateTag();
            if(tag.contains(property.getTagName(), Constants.NBT.TAG_BYTE))
            {
                return tag.getBoolean(property.getTagName());
            }
        }
        return property.getDefaultValue();
    }

    private void setLocalBackpackProperty(BackpackModelProperty property, boolean value)
    {
        ItemStack stack = Backpacked.getBackpackStack(this.minecraft.player);
        if(!stack.isEmpty())
        {
            stack.getOrCreateTag().putBoolean(property.getTagName(), value);
        }
    }

    private void renderPlayer(int x, int y, int mouseX, int mouseY, PlayerEntity player)
    {
        float scale = 70F;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)x, (float)y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0, 0, 1000);
        matrixStack.translate(0, -15, 0);
        Quaternion playerRotation = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion cameraRotation = new Quaternion(0F, 0F, 0F, true);
        cameraRotation.mul(Vector3f.XN.rotationDegrees(this.windowRotationY + (this.windowGrabbed ? mouseY - this.mouseClickedY : 0)));
        cameraRotation.mul(Vector3f.YP.rotationDegrees(this.windowRotationX + (this.windowGrabbed ? mouseX - this.mouseClickedX : 0)));
        playerRotation.mul(cameraRotation);
        matrixStack.mulPose(playerRotation);
        matrixStack.translate(0, -this.windowHeight / 2, 0);
        matrixStack.scale(scale, scale, scale);
        float origBodyRot = player.yBodyRot;
        float origYaw = player.yRot;
        float origPitch = player.xRot;
        float origHeadYawOld = player.yHeadRotO;
        float origHeadYaw = player.yHeadRot;
        String origBackpackModel = this.getBackpackModel();
        boolean origShowWithElytra = this.getLocalBackpackProperty(BackpackModelProperty.SHOW_WITH_ELYTRA);
        boolean origShowEffects = this.getLocalBackpackProperty(BackpackModelProperty.SHOW_EFFECTS);
        player.yBodyRot = 0.0F;
        player.yRot = 0.0F;
        player.xRot = 0.0F;
        player.yHeadRot = player.yRot;
        player.yHeadRotO = player.yRot;
        this.setLocalBackpackModel(this.displayBackpackModel);
        this.setLocalBackpackProperty(BackpackModelProperty.SHOW_WITH_ELYTRA, this.displayShowWithElytra);
        this.setLocalBackpackProperty(BackpackModelProperty.SHOW_EFFECTS, this.displayShowEffects);
        EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
        cameraRotation.conj();
        manager.overrideCameraOrientation(cameraRotation);
        manager.setRenderShadow(false);
        IRenderTypeBuffer.Impl source = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> manager.render(player, 0, 0.0625, 0.35, 0, 1, matrixStack, source, 15728880));
        source.endBatch();
        manager.setRenderShadow(true);
        player.yBodyRot = origBodyRot;
        player.yRot = origYaw;
        player.xRot = origPitch;
        player.yHeadRotO = origHeadYawOld;
        player.yHeadRot = origHeadYaw;
        this.setLocalBackpackModel(origBackpackModel);
        this.setLocalBackpackProperty(BackpackModelProperty.SHOW_WITH_ELYTRA, origShowWithElytra);
        this.setLocalBackpackProperty(BackpackModelProperty.SHOW_EFFECTS, origShowEffects);
        RenderSystem.popMatrix();
    }

    private static class BackpackModelEntry
    {
        private final String id;
        private final Backpack backpack;
        private final ITextComponent label;
        private final List<IReorderingProcessor> unlockTooltip;

        public BackpackModelEntry(Backpack backpack, Map<ResourceLocation, ITextComponent> progressMap)
        {
            this.id = backpack.getId().toString();
            this.backpack = backpack;
            this.label = new TranslationTextComponent(backpack.getId().getNamespace() + ".backpack." + backpack.getId().getPath());
            ITextComponent unlockMessage = new TranslationTextComponent(backpack.getId().getNamespace() + ".backpack." + backpack.getId().getPath() + ".unlock");
            List<IReorderingProcessor> list = new ArrayList<>(Minecraft.getInstance().font.split(unlockMessage, 150));
            list.add(0, LanguageMap.getInstance().getVisualOrder(LOCKED));
            if(progressMap.containsKey(backpack.getId()))
            {
                ITextComponent component = progressMap.get(backpack.getId()).plainCopy().withStyle(TextFormatting.YELLOW);
                list.add(LanguageMap.getInstance().getVisualOrder(component));
            }
            this.unlockTooltip = ImmutableList.copyOf(list);
        }

        public String getId()
        {
            return this.id;
        }

        public ITextComponent getLabel()
        {
            return this.label;
        }

        public List<IReorderingProcessor> getUnlockTooltip()
        {
            return this.unlockTooltip;
        }

        public Backpack getBackpack()
        {
            return this.backpack;
        }
    }
}
