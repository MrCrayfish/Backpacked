package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Supplier;

public class PlayerDisplay extends AbstractWidget
{
    private static final Identifier FRAME = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "backpack/frame");
    private static final int FRAME_OFFSET = 4;

    private final Player player;
    private final Supplier<CosmeticProperties> propertiesSupplier;

    private float rotationX = 35;
    private float rotationY = 10;
    private boolean grabbed;
    private int grabbedX;
    private int grabbedY;

    private float origBodyRot;
    private float origBodyRotOld;
    private float origYaw;
    private float origYawOld;
    private float origPitch;
    private float origPitchOld;
    private float origHeadYaw;
    private float origHeadYawOld;
    private CosmeticProperties origProperties;

    public PlayerDisplay(Player player, int x, int y, int width, int height, Supplier<CosmeticProperties> propertiesSupplier)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.player = player;
        this.propertiesSupplier = propertiesSupplier;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.defaultButtonNarrationText(output);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int widgetX = this.getX();
        int widgetY = this.getY();
        int widgetWidth = this.getWidth();
        int widgetHeight = this.getHeight();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FRAME, widgetX, widgetY, widgetWidth, widgetHeight);
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        graphics.enableScissor(widgetX + FRAME_OFFSET, widgetY + FRAME_OFFSET, widgetX + widgetWidth - FRAME_OFFSET, widgetY + widgetHeight - FRAME_OFFSET);
        this.renderPlayerModel(graphics, widgetX + widgetWidth / 2, widgetY + widgetHeight / 2, mouseX, mouseY);
        graphics.disableScissor();
        pose.popMatrix();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if(!this.active || !this.visible || !this.isValidClickButton(event.buttonInfo()))
            return false;

        if(ScreenUtil.isPointInArea((int) event.x(), (int) event.y(), this.getX() + FRAME_OFFSET, this.getY() + FRAME_OFFSET, this.getWidth() - FRAME_OFFSET * 2, this.getHeight() - FRAME_OFFSET * 2))
        {
            if(!this.grabbed)
            {
                this.grabbed = true;
                this.grabbedX = (int) event.x();
                this.grabbedY = (int) event.y();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        if(this.grabbed)
        {
            if(this.isValidClickButton(event.buttonInfo()))
            {
                this.rotationX += (float) (event.x() - this.grabbedX);
                this.rotationY += (float) (event.y() - this.grabbedY);
                this.grabbed = false;
                return true;
            }
        }
        return false;
    }

    private void renderPlayerModel(GuiGraphics graphics, int x, int y, int mouseX, int mouseY)
    {
        if(this.player == null)
            return;

        this.captureValues();
        this.overrideValues();
        Quaternionf playerRotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf cameraRotation = new Quaternionf();
        cameraRotation.mul(Axis.XN.rotationDegrees(this.rotationY + (this.grabbed ? mouseY - this.grabbedY : 0)));
        cameraRotation.mul(Axis.YP.rotationDegrees(this.rotationX + (this.grabbed ? mouseX - this.grabbedX : 0)));
        playerRotation.mul(cameraRotation);
        float entityScale = player.getScale();
        float renderScale = 70F / entityScale;
        Vector3f box = new Vector3f(0.0F, player.getBbHeight() / 2.0F + entityScale * 0.0625F, 0.0F);
        // TODO 1.21.11 restore
        //InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x, y + 15, renderScale, box, playerRotation, cameraRotation, player);
        this.restoreValues();
    }

    private void captureValues()
    {
        this.origBodyRot = this.player.yBodyRot;
        this.origBodyRotOld = this.player.yBodyRotO;
        this.origYaw = this.player.getYRot();
        this.origYawOld = this.player.yRotO;
        this.origPitch = this.player.getXRot();
        this.origPitchOld = this.player.xRotO;
        this.origHeadYaw = this.player.yHeadRot;
        this.origHeadYawOld = this.player.yHeadRotO;
        this.origProperties = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(this.player).orElse(CosmeticProperties.DEFAULT);
    }

    private void overrideValues()
    {
        this.player.yBodyRot = 0;
        this.player.yBodyRotO = 0;
        this.player.setYRot(0);
        this.player.yRotO = 0;
        this.player.setXRot(15);
        this.player.xRotO = 15;
        this.player.yHeadRot = player.getYRot();
        this.player.yHeadRotO = player.getYRot();
        ModSyncedDataKeys.COSMETIC_PROPERTIES.setValue(this.player, Optional.ofNullable(this.propertiesSupplier.get()));
    }

    private void restoreValues()
    {
        this.player.yBodyRot = this.origBodyRot;
        this.player.yBodyRotO = this.origBodyRotOld;
        this.player.setYRot(this.origYaw);
        this.player.yRotO = this.origYawOld;
        this.player.setXRot(this.origPitch);
        this.player.xRotO = this.origPitchOld;
        this.player.yHeadRot = this.origHeadYaw;
        this.player.yHeadRotO = this.origHeadYawOld;
        ModSyncedDataKeys.COSMETIC_PROPERTIES.setValue(this.player, Optional.ofNullable(this.origProperties));
    }
}
