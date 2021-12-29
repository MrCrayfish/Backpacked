package com.mrcrayfish.backpacked.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

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

    public CustomiseBackpackScreen()
    {
        super(new TranslationTextComponent("backpacked.title.customise_backpack"));
        this.windowWidth = 176;
        this.windowHeight = 166;
    }

    @Override
    protected void init()
    {
        super.init();
        this.windowLeft = (this.width - this.windowWidth) / 2;
        this.windowTop = (this.height - this.windowHeight) / 2;
        this.addButton(new Button(this.windowLeft + 7, this.windowTop + 115, 71, 20, new TranslationTextComponent("backpacked.button.reset"), onPress -> {}));
        this.addButton(new Button(this.windowLeft + 7, this.windowTop + 139, 71, 20, new TranslationTextComponent("backpacked.button.save"), onPress -> {}));
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
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + 8, this.windowTop + 18, 69, 92))
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
        player.yBodyRot = 0.0F;
        player.yRot = 0.0F;
        player.xRot = 0.0F;
        player.yHeadRot = player.yRot;
        player.yHeadRotO = player.yRot;
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
        RenderSystem.popMatrix();
    }
}
