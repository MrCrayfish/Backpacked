package com.mrcrayfish.backpacked.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CheckBox;
import com.mrcrayfish.backpacked.client.renderer.BakedModelRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
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
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class CustomiseBackpackScreen extends Screen
{
    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/customise_backpack.png");
    private static final ResourceLocation LABEL_WARNING_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/label_warning");

    private static final Component SHOW_EFFECTS_TOOLTIP = Component.translatable("backpacked.button.show_effects.tooltip");
    private static final Component SHOW_WITH_ELYTRA_TOOLTIP = Component.translatable("backpacked.button.show_with_elytra.tooltip");
    private static final Component SHOW_ENCHANTMENT_GLINT = Component.translatable("backpacked.button.show_enchantment_glint.tooltip");
    private static final Component LOCKED = Component.translatable("backpacked.gui.locked").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
    private static final Component COSMETIC_WARNING = Component.translatable("backpacked.gui.cosmetic_warning");

    private static final int ITEM_WIDTH = 97;
    private static final int ITEM_HEIGHT = 20;
    private static final int ITEM_LIST_WIDTH = 97;
    private static final int ITEM_LIST_HEIGHT = 140;
    private static final int MAX_VISIBLE_ITEMS = ITEM_LIST_HEIGHT / ITEM_HEIGHT; // Should have no remainder
    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 15;
    private static final int SCROLLABLE_HEIGHT = 138;
    private static final int SCROLLABLE_AREA = SCROLLABLE_HEIGHT - SCROLL_BAR_HEIGHT;

    private static final int DEFAULT_ITEM_TEXT_COLOUR = 0x4E1C1C;
    private static final int SELECTED_ITEM_TEXT_COLOUR = 0x407F10;
    private static final int HOVERED_ITEM_TEXT_COLOUR = 0xFFFF80;
    private static final int UNLOCKED_ITEM_TEXT_COLOUR = 0x685E4A;
    private static final int MODEL_LIGHTING = 0xF000F0;

    private final int windowWidth;
    private final int windowHeight;
    private final boolean showCosmeticWarning;
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
    private BackpackProperties realProperties;
    private BackpackProperties currentProperties;
    private BackpackProperties displayBackpack = null;
    private final List<BackpackModelEntry> models;
    private int scroll;

    public CustomiseBackpackScreen(Map<ResourceLocation, Component> progressMap, BackpackProperties properties, boolean showCosmeticWarning)
    {
        super(Component.translatable("backpacked.title.customise_backpack"));
        this.windowWidth = 201;
        this.windowHeight = 166;
        Comparator<BackpackModelEntry> compareUnlock = Comparator.comparing(e -> !e.backpack.isUnlocked(Minecraft.getInstance().player));
        Comparator<BackpackModelEntry> compareLabel = Comparator.comparing(e -> e.label.getString());
        List<BackpackModelEntry> models = ClientRegistry.instance().getBackpacks()
                .stream()
                .map(backpack -> new BackpackModelEntry(backpack, progressMap))
                .sorted(compareUnlock.thenComparing(compareLabel))
                .collect(Collectors.toList());
        this.models = ImmutableList.copyOf(models);
        this.showCosmeticWarning = showCosmeticWarning;
        this.currentProperties = properties;
    }

    @Override
    protected void init()
    {
        MouseRestorer.loadCapturedPosition();

        super.init();
        if(this.displayBackpack == null)
        {
            this.realProperties = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(this.minecraft.player).orElse(BackpackProperties.DEFAULT);
            this.displayBackpack = this.currentProperties;
        }

        this.windowLeft = (this.width - this.windowWidth) / 2;
        this.windowTop = (this.height - this.windowHeight) / 2;

        this.resetButton = this.addRenderableWidget(Button.builder(Component.translatable("backpacked.button.reset"), onPress -> {
            this.displayBackpack = this.displayBackpack.setCosmetic(BackpackManager.getDefaultOrFallbackCosmetic());
        }).pos(this.windowLeft + 7, this.windowTop + 114).size(71, 20).build());

        this.saveButton = this.addRenderableWidget(Button.builder(Component.translatable("backpacked.button.save"), onPress -> {
            Network.getPlay().sendToServer(new MessageBackpackCosmetics(this.displayBackpack));
            this.currentProperties = this.displayBackpack;
        }).pos(this.windowLeft + 7, this.windowTop + 137).size(71, 20).build());

        this.showEnchantmentGlintButton = this.addRenderableWidget(new CheckBox(this.windowLeft + 133, this.windowTop + 6, CommonComponents.EMPTY, onPress -> {
            this.displayBackpack = this.displayBackpack.setShowEnchantmentGlint(!this.displayBackpack.showEnchantmentGlint());
        }));
        this.showEnchantmentGlintButton.setTooltip(Tooltip.create(SHOW_ENCHANTMENT_GLINT));
        this.showEnchantmentGlintButton.setChecked(this.displayBackpack.showEnchantmentGlint());

        this.showWithElytraButton = this.addRenderableWidget(new CheckBox(this.windowLeft + 160, this.windowTop + 6, CommonComponents.EMPTY, onPress -> {
            this.displayBackpack = this.displayBackpack.setShowWithElytra(!this.displayBackpack.showWithElytra());
        }));
        this.showWithElytraButton.setTooltip(Tooltip.create(SHOW_WITH_ELYTRA_TOOLTIP));
        this.showWithElytraButton.setChecked(this.displayBackpack.showWithElytra());

        this.showEffectsButton = this.addRenderableWidget(new CheckBox(this.windowLeft + 186, this.windowTop + 6, CommonComponents.EMPTY, onPress -> {
            this.displayBackpack = this.displayBackpack.setShowEffects(!this.displayBackpack.showEffects());
        }));
        this.showEffectsButton.setTooltip(Tooltip.create(SHOW_EFFECTS_TOOLTIP));
        this.showEffectsButton.setChecked(this.displayBackpack.showEffects());

        this.updateButtons();
    }

    private void updateButtons()
    {
        ResourceLocation displayCosmetic = this.displayBackpack.cosmetic().orElse(null);
        this.resetButton.active = !Objects.equals(displayCosmetic, BackpackManager.getDefaultCosmetic());
        this.saveButton.active = this.needsToSave();
    }

    private boolean needsToSave()
    {
        return !this.displayBackpack.equals(this.currentProperties);
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

        if(this.showCosmeticWarning)
        {
            int messageWidth = this.font.width(COSMETIC_WARNING);
            int messageBgWidth = 7 + messageWidth + 7;
            int messageY = 8;
            graphics.fillGradient(0, 0, this.width, 50, 0xAA000000, 0x00000000);
            graphics.blitSprite(LABEL_WARNING_BACKGROUND, (this.width - messageBgWidth) / 2, messageY, messageBgWidth, 20);
            graphics.drawString(this.font, COSMETIC_WARNING, (this.width - messageWidth) / 2, messageY + 6, 0xFFFFFFFF);
        }
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
        boolean canScroll = this.models.size() > MAX_VISIBLE_ITEMS;
        int scroll = (canScroll ? this.scroll : 0) + (this.scrollGrabbed ? mouseY - this.mouseClickedY : 0);
        scroll = Mth.clamp(scroll, 0, SCROLLABLE_HEIGHT - SCROLL_BAR_HEIGHT);
        int scrollBarX = this.windowLeft + 181;
        int scrollBarY = this.windowTop + 18 + scroll;
        int scrollBarTexU = 201 + (!canScroll ? SCROLL_BAR_WIDTH : 0);
        graphics.blit(GUI_TEXTURE, scrollBarX, scrollBarY, scrollBarTexU, 0, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);

        // Draw backpack items
        int startIndex = (int) (Math.max(0, this.models.size() - MAX_VISIBLE_ITEMS) * Mth.clamp(scroll / (double) SCROLLABLE_AREA, 0, 1));
        for(int i = startIndex; i < this.models.size() && i < startIndex + MAX_VISIBLE_ITEMS; i++)
        {
            int itemX = this.windowLeft + 82;
            int itemY = this.windowTop + 17 + (i - startIndex) * ITEM_HEIGHT;
            graphics.enableScissor(itemX, itemY, itemX + ITEM_WIDTH, itemY + ITEM_HEIGHT);
            this.drawBackpackItem(graphics, itemX, itemY, mouseX, mouseY, partialTick, this.models.get(i));
            graphics.disableScissor();
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
        boolean selected = unlocked && this.displayBackpack.cosmetic().stream().anyMatch(id -> id.equals(entry.getCosmeticId()));
        boolean hovered = unlocked && !selected && ScreenUtil.isPointInArea(mouseX, mouseY, x, y, ITEM_WIDTH, ITEM_HEIGHT);

        // Draw background for item
        int offset = (unlocked ? 0 : 60) + (selected ? 20 : 0) + (hovered ? 40 : 0);
        graphics.blit(GUI_TEXTURE, x, y, 0, 166 + offset, ITEM_WIDTH, ITEM_HEIGHT);

        // Draw label
        int color = this.getItemTextColour(unlocked, selected, hovered);
        graphics.drawString(this.font, entry.getLabel(), x + 20, y + 6, color, false);

        // Draw backpack cosmetic
        drawBackpackInGui(this.minecraft, graphics, entry.getBackpack(), x + 10, y + 10, partialTick);
    }

    private int getItemTextColour(boolean unlocked, boolean selected, boolean hovered)
    {
        if(selected) return SELECTED_ITEM_TEXT_COLOUR;
        if(hovered) return HOVERED_ITEM_TEXT_COLOUR;
        if(unlocked) return UNLOCKED_ITEM_TEXT_COLOUR;
        return DEFAULT_ITEM_TEXT_COLOUR;
    }

    public static void drawBackpackInGui(Minecraft mc, GuiGraphics graphics, ClientBackpack backpack, int x, int y, float partialTick)
    {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 150);
        pose.mulPose(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        pose.scale(16, 16, 16);
        ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
        meta.guiDisplay().ifPresent(transform -> transform.apply(false, pose));
        meta.renderer().ifPresentOrElse(renderer -> {
            BackpackRenderContext context = new BackpackRenderContext(Scene.CUSTOMISATION_MENU, RenderMode.MODELS_ONLY, pose, graphics.bufferSource(), 0xF000F0, backpack, mc.player, mc.level, partialTick, model -> {
                BakedModelRenderer.drawBakedModel(model, pose, graphics.bufferSource(), MODEL_LIGHTING, OverlayTexture.NO_OVERLAY);
                graphics.bufferSource().endBatch();
            });
            pose.pushPose();
            renderer.render(context);
            pose.popPose();
        }, () -> {
            BakedModel model = mc.getModelManager().getModel(backpack.getBaseModel());
            BakedModelRenderer.drawBakedModel(model, pose, graphics.bufferSource(), MODEL_LIGHTING, OverlayTexture.NO_OVERLAY);
            graphics.bufferSource().endBatch();
        });
        pose.popPose();
    }

    private int getHoveredIndex(int mouseX, int mouseY)
    {
        if(ScreenUtil.isPointInArea(mouseX, mouseY, this.windowLeft + 82, this.windowTop + 17, ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT))
        {
            int startIndex = (int) (Math.max(0, this.models.size() - MAX_VISIBLE_ITEMS) * Mth.clamp(this.scroll / (double) SCROLLABLE_AREA, 0, 1));
            int offsetIndex = (mouseY - this.windowTop - 17) / ITEM_HEIGHT;
            int hoveredIndex = startIndex + offsetIndex;
            if(hoveredIndex >= 0 && hoveredIndex < this.models.size())
            {
                return hoveredIndex;
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
                        this.displayBackpack = this.displayBackpack.setCosmetic(entry.getCosmeticId());
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
            int scrollableContentHeight = Math.max(this.models.size() * ITEM_HEIGHT - ITEM_LIST_HEIGHT, 0);
            float scrollNormal = (float) this.scroll / (SCROLLABLE_HEIGHT - SCROLL_BAR_HEIGHT);
            int startIndex = (int) (scrollableContentHeight * scrollNormal) / ITEM_HEIGHT;
            this.scrollToIndex(startIndex + Mth.sign(-deltaY));
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    private void scrollToIndex(int index)
    {
        this.scroll = Mth.ceil((SCROLLABLE_HEIGHT - SCROLL_BAR_HEIGHT) * ((double) index / (double) Math.max(this.models.size() - MAX_VISIBLE_ITEMS, 1)));
        this.scroll = Mth.clamp(this.scroll, 0, (SCROLLABLE_HEIGHT - SCROLL_BAR_HEIGHT));
    }

    private void setLocalBackpackProperties(BackpackProperties properties)
    {
        ModSyncedDataKeys.COSMETIC_PROPERTIES.setValue(this.minecraft.player, Optional.ofNullable(properties));
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
        this.setLocalBackpackProperties(this.displayBackpack);
        float entityScale = player.getScale();
        float renderScale = 70F / entityScale;
        Vector3f box = new Vector3f(0.0F, player.getBbHeight() / 2.0F + entityScale * 0.0625F, 0.0F);
        InventoryScreen.renderEntityInInventory(graphics, x, y, renderScale, box, playerRotation, cameraRotation, player);
        this.setLocalBackpackProperties(this.realProperties);
        player.yBodyRot = origBodyRot;
        player.yBodyRotO = origBodyRotOld;
        player.setYRot(origYaw);
        player.yRotO = origYawOld;
        player.setXRot(origPitch);
        player.xRotO = origPitchOld;
        player.yHeadRot = origHeadYaw;
        player.yHeadRotO = origHeadYawOld;
    }

    @Override
    public void removed()
    {
        super.removed();
        MouseRestorer.capturePosition();
    }

    private static class BackpackModelEntry
    {
        private final ResourceLocation cosmeticId;
        private final ClientBackpack backpack;
        private final Component label;
        private final List<FormattedCharSequence> unlockTooltip;

        public BackpackModelEntry(ClientBackpack backpack, Map<ResourceLocation, Component> progressMap)
        {
            this.cosmeticId = backpack.getId();
            this.backpack = backpack;
            this.label = Component.translatable(backpack.getTranslationKey());
            Component unlockMessage = Component.translatable(backpack.getTranslationKey() + ".unlock");
            List<FormattedCharSequence> list = new ArrayList<>(Minecraft.getInstance().font.split(unlockMessage, 150));
            list.addFirst(Language.getInstance().getVisualOrder(LOCKED));
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

        public ClientBackpack getBackpack()
        {
            return this.backpack;
        }
    }
}
