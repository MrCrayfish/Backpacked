package com.mrcrayfish.backpacked.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.ClientRegistry;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import com.mrcrayfish.backpacked.client.backpack.ModelMeta;
import com.mrcrayfish.backpacked.client.gui.MouseRestorer;
import com.mrcrayfish.backpacked.client.gui.pip.GuiBackpackRenderState;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.PlayerDisplay;
import com.mrcrayfish.backpacked.client.gui.screen.widget.ScrollBar;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.Alignment;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.CustomScreen;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.DropdownMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.item.CheckboxItem;
import com.mrcrayfish.backpacked.client.renderer.backpack.LevelDataState;
import com.mrcrayfish.backpacked.client.renderer.backpack.LivingEntityDataState;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageBackpackCosmetics;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joml.Matrix3x2f;
import org.lwjgl.glfw.GLFW;

import java.net.URI;
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
    private static final Identifier BACKPACK_BACKGROUND = Utils.id("backpack/background");
    private static final Identifier LABEL_BACKGROUND = Utils.id("backpack/label");
    private static final Identifier CHECKERS = Utils.id("backpack/checkers");
    private static final Identifier LABEL_WARNING_BACKGROUND = Utils.id("backpack/label_warning");
    private static final Identifier ROUNDED_BOX = Utils.id("backpack/rounded_box");
    private static final Identifier LIST_ITEM = Utils.id("backpack/list_item");
    private static final Identifier LIST_ITEM_FOCUSED = Utils.id("backpack/list_item_focused");
    private static final Identifier LIST_ITEM_SELECTED = Utils.id("backpack/list_item_selected");
    private static final Identifier LIST_ITEM_LOCKED = Utils.id("backpack/list_item_locked");
    private static final Identifier ICON_LOCK = Utils.id("backpack/lock");
    private static final Identifier UNLOCK_PROGRESS_BAR = Utils.id("backpack/unlock_progress_bar");
    private static final Identifier UNLOCK_PROGRESS_BAR_INNER = Utils.id("backpack/unlock_progress_bar_inner");
    private static final Identifier SETTINGS = Utils.id("backpack/settings");
    private static final Identifier ARROW_LEFT = Utils.id("backpack/arrow_left");

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

    public CustomiseBackpackScreen(int backpackIndex, Map<Identifier, Component> progressMap, CosmeticProperties properties, boolean showCosmeticWarning, Map<Identifier, Double> completionMap)
    {
        super(Component.translatable("backpacked.title.customise_backpack"));
        this.backpackIndex = backpackIndex;
        this.windowWidth = 260;
        this.windowHeight = 174;
        Comparator<BackpackModelItem> compareUnlock = Comparator.comparing(e -> !e.backpack.isUnlocked(Minecraft.getInstance().player));
        Comparator<BackpackModelItem> compareLabel = Comparator.comparing(e -> e.label.getString());
        List<CosmeticItem> items = ClientRegistry.instance().getBackpacks()
                .stream()
                .filter(backpack -> !BackpackHelper.isCosmeticDisabled(backpack.getId()))
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
    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
    {
        super.extractTransparentBackground(extractor);
        this.extractBackgroundWindow(extractor, this.windowLeft, this.windowTop, this.windowWidth, this.windowHeight);
        this.extractWarning(extractor);

        int scrollBarBgX = this.scrollBar.getX() - 2;
        int scrollBarBgY = this.scrollBar.getY() - 2;
        int scrollBarBgWidth = this.scrollBar.getWidth() + 4;
        int scrollBarBgHeight = this.scrollBar.getHeight() + 4;
        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, ROUNDED_BOX, scrollBarBgX, scrollBarBgY, scrollBarBgWidth, scrollBarBgHeight);

        int itemBgX = this.playerDisplay.getRight() + 3;
        int itemBgWidth = (this.scrollBar.getX() - 2 - 2) - itemBgX;
        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, ROUNDED_BOX, itemBgX, this.windowTop + 27, itemBgWidth, scrollBarBgHeight);

        if(this.backButton.isHovered())
        {
            if(this.saveButton.active)
            {
                extractor.setTooltipForNextFrame(List.of(BACK_TO_INVENTORY.getVisualOrderText(), UNSAVED_CHANGES.getVisualOrderText()), mouseX, mouseY);
            }
            else
            {
                extractor.setTooltipForNextFrame(List.of(BACK_TO_INVENTORY.getVisualOrderText()), mouseX, mouseY);
            }
        }
    }

    @Override
    public void extractForeground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
    {
        // Draw title
        int titleWidth = this.font.width(this.title);
        extractor.text(this.font, this.title, this.windowLeft + (this.windowWidth - titleWidth) / 2, this.windowTop + 6, 0xFF61503D, false);

        // Draw backpack items
        int startIndex = (int) (Math.max(0, this.items.size() - MAX_VISIBLE_ITEMS) * this.scrollBar.getScroll(mouseY));
        for(int i = startIndex; i < this.items.size() && i < startIndex + MAX_VISIBLE_ITEMS; i++)
        {
            int itemX = this.windowLeft + ITEM_LIST_LEFT;
            int itemY = this.windowTop + ITEM_LIST_TOP + (i - startIndex) * (ITEM_HEIGHT + ITEM_LIST_GAP);
            extractor.enableScissor(itemX, itemY, itemX + ITEM_WIDTH, itemY + ITEM_HEIGHT);
            this.items.get(i).extract(extractor, itemX, itemY, mouseX, mouseY, partialTick, this.minecraft);
            extractor.disableScissor();
        }

        if(this.hasPopupMenu())
            return;

        int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
        if(hoveredIndex != -1)
        {
            int itemX = this.windowLeft + ITEM_LIST_LEFT;
            int itemY = this.windowTop + ITEM_LIST_TOP + (hoveredIndex - startIndex) * (ITEM_HEIGHT + ITEM_LIST_GAP);
            CosmeticItem item = this.items.get(hoveredIndex);
            item.onMouseHover(extractor, this.minecraft, itemX, itemY, mouseX, mouseY);
        }
    }

    private void extractWarning(GuiGraphicsExtractor extractor)
    {
        if(!this.showCosmeticWarning)
            return;

        int messageWidth = this.font.width(COSMETIC_WARNING);
        int messageBgWidth = 7 + messageWidth + 7;
        int messageY = 8;
        extractor.fillGradient(0, 0, this.width, 50, 0xAA000000, 0x00000000);
        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, LABEL_WARNING_BACKGROUND, (this.width - messageBgWidth) / 2, messageY, messageBgWidth, 20);
        extractor.text(this.font, COSMETIC_WARNING, (this.width - messageWidth) / 2, messageY + 6, 0xFFFFFFFF);
    }

    private void extractBackgroundWindow(GuiGraphicsExtractor extractor, int x, int y, int width, int height)
    {
        int titleWidth = this.font.width(this.title);
        int labelWidth = 20 + titleWidth + 20;
        int labelX = x + (this.windowWidth - labelWidth) / 2;
        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, LABEL_BACKGROUND, labelX, y, labelWidth, 21);

        int titleX = x + (this.windowWidth - titleWidth) / 2;
        int checkersX = labelX + 5;
        int checkersWidth = titleX - checkersX - 2;
        if(checkersWidth > 0)
        {
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, CHECKERS, checkersX, y + 7, checkersWidth, 5);
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, CHECKERS, titleX + titleWidth + 1, y + 7, checkersWidth, 5);
        }

        int backPanelX = this.backButton.getX() - 6;
        int backPanelY = this.backButton.getY() - 5;
        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, LABEL_BACKGROUND, backPanelX, backPanelY, 50, 26);

        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, BACKPACK_BACKGROUND, x, y + 17, width, height - 17);
    }

    public static void extractBackpackPictureInPicture(Minecraft mc, GuiGraphicsExtractor extractor, ClientBackpack backpack, int windowX, int windowY, int windowWidth, int windowHeight, float partialTick, int tickCount)
    {
        assert mc.player != null && mc.level != null;
        ModelMeta meta = ClientRegistry.instance().getModelMeta(backpack);
        GuiBackpackRenderState state = new GuiBackpackRenderState(
            meta.display().gui(),
            meta.renderer().orElse(null),
            LivingEntityDataState.create(mc.player, partialTick),
            LevelDataState.create(mc.level, partialTick),
            backpack.getBaseModel(),
            backpack.getStrapsModel(),
            mc.player.getId(),
            tickCount,
            partialTick,
            new Matrix3x2f(extractor.pose()),
            windowX,
            windowX + windowWidth,
            windowY,
            windowY + windowHeight,
            16,
            null
        );
        ClientServices.CLIENT.submitGuiPipRenderState(extractor, state);
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if(!this.hasPopupMenu())
        {
            if(ScreenUtil.isPointInArea((int) event.x(), (int) event.y(), this.windowLeft + ITEM_LIST_LEFT, this.windowTop + ITEM_LIST_TOP, ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT))
            {
                if(event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT)
                {
                    int hoveredIndex = this.getHoveredIndex((int) event.x(), (int) event.y());
                    if(hoveredIndex != -1)
                    {
                        CosmeticItem item = this.items.get(hoveredIndex);
                        if(item.onMouseClicked(event, this.minecraft))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
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
        protected abstract void extract(GuiGraphicsExtractor extractor, int x, int y, int mouseX, int mouseY, float partialTick, Minecraft mc);

        protected boolean onMouseClicked(MouseButtonEvent event, Minecraft mc)
        {
            return false;
        }

        protected void onMouseHover(GuiGraphicsExtractor extractor, Minecraft mc, int x, int y, int mouseX, int mouseY) {}
    }

    private class BackpackModelItem extends CosmeticItem
    {
        private final Identifier cosmeticId;
        private final ClientBackpack backpack;
        private final Component label;
        private final List<FormattedCharSequence> unlockTooltip;
        private final double completionProgress;

        public BackpackModelItem(ClientBackpack backpack, Map<Identifier, Component> labelMap, Map<Identifier, Double> completionMap)
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
        protected void extract(GuiGraphicsExtractor extractor, int x, int y, int mouseX, int mouseY, float partialTick, Minecraft mc)
        {
            boolean unlocked = this.backpack.isUnlocked(mc.player);
            boolean selected = unlocked && CustomiseBackpackScreen.this.displayBackpack.cosmetic().orElse(BackpackManager.getDefaultOrFallbackCosmetic()).equals(this.cosmeticId);
            boolean hovered = unlocked && (selected || ScreenUtil.isPointInArea(mouseX, mouseY, x, y, ITEM_WIDTH, ITEM_HEIGHT));

            // Draw background for item
            Identifier itemTexture = this.getItemTexture(unlocked, selected, hovered);
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, itemTexture, x, y, ITEM_WIDTH, ITEM_HEIGHT);

            if(!unlocked)
            {
                int progressBarX = x + 24;
                int progressBarY = y + ITEM_HEIGHT - 5 - 4;
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, UNLOCK_PROGRESS_BAR, progressBarX, progressBarY, 89, 5);

                int progressWidth = (int) (87 * this.completionProgress);
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, UNLOCK_PROGRESS_BAR_INNER, progressBarX + 1, progressBarY + 1, progressWidth, 3);

                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_LOCK, x + ITEM_WIDTH - 12 - 4, y + 6, 12, 12);
            }

            // Draw label
            int textColour = this.getItemTextColour(unlocked, selected);
            int textY = y + (unlocked ? 8 : 5);
            extractor.text(mc.font, this.label, x + 24, textY, textColour, selected);

            // Draw backpack cosmetic
            extractBackpackPictureInPicture(mc, extractor, this.backpack, x, y, ITEM_HEIGHT, ITEM_HEIGHT, partialTick, CustomiseBackpackScreen.this.tickCount);
        }

        @Override
        protected void onMouseHover(GuiGraphicsExtractor extractor, Minecraft mc, int x, int y, int mouseX, int mouseY)
        {
            if(Minecraft.getInstance().hasControlDown())
            {
                extractor.setTooltipForNextFrame(mc.font, Component.literal(this.backpack.getId().toString()), mouseX, mouseY);
                return;
            }

            if(!this.backpack.isUnlocked(mc.player))
            {
                int progressBarX = x + 24;
                int progressBarY = y + ITEM_HEIGHT - 5 - 4;
                int lockX = x + ITEM_WIDTH - 12 - 4;
                int lockY = y + 6;
                if(ScreenUtil.isPointInArea(mouseX, mouseY, progressBarX, progressBarY, 89, 5) || ScreenUtil.isPointInArea(mouseX, mouseY, lockX, lockY, 12, 12))
                {
                    extractor.setTooltipForNextFrame(mc.font, this.unlockTooltip, mouseX, mouseY);
                }
            }
        }

        @Override
        protected boolean onMouseClicked(MouseButtonEvent event, Minecraft mc)
        {
            if(Minecraft.getInstance().hasControlDown())
            {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.backpack.getId().toString());
                Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(Component.literal("Copied " + this.backpack.getId() + " to the clipboard"));
                return true;
            }

            if(event.button() == 0 && this.backpack.isUnlocked(mc.player))
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

        private Identifier getItemTexture(boolean unlocked, boolean selected, boolean hovered)
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
                    var event = new ClickEvent.OpenUrl(URI.create("https://mrcrayfish.github.io/Backpacked/"));
                    defaultHandleClickEvent(event, CustomiseBackpackScreen.this.minecraft, CustomiseBackpackScreen.this);
                }).build();

        @Override
        protected void extract(GuiGraphicsExtractor extractor, int x, int y, int mouseX, int mouseY, float partialTick, Minecraft mc)
        {
            int width = mc.font.width(MESSAGE);
            extractor.text(mc.font, MESSAGE, x + (ITEM_WIDTH - width) / 2, y + 1, UNLOCKED_ITEM_TEXT_COLOUR, false);

            this.button.setX(x + (ITEM_WIDTH - this.button.getWidth()) / 2);
            this.button.setY(y + ITEM_HEIGHT - this.button.getHeight());
            this.button.extractRenderState(extractor, mouseX, mouseY, partialTick);
        }

        @Override
        protected boolean onMouseClicked(MouseButtonEvent event, Minecraft mc)
        {
            if(this.button.isHovered())
            {
                this.button.onPress(event);
            }
            return false;
        }
    }
}
