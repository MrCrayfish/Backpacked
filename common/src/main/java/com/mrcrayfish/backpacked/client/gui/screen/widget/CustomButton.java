package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.LabelAndDescription;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomButton extends AbstractButton
{
    private static final WidgetSprites DEFAULT_SPRITES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_disabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled_focused")
    );
    private static final int DEFAULT_TOOLTIP_DELAY = 350;

    private final Message message;
    private final @Nullable Icon icon;
    private final int gap;
    private final @Nullable Map<Integer, Action<CustomButton>> actions;
    private final @Nullable WidgetSprites texture;
    private final @Nullable Controller controller;
    private final @Nullable Supplier<Boolean> activeSupplier;
    private final @Nullable Function<CustomButton, Tooltip> tooltip;
    private final int tooltipOptions;
    private @Nullable Tooltip currentTooltip;
    private boolean shiftWasDown;
    private final @Nullable ContentRenderer contentRenderer;

    private CustomButton(int x, int y, int width, int height, Message message, @Nullable Icon icon, int gap, @Nullable Map<Integer, Action<CustomButton>> actions, @Nullable WidgetSprites texture, @Nullable Controller controller, @Nullable Supplier<Boolean> activeSupplier, @Nullable Function<CustomButton, Tooltip> tooltip, int tooltipDelay, int tooltipOptions, @Nullable ContentRenderer contentRenderer)
    {
        super(x, y, width, height, message.component());
        this.message = message;
        this.icon = icon;
        this.gap = gap;
        this.actions = actions;
        this.texture = texture;
        this.controller = controller;
        this.activeSupplier = activeSupplier;
        this.tooltip = tooltip;
        this.tooltipOptions = tooltipOptions;
        this.contentRenderer = contentRenderer;
        this.updateActiveState();
        this.rebuildTooltip();
        this.setTooltipDelay(Duration.ofMillis(tooltipDelay));
    }

    @Override
    public Component getMessage()
    {
        return this.message.component();
    }

    @Nullable
    public Icon getIcon()
    {
        return this.icon;
    }

    @Nullable
    public WidgetSprites getTexture()
    {
        return this.texture;
    }

    public int getGap()
    {
        return this.gap;
    }

    private void onAction(int button)
    {
        if(button == 0)
        {
            if(this.controller != null)
            {
                this.controller.run();
            }
        }
        Holder<SoundEvent> sound = SoundEvents.UI_BUTTON_CLICK;
        Action<CustomButton> action = this.actions != null ? this.actions.get(button) : null;
        if(action != null)
        {
            action.handler().accept(this);
            sound = action.sound();
        }
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        this.rebuildTooltip();
    }

    private void updateActiveState()
    {
        if(this.activeSupplier != null)
        {
            this.active = this.activeSupplier.get();
        }
    }

    public void rebuildTooltip()
    {
        if(this.tooltip != null)
        {
            this.currentTooltip = this.tooltip.apply(this);
        }
    }

    private void updateTooltip()
    {
        if((this.tooltipOptions & TooltipOptions.REBUILD_TOOLTIP_ON_SHIFT) != 0)
        {
            if(!this.shiftWasDown && Screen.hasShiftDown() && this.isHovered())
            {
                this.rebuildTooltip();
                this.shiftWasDown = true;
            }
        }
        if(this.shiftWasDown && !Screen.hasShiftDown())
        {
            this.rebuildTooltip();
            this.shiftWasDown = false;
        }
        if(!this.active && (this.tooltipOptions & TooltipOptions.DISABLE_TOOLTIP_WHEN_WIDGET_INACTIVE) != 0)
        {
            this.setTooltip(null);
        }
        else
        {
            this.setTooltip(this.currentTooltip);
        }
    }

    @Override
    public void onPress()
    {
        this.onAction(0);
    }

    @Override
    public void playDownSound(SoundManager manager) {}

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.updateActiveState();
        this.updateTooltip();
        if(this.contentRenderer != null)
        {
            this.contentRenderer.draw(this, graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.defaultButtonNarrationText(output);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(this.active && this.visible && this.isValidClickButton(button) && this.clicked(mouseX, mouseY))
        {
            this.onAction(button);
        }
        return false;
    }

    @Override
    protected boolean isValidClickButton(int button)
    {
        return button == 0 || this.actions != null && this.actions.containsKey(button);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder state(Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return new Builder(new StateController(getter, setter));
    }

    public static <T extends Enum<T> & LabelAndDescription> Builder values(Supplier<T> getter, Consumer<T> setter)
    {
        return new Builder(new EnumController<>(getter, setter))
            .setMessage(() -> getter.get().label())
            .setTooltip(btn -> Tooltip.create(getter.get().description()));
    }

    public static final class Builder
    {
        private int x;
        private int y;
        private int width = 20;
        private int height = 20;
        private Message message = new ConstantMessage(CommonComponents.EMPTY);
        private @Nullable Icon icon;
        private int gap = 2;
        private @Nullable Map<Integer, Action<CustomButton>> actions;
        private @Nullable WidgetSprites texture = DEFAULT_SPRITES;
        private @Nullable Controller controller;
        private @Nullable Supplier<Boolean> active;
        private @Nullable Function<CustomButton, Tooltip> tooltip;
        private int tooltipDelay = DEFAULT_TOOLTIP_DELAY;
        private int tooltipOptions;
        private @Nullable ContentRenderer contentRenderer = DefaultContentRenderer.INSTANCE;

        private Builder() {}

        private Builder(@Nullable Controller controller)
        {
            this.controller = controller;
        }

        public CustomButton build()
        {
            return new CustomButton(this.x, this.y, this.width, this.height, this.message, this.icon, this.gap, this.actions, this.texture, this.controller, this.active, this.tooltip, this.tooltipDelay, this.tooltipOptions, this.contentRenderer);
        }

        private Map<Integer, Action<CustomButton>> actions()
        {
            if(this.actions == null)
            {
                this.actions = new HashMap<>();
            }
            return this.actions;
        }

        public Builder setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setMessage(Component message)
        {
            this.message = new ConstantMessage(message);
            return this;
        }

        public Builder setMessage(Supplier<Component> supplier)
        {
            this.message = new DynamicMessage(supplier);
            return this;
        }

        public Builder setIcon(ResourceLocation sprite, int width, int height)
        {
            this.icon = new StaticIcon(sprite, width, height);
            return this;
        }

        public Builder setIcon(Function<CustomButton, ResourceLocation> function, int width, int height)
        {
            this.icon = new DynamicIcon(function, width, height);
            return this;
        }

        public Builder setGap(int gap)
        {
            this.gap = gap;
            return this;
        }

        public Builder setAction(Consumer<CustomButton> action)
        {
            this.actions().put(0, Action.create(action));
            return this;
        }

        public Builder setPrimaryAction(Consumer<CustomButton> action)
        {
            this.actions().put(0, Action.create(action));
            return this;
        }

        public Builder setPrimaryAction(Action<CustomButton> action)
        {
            this.actions().put(0, action);
            return this;
        }

        public Builder setSecondaryAction(Consumer<CustomButton> action)
        {
            this.actions().put(1, Action.create(action));
            return this;
        }

        public Builder setSecondaryAction(Action<CustomButton> action)
        {
            this.actions().put(1, action);
            return this;
        }

        public Builder setTertiaryAction(Consumer<CustomButton> action)
        {
            this.actions().put(2, Action.create(action));
            return this;
        }

        public Builder setTertiaryAction(Action<CustomButton> action)
        {
            this.actions().put(2, action);
            return this;
        }

        public Builder setTexture(WidgetSprites texture)
        {
            this.texture = texture;
            return this;
        }

        public Builder noTexture()
        {
            this.texture = null;
            return this;
        }

        public Builder setActive(Supplier<Boolean> active)
        {
            this.active = active;
            return this;
        }

        public Builder setTooltip(Function<CustomButton, Tooltip> tooltip)
        {
            this.tooltip = tooltip;
            return this;
        }

        public Builder setTooltipDelay(int delay)
        {
            this.tooltipDelay = delay;
            return this;
        }

        public Builder setTooltipOptions(int options)
        {
            this.tooltipOptions = options;
            return this;
        }

        public Builder setContentRenderer(@Nullable ContentRenderer renderer)
        {
            this.contentRenderer = renderer;
            return this;
        }
    }

    private interface Message
    {
        Component component();
    }

    private record ConstantMessage(Component component) implements Message {}

    private record DynamicMessage(Supplier<Component> supplier) implements Message
    {
        public Component component()
        {
            return this.supplier.get();
        }
    }

    public interface Icon
    {
        ResourceLocation sprite(CustomButton button);

        int width();

        int height();
    }

    private record StaticIcon(ResourceLocation sprite, int width, int height) implements Icon
    {
        @Override
        public ResourceLocation sprite(CustomButton button)
        {
            return this.sprite;
        }
    }

    private record DynamicIcon(Function<CustomButton, ResourceLocation> function, int width, int height) implements Icon
    {
        @Override
        public ResourceLocation sprite(CustomButton button)
        {
            return this.function.apply(button);
        }
    }

    private interface Controller
    {
        void run();
    }

    private record StateController(Supplier<Boolean> getter, Consumer<Boolean> setter) implements Controller
    {
        @Override
        public void run()
        {
            this.setter.accept(!this.getter.get());
        }
    }

    private record EnumController<T extends Enum<T> & LabelAndDescription>(Supplier<T> getter, Consumer<T> setter) implements Controller
    {
        @Override
        public void run()
        {
            T currentValue = this.getter.get();
            T[] values = currentValue.getDeclaringClass().getEnumConstants();
            T nextValue = values[(currentValue.ordinal() + 1) % values.length];
            this.setter.accept(nextValue);
        }
    }

    public interface ContentRenderer
    {
        void draw(CustomButton button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
    }

    public static class DefaultContentRenderer implements ContentRenderer
    {
        public static final DefaultContentRenderer INSTANCE = new DefaultContentRenderer();

        private DefaultContentRenderer() {}

        @Override
        public void draw(CustomButton button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if(button.texture != null)
            {
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                graphics.blitSprite(button.texture.get(button.active, button.isHovered() && button.active), button.getX(), button.getY(), button.getWidth(), button.getHeight());
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }

            Component message = button.getMessage();
            Font font = Minecraft.getInstance().font;
            int contentWidth = font.width(message);
            int contentHeight = contentWidth > 0 ? font.lineHeight + 1 : 0;
            if(button.icon != null)
            {
                // Only add gap if the message is not empty
                if(contentWidth > 0)
                {
                    contentWidth += button.gap;
                }
                contentWidth += button.icon.width();
                contentHeight = Math.max(contentHeight, button.icon.height());
            }
            int contentLeft = button.getX() + (button.getWidth() - contentWidth) / 2;
            int contentTop = button.getY() + (button.getHeight() - contentHeight) / 2;

            int textX = contentLeft + (button.icon != null ? button.gap + button.icon.width() : 0);
            int textY = contentTop + (contentHeight - font.lineHeight) / 2 + 1;
            int textColour = button.active ? 0xFFFFFFFF : 0xFF8C7E6D;
            graphics.drawString(font, message, textX, textY, textColour, button.active);

            if(button.icon != null)
            {
                int iconX = contentLeft;
                int iconY = contentTop + (contentHeight - button.icon.height()) / 2;
                RenderSystem.enableBlend();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                graphics.blitSprite(button.icon.sprite(button), iconX, iconY, button.icon.width(), button.icon.height());
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }
        }
    }

    public static class ToggleContentRenderer implements ContentRenderer
    {
        public static final ToggleContentRenderer INSTANCE = new ToggleContentRenderer();

        private static final int TOGGLE_SIZE = 6;
        private static final WidgetSprites TOGGLE_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/toggle_on"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/toggle_off"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/toggle_on")
        );

        private ToggleContentRenderer() {}

        @Override
        public void draw(CustomButton button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if(button.texture != null)
            {
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                graphics.blitSprite(button.texture.get(button.active, button.isHovered() && button.active), button.getX(), button.getY(), button.getWidth(), button.getHeight());
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }

            Component message = button.getMessage();
            Font font = Minecraft.getInstance().font;
            int contentLeft = button.getX() + 6;
            int textX = contentLeft + (button.icon != null ? button.gap + button.icon.width() : 0);
            int textY = button.getY() + (button.getHeight() - font.lineHeight) / 2 + 1;
            int textColour = button.active ? 0xFFFFFFFF : 0xFF8C7E6D;
            graphics.drawString(font, message, textX, textY, textColour, button.active);

            if(button.icon != null)
            {
                int iconX = contentLeft;
                int iconY = button.getY() + (button.getHeight() - button.icon.height()) / 2;
                RenderSystem.enableBlend();
                graphics.setColor(1, 1, 1, button.active ? 1.0F : 0.5F);
                graphics.blitSprite(button.icon.sprite(button), iconX, iconY, button.icon.width(), button.icon.height());
                graphics.setColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
            }

            int yOffset = (button.getHeight() - TOGGLE_SIZE) / 2;
            int stateIconY = button.getY() + yOffset;
            int stateIconX = button.getX() + button.getWidth() - TOGGLE_SIZE - yOffset;
            boolean value = button.controller instanceof StateController state ? state.getter.get() : false;
            graphics.blitSprite(TOGGLE_SPRITES.get(value, button.isHovered()), stateIconX, stateIconY, TOGGLE_SIZE, TOGGLE_SIZE);
        }
    }
}
