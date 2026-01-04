package com.mrcrayfish.backpacked.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.PlayerDisplay;
import com.mrcrayfish.backpacked.client.gui.screen.widget.ScrollBar;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.Alignment;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.CustomScreen;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.DropdownMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.item.CheckboxItem;
import com.mrcrayfish.backpacked.client.renderer.BakedModelRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.RenderMode;
import com.mrcrayfish.backpacked.client.renderer.backpack.Scene;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class CustomiseBackpackScreen extends CustomScreen
{
    private static final ResourceLocation BACKPACK_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/background");
    private static final ResourceLocation LABEL_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/label");
    private static final ResourceLocation CHECKERS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/checkers");
    private static final ResourceLocation LABEL_WARNING_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/label_warning");
    private static final ResourceLocation ROUNDED_BOX = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/rounded_box");
    private static final ResourceLocation LIST_ITEM = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list_item");
    private static final ResourceLocation LIST_ITEM_FOCUSED = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list_item_focused");
    private static final ResourceLocation LIST_ITEM_SELECTED = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list_item_selected");
    private static final ResourceLocation LIST_ITEM_LOCKED = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/list_item_locked");
    private static final ResourceLocation ICON_LOCK = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock");
    private static final ResourceLocation UNLOCK_PROGRESS_BAR = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/unlock_progress_bar");
    private static final ResourceLocation UNLOCK_PROGRESS_BAR_INNER = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/unlock_progress_bar_inner");
    private static final ResourceLocation SETTINGS = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/settings");
    private static final ResourceLocation ARROW_LEFT = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/arrow_left");

    private static final Component SAVE = Component.translatable("backpacked.button.save");
    private static final Component SHOW_PARTICLES = Component.translatable("backpacked.button.show_particles");
    private static final Component HIDE_WITH_ELYTRA = Component.translatable("backpacked.button.hide_with_elytra");
    private static final Component LOCKED = Component.translatable("backpacked.gui.locked").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
    private static final Component COSMETIC_WARNING = Component.translatable("backpacked.gui.cosmetic_warning");
    private static final Component BACK_TO_INVENTORY = Component.translatable("backpacked.gui.back_to_inventory");
    private static final Component UNSAVED_CHANGES = Component.translatable("backpacked.gui.unsaved_changes").withStyle(ChatFormatting.RED);

    private static final int PLAYER_DISPLAY_WIDTH = 80;

    private static final int MAX_VISIBLE_ITEMS = 5;
    private static final int ITEM_WIDTH = 133;
    private static final int ITEM_HEIGHT = 24;
    private static final int ITEM_LIST_GAP = 2;
    private static final int ITEM_LIST_LEFT = 96;
    private static final int ITEM_LIST_TOP = 30;
    private static final int ITEM_LIST_WIDTH = ITEM_WIDTH;
    private static final int ITEM_LIST_HEIGHT = ITEM_HEIGHT * MAX_VISIBLE_ITEMS + ITEM_LIST_GAP * MAX_VISIBLE_ITEMS - 1;

    private static final int DEFAULT_ITEM_TEXT_COLOUR = 0xFF5C5145;
    private static final int SELECTED_ITEM_TEXT_COLOUR = 0xFFFFFFFF;
    private static final int UNLOCKED_ITEM_TEXT_COLOUR = 0xFF685E4A;
    private static final int MODEL_LIGHTING = 0xF000F0;

    private final int backpackIndex;
    private final int windowWidth;
    private final int windowHeight;
    private final boolean showCosmeticWarning;
    private int windowLeft;
    private int windowTop;
    private FrameworkButton saveButton;
    private FrameworkButton settingsButton;
    private FrameworkButton backButton;
    private CosmeticProperties currentProperties;
    private CosmeticProperties displayBackpack = null;
    private final List<CosmeticItem> items;
    private PlayerDisplay playerDisplay;
    private final MutableInt scroll = new MutableInt();
    private ScrollBar scrollBar;
    private int tickCount;

    public CustomiseBackpackScreen(int backpackIndex, Map<ResourceLocation, Component> progressMap, CosmeticProperties properties, boolean showCosmeticWarning, Map<ResourceLocation, Double> completionMap)
    {
        super(Component.translatable("backpacked.title.customise_backpack"));
        this.backpackIndex = backpackIndex;
        this.windowWidth = 260;
        this.windowHeight = 174;
        Comparator<BackpackModelItem> compareUnlock = Comparator.comparing(e -> !e.backpack.isUnlocked(Minecraft.getInstance().player));
        Comparator<BackpackModelItem> compareLabel = Comparator.comparing(e -> e.label.getString());
        List<CosmeticItem> items = ClientRegistry.instance().getBackpacks()
                .stream()
                .map(backpack -> new BackpackModelItem(backpack, progressMap, completionMap))
                .sorted(compareUnlock.thenComparing(compareLabel))
                .collect(Collectors.toCollection(ArrayList::new));
        if(!Config.CLIENT.hideAddonsCallToAction.get()) {
            items.add(new GuideItem());
        }
        this.items = ImmutableList.copyOf(items);
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
            this.displayBackpack = this.currentProperties;
        }

        this.windowLeft = (this.width - this.windowWidth) / 2;
        this.windowTop = (this.height - this.windowHeight) / 2;
        int contentHeight = this.windowHeight - 27 - 13;

        this.playerDisplay = this.addRenderableWidget(new PlayerDisplay(this.minecraft.player, this.windowLeft + 10, this.windowTop + 27, PLAYER_DISPLAY_WIDTH, contentHeight - 1 - 20, () -> {
            return this.displayBackpack;
        }));

        this.saveButton = this.addRenderableWidget(BackpackButtons.builder()
            .setPosition(this.windowLeft + 10, this.playerDisplay.getBottom() + 1)
            .setSize(60, 20)
            .setLabel(SAVE)
            .setAction(btn -> {
                Network.getPlay().sendToServer(new MessageBackpackCosmetics(this.backpackIndex, this.displayBackpack));
                this.currentProperties = this.displayBackpack;
            }).build());

        DropdownMenu settingMenu = DropdownMenu.builder(this)
            .setMinItemSize(70, 16)
            .addItem(CheckboxItem.create(HIDE_WITH_ELYTRA, new MutableBoolean(!this.displayBackpack.showWithElytra()), value -> {
                this.displayBackpack = this.displayBackpack.setShowWithElytra(!value);
                return false;
            }))
            .addItem(CheckboxItem.create(SHOW_PARTICLES, new MutableBoolean(this.displayBackpack.showEffects()), value -> {
                this.displayBackpack = this.displayBackpack.setShowEffects(value);
                return false;
            }))
            .setAlignment(Alignment.ABOVE_LEFT)
            .build();
        this.settingsButton = this.addRenderableWidget(BackpackButtons.builder()
            .setPosition(this.saveButton.getX() + this.saveButton.getWidth(), this.saveButton.getY())
            .setSize(20, 20)
            .setIcon(SETTINGS, 10, 10)
            .setAction(settingMenu::show)
            .build()
        );

        this.scrollBar = this.addRenderableWidget(new ScrollBar(this.windowLeft + this.windowWidth - 24, this.windowTop + 29, contentHeight - 4, this.scroll));
        this.scrollBar.active = this.items.size() > MAX_VISIBLE_ITEMS;

        this.backButton = this.addRenderableWidget(BackpackButtons.builder()
            .setPosition(this.windowLeft - 20, this.windowTop + (this.windowHeight - 17 - 20) / 2 + 17)
            .setSize(16, 16)
            .setIcon(ARROW_LEFT, 4, 6)
            .setAction(btn -> {
                Network.getPlay().sendToServer(new MessageOpenBackpack());
            }).build()
        );

        this.updateButtons();
    }

    private void updateButtons()
    {
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
        this.tickCount++;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderTransparentBackground(graphics);
        this.drawBackgroundWindow(graphics, this.windowLeft, this.windowTop, this.windowWidth, this.windowHeight);
        this.renderWarning(graphics);

        int scrollBarBgX = this.scrollBar.getX() - 2;
        int scrollBarBgY = this.scrollBar.getY() - 2;
        int scrollBarBgWidth = this.scrollBar.getWidth() + 4;
        int scrollBarBgHeight = this.scrollBar.getHeight() + 4;
        graphics.blitSprite(ROUNDED_BOX, scrollBarBgX, scrollBarBgY, scrollBarBgWidth, scrollBarBgHeight);

        int itemBgX = this.playerDisplay.getRight() + 3;
        int itemBgWidth = (this.scrollBar.getX() - 2 - 2) - itemBgX;
        graphics.blitSprite(ROUNDED_BOX, itemBgX, this.windowTop + 27, itemBgWidth, scrollBarBgHeight);

        if(this.backButton.isHovered())
        {
            if(this.saveButton.active)
            {
                this.setTooltipForNextRenderPass(List.of(
                    BACK_TO_INVENTORY.getVisualOrderText(),
                    UNSAVED_CHANGES.getVisualOrderText()
                ));
            }
            else
            {
                this.setTooltipForNextRenderPass(BACK_TO_INVENTORY);
            }
        }
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Draw title
        int titleWidth = this.font.width(this.title);
        graphics.drawString(this.font, this.title, this.windowLeft + (this.windowWidth - titleWidth) / 2, this.windowTop + 6, 0xFF61503D, false);

        // Draw backpack items
        int startIndex = (int) (Math.max(0, this.items.size() - MAX_VISIBLE_ITEMS) * this.scrollBar.getScroll(mouseY));
        for(int i = startIndex; i < this.items.size() && i < startIndex + MAX_VISIBLE_ITEMS; i++)
        {
            int itemX = this.windowLeft + ITEM_LIST_LEFT;
            int itemY = this.windowTop + ITEM_LIST_TOP + (i - startIndex) * (ITEM_HEIGHT + ITEM_LIST_GAP);
            graphics.enableScissor(itemX, itemY, itemX + ITEM_WIDTH, itemY + ITEM_HEIGHT);
            this.items.get(i).draw(graphics, itemX, itemY, mouseX, mouseY, partialTick, this.minecraft);
            graphics.disableScissor();
        }

        if(this.hasPopupMenu())
            return;

        int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
        if(hoveredIndex != -1)
        {
            int itemX = this.windowLeft + ITEM_LIST_LEFT;
            int itemY = this.windowTop + ITEM_LIST_TOP + (hoveredIndex - startIndex) * (ITEM_HEIGHT + ITEM_LIST_GAP);
            CosmeticItem item = this.items.get(hoveredIndex);
            item.onMouseHover(this.minecraft, itemX, itemY, mouseX, mouseY);
        }
    }

    private void renderWarning(GuiGraphics graphics)
    {
        if(!this.showCosmeticWarning)
            return;

        int messageWidth = this.font.width(COSMETIC_WARNING);
        int messageBgWidth = 7 + messageWidth + 7;
        int messageY = 8;
        graphics.fillGradient(0, 0, this.width, 50, 0xAA000000, 0x00000000);
        graphics.blitSprite(LABEL_WARNING_BACKGROUND, (this.width - messageBgWidth) / 2, messageY, messageBgWidth, 20);
        graphics.drawString(this.font, COSMETIC_WARNING, (this.width - messageWidth) / 2, messageY + 6, 0xFFFFFFFF);
    }

    private void drawBackgroundWindow(GuiGraphics graphics, int x, int y, int width, int height)
    {
        int titleWidth = this.font.width(this.title);
        int labelWidth = 20 + titleWidth + 20;
        int labelX = x + (this.windowWidth - labelWidth) / 2;
        graphics.blitSprite(LABEL_BACKGROUND, labelX, y, labelWidth, 21);

        int titleX = x + (this.windowWidth - titleWidth) / 2;
        int checkersX = labelX + 5;
        int checkersWidth = titleX - checkersX - 2;
        if(checkersWidth > 0)
        {
            graphics.blitSprite(CHECKERS, checkersX, y + 7, checkersWidth, 5);
            graphics.blitSprite(CHECKERS, titleX + titleWidth + 1, y + 7, checkersWidth, 5);
        }

        int backPanelX = this.backButton.getX() - 6;
        int backPanelY = this.backButton.getY() - 5;
        graphics.blitSprite(LABEL_BACKGROUND, backPanelX, backPanelY, 50, 26);

        graphics.blitSprite(BACKPACK_BACKGROUND, x, y + 17, width, height - 17);
    }

    public static void drawBackpackInGui(Minecraft mc, GuiGraphics graphics, ClientBackpack backpack, int x, int y, float partialTick, int tickCount)
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
            }, tickCount);
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
        if(ScreenUtil.isPointInArea(mouseX, mouseY, this.windowLeft + ITEM_LIST_LEFT, this.windowTop + ITEM_LIST_TOP, ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT))
        {
            int startIndex = (int) (Math.max(0, this.items.size() - MAX_VISIBLE_ITEMS) * this.scrollBar.getScroll(mouseY));
            int offsetIndex = (mouseY - this.windowTop - ITEM_LIST_TOP) / (ITEM_HEIGHT + ITEM_LIST_GAP);
            int hoveredIndex = startIndex + offsetIndex;
            if(hoveredIndex >= 0 && hoveredIndex < this.items.size())
            {
                return hoveredIndex;
            }
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(!this.hasPopupMenu())
        {
            if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + ITEM_LIST_LEFT, this.windowTop + ITEM_LIST_TOP, ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT))
            {
                if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
                {
                    int hoveredIndex = this.getHoveredIndex((int) mouseX, (int) mouseY);
                    if(hoveredIndex != -1)
                    {
                        CosmeticItem item = this.items.get(hoveredIndex);
                        if(item.onMouseClicked(this.minecraft))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        if(!this.hasPopupMenu() && !this.scrollBar.isGrabbed() && ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.windowLeft + ITEM_LIST_LEFT, this.windowTop + ITEM_LIST_TOP, ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT))
        {
            int scrollableContentHeight = Math.max(this.items.size() * (ITEM_HEIGHT + ITEM_LIST_GAP) - ITEM_LIST_HEIGHT, 0);
            double scrollNormal = this.scrollBar.getScroll((int) mouseY);
            int currentIndex = (int) (scrollableContentHeight * scrollNormal) / (ITEM_HEIGHT + ITEM_LIST_GAP);
            int nextIndex = currentIndex + Mth.sign(-deltaY);
            double amount = (double) nextIndex / Math.max(this.items.size() - MAX_VISIBLE_ITEMS, 1);
            this.scrollBar.scrollTo(amount);
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public void removed()
    {
        super.removed();
        MouseRestorer.capturePosition();
    }

    private abstract static class CosmeticItem
    {
        protected abstract void draw(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTick, Minecraft mc);

        protected boolean onMouseClicked(Minecraft mc)
        {
            return false;
        }

        protected void onMouseHover(Minecraft mc, int x, int y, int mouseX, int mouseY) {}
    }

    private class BackpackModelItem extends CosmeticItem
    {
        private final ResourceLocation cosmeticId;
        private final ClientBackpack backpack;
        private final Component label;
        private final List<FormattedCharSequence> unlockTooltip;
        private final double completionProgress;

        public BackpackModelItem(ClientBackpack backpack, Map<ResourceLocation, Component> labelMap, Map<ResourceLocation, Double> completionMap)
        {
            this.cosmeticId = backpack.getId();
            this.backpack = backpack;
            this.label = Component.translatable(backpack.getTranslationKey());
            Component unlockMessage = Component.translatable(backpack.getTranslationKey() + ".unlock");
            List<FormattedCharSequence> list = new ArrayList<>(Minecraft.getInstance().font.split(unlockMessage, 150));
            list.addFirst(Language.getInstance().getVisualOrder(LOCKED));
            if(labelMap.containsKey(backpack.getId()))
            {
                Component component = labelMap.get(backpack.getId()).plainCopy().withStyle(ChatFormatting.YELLOW);
                list.add(Language.getInstance().getVisualOrder(component));
            }
            this.unlockTooltip = ImmutableList.copyOf(list);
            this.completionProgress = completionMap.getOrDefault(backpack.getId(), 1.0);
        }

        @Override
        protected void draw(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTick, Minecraft mc)
        {
            boolean unlocked = this.backpack.isUnlocked(mc.player);
            boolean selected = unlocked && CustomiseBackpackScreen.this.displayBackpack.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic()).equals(this.cosmeticId);
            boolean hovered = unlocked && (selected || ScreenUtil.isPointInArea(mouseX, mouseY, x, y, ITEM_WIDTH, ITEM_HEIGHT));

            // Draw background for item
            ResourceLocation itemTexture = this.getItemTexture(unlocked, selected, hovered);
            graphics.blitSprite(itemTexture, x, y, ITEM_WIDTH, ITEM_HEIGHT);

            if(!unlocked)
            {
                int progressBarX = x + 24;
                int progressBarY = y + ITEM_HEIGHT - 5 - 4;
                graphics.blitSprite(UNLOCK_PROGRESS_BAR, progressBarX, progressBarY, 89, 5);

                int progressWidth = (int) (87 * this.completionProgress);
                graphics.blitSprite(UNLOCK_PROGRESS_BAR_INNER, progressBarX + 1, progressBarY + 1, progressWidth, 3);

                graphics.blitSprite(ICON_LOCK, x + ITEM_WIDTH - 12 - 4, y + 6, 12, 12);
            }

            // Draw label
            int textColour = this.getItemTextColour(unlocked, selected);
            int textY = y + (unlocked ? 8 : 5);
            graphics.drawString(mc.font, this.label, x + 24, textY, textColour, selected);

            // Draw backpack cosmetic
            drawBackpackInGui(mc, graphics, this.backpack, x + 12, y + 12, partialTick, CustomiseBackpackScreen.this.tickCount);
        }

        @Override
        protected void onMouseHover(Minecraft mc, int x, int y, int mouseX, int mouseY)
        {
            if(!this.backpack.isUnlocked(mc.player))
            {
                int progressBarX = x + 24;
                int progressBarY = y + ITEM_HEIGHT - 5 - 4;
                int lockX = x + ITEM_WIDTH - 12 - 4;
                int lockY = y + 6;
                if(ScreenUtil.isPointInArea(mouseX, mouseY, progressBarX, progressBarY, 89, 5) || ScreenUtil.isPointInArea(mouseX, mouseY, lockX, lockY, 12, 12))
                {
                    CustomiseBackpackScreen.this.setTooltipForNextRenderPass(this.unlockTooltip);
                }
            }
        }

        @Override
        protected boolean onMouseClicked(Minecraft mc)
        {
            if(this.backpack.isUnlocked(mc.player))
            {
                if(!CustomiseBackpackScreen.this.displayBackpack.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic()).equals(this.cosmeticId))
                {
                    CustomiseBackpackScreen.this.displayBackpack = CustomiseBackpackScreen.this.displayBackpack.setCosmetic(this.cosmeticId);
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
                return true;
            }
            return false;
        }

        private ResourceLocation getItemTexture(boolean unlocked, boolean selected, boolean hovered)
        {
            if(selected) return LIST_ITEM_SELECTED;
            if(unlocked) return hovered ? LIST_ITEM_FOCUSED : LIST_ITEM;
            return LIST_ITEM_LOCKED;
        }

        private int getItemTextColour(boolean unlocked, boolean selected)
        {
            if(selected) return SELECTED_ITEM_TEXT_COLOUR;
            if(unlocked) return UNLOCKED_ITEM_TEXT_COLOUR;
            return DEFAULT_ITEM_TEXT_COLOUR;
        }
    }

    private class GuideItem extends CosmeticItem
    {
        private static final Component MESSAGE = Component.translatable("backpacked.gui.want_more_backpacks");
        private static final Component VIEW_ADDONS = Component.translatable("backpacked.gui.view_addons");

        private final FrameworkButton button = BackpackButtons.builder()
                .setSize(100, 14)
                .setLabel(VIEW_ADDONS)
                .setAction(btn -> {
                    Style style = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mrcrayfish.github.io/Backpacked/"));
                    CustomiseBackpackScreen.this.handleComponentClicked(style);
                }).build();

        @Override
        protected void draw(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTick, Minecraft mc)
        {
            int width = mc.font.width(MESSAGE);
            graphics.drawString(mc.font, MESSAGE, x + (ITEM_WIDTH - width) / 2, y + 1, UNLOCKED_ITEM_TEXT_COLOUR, false);

            this.button.setX(x + (ITEM_WIDTH - this.button.getWidth()) / 2);
            this.button.setY(y + ITEM_HEIGHT - this.button.getHeight());
            this.button.render(graphics, mouseX, mouseY, partialTick);
        }

        @Override
        protected boolean onMouseClicked(Minecraft mc)
        {
            if(this.button.isHovered())
            {
                this.button.onPress();
            }
            return false;
        }
    }
}
