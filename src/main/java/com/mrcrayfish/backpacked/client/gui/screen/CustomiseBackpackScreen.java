package com.mrcrayfish.backpacked.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.BackpackModels;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.network.PacketHandler;
import com.mrcrayfish.backpacked.network.message.MessageSetBackpackModel;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class CustomiseBackpackScreen extends Screen
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/customise_backpack.png");

    private final int windowWidth;
    private final int windowHeight;
    private int windowLeft;
    private int windowTop;
    private float windowRotationX = -35F;
    private float windowRotationY = 10;
    private boolean mouseGrabbed;
    private int mouseGrabbedButton;
    private int mouseClickedX, mouseClickedY;
    private Button resetButton;
    private Button saveButton;
    private String displayBackpackModel;
    private final List<BackpackModelEntry> models;
    private int scroll;

    public CustomiseBackpackScreen()
    {
        super(new TranslationTextComponent("backpacked.title.customise_backpack"));
        this.windowWidth = 176;
        this.windowHeight = 166;
        List<BackpackModelEntry> models = BackpackLayer.getBackpackModels().entrySet().stream().map(entry -> new BackpackModelEntry(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        models.add(new BackpackModelEntry());
        models.sort(Comparator.comparing(o -> o.label.getString()));
        this.models = ImmutableList.copyOf(models);
    }

    @Override
    protected void init()
    {
        super.init();
        this.displayBackpackModel = this.getBackpackModel();
        this.windowLeft = (this.width - this.windowWidth) / 2;
        this.windowTop = (this.height - this.windowHeight) / 2;
        this.resetButton = this.addButton(new Button(this.windowLeft + 7, this.windowTop + 114, 71, 20, new TranslationTextComponent("backpacked.button.reset"), onPress -> {
            this.displayBackpackModel = "";
        }));
        this.saveButton = this.addButton(new Button(this.windowLeft + 7, this.windowTop + 137, 71, 20, new TranslationTextComponent("backpacked.button.save"), onPress -> {
            PacketHandler.instance.sendToServer(new MessageSetBackpackModel(this.displayBackpackModel));
        }));
        this.updateButtons();
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

    private void updateButtons()
    {
        this.resetButton.active = !this.getBackpackModel().isEmpty();
        this.saveButton.active = !this.displayBackpackModel.equals(this.getBackpackModel());
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
        if(this.minecraft.player != null)
        {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            ScreenUtil.scissor(this.windowLeft + 8, this.windowTop + 18, 69, 92);
            this.renderPlayer(this.windowLeft + 42, this.windowTop + this.windowHeight / 2, mouseX, mouseY, this.minecraft.player);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        this.font.draw(matrixStack, this.title, this.windowLeft + 8, this.windowTop + 6, 4210752);
        for(int i = 0; i < this.models.size() && i < 7; i++)
        {
            this.drawBackpackItem(matrixStack, this.windowLeft + 82, this.windowTop + 17 + i * 20, mouseX, mouseY, this.models.get(i));
        }
    }

    private void drawBackpackItem(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY, BackpackModelEntry entry)
    {
        boolean selected = entry.getId().equals(this.displayBackpackModel);
        boolean hovered = !selected && ScreenUtil.isPointInArea(mouseX, mouseY, x, y, 72, 20);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        this.blit(matrixStack, x, y, 176, 15 + (selected ? 20 : 0) + (hovered ? 40 : 0), 72, 20);
        this.font.draw(matrixStack, entry.getLabel(), x + 20, y + 6, selected ? 4226832 : hovered ? 16777088 : 6839882);

        matrixStack.pushPose();
        matrixStack.translate(x + 8, y + 4, 50);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-10F));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(35F));
        matrixStack.scale(20F, 20F, 20F);
        RenderHelper.setupForFlatItems();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.pushMatrix();
        IRenderTypeBuffer.Impl source = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        IVertexBuilder builder = source.getBuffer(entry.getModel().renderType(entry.getModel().getTextureLocation()));
        BackpackModel backpackModel = entry.getModel();
        backpackModel.getStraps().visible = false;
        ModelRenderer bag = backpackModel.getBag();
        bag.setPos(0, 0, 0);
        bag.render(matrixStack, builder, 15728880, OverlayTexture.NO_OVERLAY);
        source.endBatch();
        RenderSystem.popMatrix();
        matrixStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + 82, this.windowTop + 17, 72, 140))
        {
            if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                int index = (int) ((mouseY - this.windowTop - 17) / 20);
                if(index >= 0 && index < this.models.size())
                {
                    this.displayBackpackModel = this.models.get(index).getId();
                    this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
            }
        }
        else if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + 8, this.windowTop + 18, 69, 92))
        {
            if(!this.mouseGrabbed && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                this.mouseGrabbed = true;
                this.mouseGrabbedButton = GLFW.GLFW_MOUSE_BUTTON_LEFT;
                this.mouseClickedX = (int) mouseX;
                this.mouseClickedY = (int) mouseY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.mouseGrabbed)
        {
            if(this.mouseGrabbedButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                this.windowRotationX += (mouseX - this.mouseClickedX);
                this.windowRotationY += (mouseY - this.mouseClickedY);
                this.mouseGrabbed = false;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
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
        cameraRotation.mul(Vector3f.XN.rotationDegrees(this.windowRotationY + (this.mouseGrabbed && this.mouseGrabbedButton == GLFW.GLFW_MOUSE_BUTTON_LEFT ? mouseY - this.mouseClickedY : 0)));
        cameraRotation.mul(Vector3f.YP.rotationDegrees(this.windowRotationX + (this.mouseGrabbed && this.mouseGrabbedButton == GLFW.GLFW_MOUSE_BUTTON_LEFT ? mouseX - this.mouseClickedX : 0)));
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
        player.yBodyRot = 0.0F;
        player.yRot = 0.0F;
        player.xRot = 0.0F;
        player.yHeadRot = player.yRot;
        player.yHeadRotO = player.yRot;
        this.setLocalBackpackModel(this.displayBackpackModel);
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
        RenderSystem.popMatrix();
    }

    private static class BackpackModelEntry
    {
        private final String id;
        private final BackpackModel model;
        private final ITextComponent label;

        public BackpackModelEntry(String id, BackpackModel model)
        {
            this.id = id;
            this.model = model;
            ResourceLocation location = new ResourceLocation(id);
            this.label = new TranslationTextComponent(location.getNamespace() + ".backpack." + location.getPath());
        }

        private BackpackModelEntry()
        {
            this.id = "";
            this.model = BackpackModels.STANDARD;
            this.label = new TranslationTextComponent("backpacked.backpack.standard");
        }

        public String getId()
        {
            return this.id;
        }

        public ITextComponent getLabel()
        {
            return this.label;
        }

        public BackpackModel getModel()
        {
            return this.model;
        }
    }
}
