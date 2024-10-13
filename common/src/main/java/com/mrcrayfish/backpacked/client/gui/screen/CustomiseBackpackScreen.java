package com.mrcrayfish.backpacked.client.gui.screen;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CheckBox;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.common.backpack.ModelMeta;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

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
    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/customise_backpack.png");
    private static final Component SHOW_EFFECTS_TOOLTIP = Component.translatable("backpacked.button.show_effects.tooltip");
    private static final Component SHOW_WITH_ELYTRA_TOOLTIP = Component.translatable("backpacked.button.show_with_elytra.tooltip");
    private static final Component SHOW_ENCHANTMENT_GLINT = Component.translatable("backpacked.button.show_enchantment_glint.tooltip");
    private static final Component LOCKED = Component.translatable("backpacked.gui.locked").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);

    private final ItemStack displayStack;
    private final int windowWidth;
    private final int windowHeight;
    private int windowLeft;
    private int windowTop;
    private float windowRotationX = 35F;
    private float windowRotationY = 10;
    private boolean windowGrabbed;
    private boolean scrollGrabbed;
    private int mouseClickedX, mouseClickedY;
    private Button resetButton;
    private Button saveButton;
    private CheckBox showEnchantmentGlintButton;
    private CheckBox showWithElytraButton;
    private CheckBox showEffectsButton;
    private BackpackProperties displayBackpack = null;
    private final List<BackpackModelEntry> models;
    private int scroll;

    public CustomiseBackpackScreen(Map<ResourceLocation, Component> progressMap)
    {
        super(Component.translatable("backpacked.title.customise_backpack"));
        this.displayStack = new ItemStack(ModItems.BACKPACK.get());
        this.windowWidth = 201;
        this.windowHeight = 166;
        Comparator<BackpackModelEntry> compareUnlock = Comparator.comparing(e -> !e.backpack.isUnlocked(Minecraft.getInstance().player));
        Comparator<BackpackModelEntry> compareLabel = Comparator.comparing(e -> e.label.getString());
        List<BackpackModelEntry> models = BackpackManager.instance().getClientBackpacks()
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
        if(this.displayBackpack == null)
        {
            this.displayBackpack = this.getCurrentBackpackProperties();
        }
        this.windowLeft = (this.width - this.windowWidth) / 2;
        this.windowTop = (this.height - this.windowHeight) / 2;
        this.resetButton = this.addRenderableWidget(Button.builder(Component.translatable("backpacked.button.reset"), onPress -> {
            this.displayBackpack = this.displayBackpack.setModel(BackpackManager.getDefaultOrFallbackCosmetic());
        }).pos(this.windowLeft + 7, this.windowTop + 114).size(71, 20).build());
        this.saveButton = this.addRenderableWidget(Button.builder(Component.translatable("backpacked.button.save"), onPress -> {
            Network.getPlay().sendToServer(new MessageBackpackCosmetics(this.displayBackpack));
        }).pos(this.windowLeft + 7, this.windowTop + 137).size(71, 20).build());
        this.showEnchantmentGlintButton = this.addRenderableWidget(new CheckBox(this.windowLeft + 133, this.windowTop + 6, CommonComponents.EMPTY, onPress -> {
            this.displayBackpack = this.displayBackpack.setShowEnchantmentGlint(!this.displayBackpack.showEnchantmentGlint());
        }));
        this.showEnchantmentGlintButton.setTooltip(Tooltip.create(SHOW_ENCHANTMENT_GLINT));
        this.showWithElytraButton = this.addRenderableWidget(new CheckBox(this.windowLeft + 160, this.windowTop + 6, CommonComponents.EMPTY, onPress -> {
            this.displayBackpack = this.displayBackpack.setShowWithElytra(!this.displayBackpack.showWithElytra());
        }));
        this.showWithElytraButton.setTooltip(Tooltip.create(SHOW_WITH_ELYTRA_TOOLTIP));
        this.showEffectsButton = this.addRenderableWidget(new CheckBox(this.windowLeft + 186, this.windowTop + 6, CommonComponents.EMPTY, onPress -> {
            this.displayBackpack = this.displayBackpack.setShowEffects(!this.displayBackpack.showEffects());
        }));
        this.showEffectsButton.setTooltip(Tooltip.create(SHOW_EFFECTS_TOOLTIP));
        ItemStack backpack = Services.BACKPACK.getBackpackStack(this.minecraft.player);
        if(!backpack.isEmpty())
        {
            this.showEnchantmentGlintButton.setChecked(this.displayBackpack.showEnchantmentGlint());
            this.showWithElytraButton.setChecked(this.displayBackpack.showWithElytra());
            this.showEffectsButton.setChecked(this.displayBackpack.showEffects());
        }
        this.updateButtons();
    }

    private void updateButtons()
    {
        this.resetButton.active = !this.displayBackpack.model().equals(BackpackManager.getDefaultCosmetic());
        this.saveButton.active = this.needsToSave();
    }

    private boolean needsToSave()
    {
        BackpackProperties properties = this.getCurrentBackpackProperties();
        return !this.displayBackpack.equals(properties);
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
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(GUI_TEXTURE, this.windowLeft, this.windowTop, 0, 0, this.windowWidth, this.windowHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        
        // Draw title
        graphics.drawString(this.font, this.title, this.windowLeft + 8, this.windowTop + 6, 4210752, false);

        // Draw player in window
        if(this.minecraft.player != null)
        {
            graphics.enableScissor(this.windowLeft + 8, this.windowTop + 18, this.windowLeft + 77, this.windowTop + 110);
            this.renderPlayer(graphics, this.windowLeft + 42, this.windowTop + this.windowHeight / 2, mouseX, mouseY, this.minecraft.player);
            graphics.disableScissor();
        }

        // Draw scroll bar
        boolean canScroll = this.models.size() > 7;
        int scroll = (canScroll ? this.scroll : 0) + (this.scrollGrabbed ? mouseY - this.mouseClickedY : 0);
        scroll = Mth.clamp(scroll, 0, 123);
        graphics.blit(GUI_TEXTURE, this.windowLeft + 181, this.windowTop + 18 + scroll, 201 + (!canScroll ? 12 : 0), 0, 12, 15);

        // Draw backpack items
        int startIndex = (int) (Math.max(0, this.models.size() - 7) * Mth.clamp((scroll + 15.0) / 123.0, 0.0, 1.0));
        for(int i = startIndex; i < this.models.size() && i < startIndex + 7; i++)
        {
            this.drawBackpackItem(graphics, this.windowLeft + 82, this.windowTop + 17 + (i - startIndex) * 20, mouseX, mouseY, partialTick, this.models.get(i));
        }

        int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
        if(hoveredIndex != -1)
        {
            BackpackModelEntry entry = this.models.get(hoveredIndex);
            if(!entry.getBackpack().isUnlocked(this.minecraft.player))
            {
                graphics.renderTooltip(this.font, entry.getUnlockTooltip(), mouseX, mouseY);
            }
        }
    }

    private void drawBackpackItem(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTick, BackpackModelEntry entry)
    {
        boolean unlocked = entry.getBackpack().isUnlocked(this.minecraft.player);
        boolean selected = unlocked && entry.getCosmeticId().equals(this.displayBackpack.model());
        boolean hovered = unlocked && !selected && ScreenUtil.isPointInArea(mouseX, mouseY, x, y, 97, 20);

        // Draw background for item
        int offset = (unlocked ? 0 : 60) + (selected ? 20 : 0) + (hovered ? 40 : 0);
        graphics.blit(GUI_TEXTURE, x, y, 0, 166 + offset, 97, 20);

        // Draw label. TODO convert dumb values into readable hex
        int color = selected ? 4226832 : (hovered ? 16777088 : (unlocked ? 6839882 : 0x4E1C1C));
        graphics.drawString(this.font, entry.getLabel(), x + 20, y + 6, color, false);

        // Draw backpack model
        drawBackpackInGui(this.minecraft, graphics, this.displayStack, entry.getBackpack(), x + 10, y + 10, partialTick);
    }

    public static void drawBackpackInGui(Minecraft mc, GuiGraphics graphics, ItemStack stack, Backpack backpack, int x, int y, float partialTick)
    {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 150);
        pose.mulPose(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        pose.scale(16, 16, 16);
        ModelMeta meta = BackpackManager.instance().getModelMeta(backpack);
        meta.guiDisplay().ifPresent(transform -> transform.apply(false, pose));
        meta.renderer().ifPresentOrElse(renderer -> {
            BackpackRenderContext context = new BackpackRenderContext(pose, graphics.bufferSource(), 0xF000F0, stack, backpack, mc.player, partialTick, mc.player.tickCount, model -> {
                mc.getItemRenderer().render(stack, ItemDisplayContext.NONE, false, pose, graphics.bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY, model);
                graphics.flush();
            });
            pose.pushPose();
            renderer.forEach(function -> function.apply(context));
            pose.popPose();
        }, () -> {
            BakedModel model = mc.getModelManager().getModel(backpack.getBaseModel());
            mc.getItemRenderer().render(stack, ItemDisplayContext.NONE, false, pose, graphics.bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY, model);
            graphics.flush();
        });
        pose.popPose();
    }

    private int getHoveredIndex(int mouseX, int mouseY)
    {
        if(ScreenUtil.isPointInArea(mouseX, mouseY, this.windowLeft + 82, this.windowTop + 17, 97, 140))
        {
            int startIndex = (int) (Math.max(0, this.models.size() - 7) * Mth.clamp((this.scroll + 15.0) / 123.0, 0.0, 1.0));
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
                        this.displayBackpack = this.displayBackpack.setModel(entry.getCosmeticId());
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
                this.scroll = Mth.clamp(this.scroll, 0, 123);
                this.scrollGrabbed = false;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + 82, this.windowTop + 17, 112, 140))
        {
            int startIndex = (int) (Math.max(0, this.models.size() - 7) * Mth.clamp((this.scroll + 15.0) / 123.0, 0.0, 1.0));
            int newIndex = startIndex - (int) Math.signum(deltaY);
            this.scrollToIndex(newIndex);
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    private void scrollToIndex(int index)
    {
        // 123 is the height of the scrollbar area
        this.scroll = (int) (123.0 * ((double) index / (double) Math.max(this.models.size() - 7, 1)));
        this.scroll = Mth.clamp(this.scroll, 0, 123);
    }

    private BackpackProperties getCurrentBackpackProperties()
    {
        ItemStack stack = Services.BACKPACK.getBackpackStack(this.minecraft.player);
        return this.getBackpackProperties(stack);
    }

    private BackpackProperties getBackpackProperties(ItemStack stack)
    {
        return stack.getOrDefault(ModDataComponents.BACKPACK_PROPERTIES.get(), BackpackProperties.DEFAULT);
    }

    private void setLocalBackpackProperties(BackpackProperties properties)
    {
        ItemStack stack = Services.BACKPACK.getBackpackStack(this.minecraft.player);
        if(!stack.isEmpty())
        {
            stack.set(ModDataComponents.BACKPACK_PROPERTIES.get(), properties);
        }
    }

    private void renderPlayer(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, Player player)
    {
        // TODO test
        Quaternionf playerRotation = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf cameraRotation = new Quaternionf();
        cameraRotation.mul(Axis.XN.rotationDegrees(this.windowRotationY + (this.windowGrabbed ? mouseY - this.mouseClickedY : 0)));
        cameraRotation.mul(Axis.YP.rotationDegrees(this.windowRotationX + (this.windowGrabbed ? mouseX - this.mouseClickedX : 0)));
        playerRotation.mul(cameraRotation);
        float origBodyRot = player.yBodyRot;
        float origBodyRotOld = player.yBodyRotO;
        float origYaw = player.getYRot();
        float origYawOld = player.yRotO;
        float origPitch = player.getXRot();
        float origPitchOld = player.xRotO;
        float origHeadYaw = player.yHeadRot;
        float origHeadYawOld = player.yHeadRotO;
        player.yBodyRot = 0.0F;
        player.yBodyRotO = 0.0F;
        player.setYRot(0.0F);
        player.yRotO = 0.0F;
        player.setXRot(15F);
        player.xRotO = 15F;
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();
        BackpackProperties originalProperties = this.getCurrentBackpackProperties();
        this.setLocalBackpackProperties(this.displayBackpack);
        float entityScale = player.getScale();
        float renderScale = 70F / entityScale;
        Vector3f box = new Vector3f(0.0F, player.getBbHeight() / 2.0F + entityScale * 0.0625F, 0.0F);
        InventoryScreen.renderEntityInInventory(graphics, x, y, renderScale, box, playerRotation, cameraRotation, player);
        this.setLocalBackpackProperties(originalProperties);
        player.yBodyRot = origBodyRot;
        player.yBodyRotO = origBodyRotOld;
        player.setYRot(origYaw);
        player.yRotO = origYawOld;
        player.setXRot(origPitch);
        player.xRotO = origPitchOld;
        player.yHeadRot = origHeadYaw;
        player.yHeadRotO = origHeadYawOld;
        this.setLocalBackpackProperties(originalProperties);
    }

    private static class BackpackModelEntry
    {
        private final ResourceLocation cosmeticId;
        private final Backpack backpack;
        private final Component label;
        private final List<FormattedCharSequence> unlockTooltip;

        public BackpackModelEntry(Backpack backpack, Map<ResourceLocation, Component> progressMap)
        {
            this.cosmeticId = backpack.getId();
            this.backpack = backpack;
            this.label = Component.translatable(backpack.getTranslationKey());
            Component unlockMessage = Component.translatable(backpack.getTranslationKey() + ".unlock");
            List<FormattedCharSequence> list = new ArrayList<>(Minecraft.getInstance().font.split(unlockMessage, 150));
            list.add(0, Language.getInstance().getVisualOrder(LOCKED));
            if(progressMap.containsKey(backpack.getId()))
            {
                Component component = progressMap.get(backpack.getId()).plainCopy().withStyle(ChatFormatting.YELLOW);
                list.add(Language.getInstance().getVisualOrder(component));
            }
            this.unlockTooltip = ImmutableList.copyOf(list);
        }

        public ResourceLocation getCosmeticId()
        {
            return this.cosmeticId;
        }

        public Component getLabel()
        {
            return this.label;
        }

        public List<FormattedCharSequence> getUnlockTooltip()
        {
            return this.unlockTooltip;
        }

        public Backpack getBackpack()
        {
            return this.backpack;
        }
    }
}
