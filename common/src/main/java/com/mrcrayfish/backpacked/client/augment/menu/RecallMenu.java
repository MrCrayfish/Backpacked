package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageCheckShelfKey;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class RecallMenu extends AugmentSettingsMenu
{
    private static final Component OPTIONS_LABEL = Component.translatable("backpacked.gui.options");
    private static final Component SET_SHELF_LABEL = Component.translatable("augment.backpacked.recall.link_shelf");
    private static final Component SET_SHELF_TOOLTIP = Component.translatable("augment.backpacked.recall.link_shelf.tooltip");
    private static final Component CHECKING_STATUS_LABEL = Component.translatable("augment.backpacked.recall.status.checking");
    private static final Component NOT_SET_LABEL = Component.translatable("augment.backpacked.recall.status.no_shelf_linked");
    private static final Component NO_SHELF_LABEL = Component.translatable("augment.backpacked.recall.status.shelf_missing");

    private static final int MIN_CONTENT_WIDTH = 110;

    public RecallMenu(PopupMenuHandler handler, AugmentHolder<RecallAugment> holder)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(OPTIONS_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());

            ShelfStatus status = layout.addChild(new ShelfStatus(MIN_CONTENT_WIDTH - 2, 16, holder), LayoutSettings::alignHorizontallyCenter);
            status.check();
            layout.addChild(BackpackButtons.builder()
                .setSize(MIN_CONTENT_WIDTH, 20)
                .setLabel(SET_SHELF_LABEL)
                .setTooltip(btn -> {
                    if(!btn.isActive()) {
                        return Tooltip.create(SET_SHELF_TOOLTIP);
                    }
                    return null;
                })
                .setTooltipDelay(0)
                .setDependent(() -> {
                    // This is checked server side too
                    Minecraft mc = Minecraft.getInstance();
                    if(mc.level == null || mc.player == null || !(mc.hitResult instanceof BlockHitResult result))
                        return false;
                    BlockPos pos = result.getBlockPos();
                    if(pos.distToCenterSqr(mc.player.getEyePosition()) > RecallAugment.UPDATE_SHELF_RANGE_SQR)
                        return false;
                    if(!(mc.level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf))
                        return false;
                    return !Objects.equals(holder.get().shelfKey().orElse(null), shelf.key());
                })
                .setAction(btn -> {
                    // This is checked server side too
                    Minecraft mc = Minecraft.getInstance();
                    if(mc.level != null && mc.hitResult instanceof BlockHitResult result) {
                        BlockPos pos = result.getBlockPos();
                        if(mc.level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf) {
                            if(!Objects.equals(holder.get().shelfKey().orElse(null), shelf.key())) {
                                holder.update(holder.get().setShelfKey(shelf.key()));
                                status.check();
                            }
                        }
                    }
                }).build());
            return layout;
        });
    }

    public static class ShelfStatus extends AbstractWidget
    {
        private static final WeakHashMap<Key, Consumer<Boolean>> PENDING = new WeakHashMap<>();

        private static final ResourceLocation SHELF_STATUS_DEFAULT_SPRITE = Utils.rl("backpack/shelf_status_default");
        private static final ResourceLocation SHELF_STATUS_INVALID_SPRITE = Utils.rl("backpack/shelf_status_invalid");
        private static final ResourceLocation SHELF_STATUS_VALID_SPRITE = Utils.rl("backpack/shelf_status_valid");

        private final Key key;
        private final AugmentHolder<RecallAugment> holder;
        private Status status = Status.NOT_SET;

        public ShelfStatus(int width, int height, AugmentHolder<RecallAugment> holder)
        {
            super(0, 0, width, height, CommonComponents.EMPTY);
            this.key = new Key(holder.backpackIndex(), holder.position());
            this.holder = holder;
        }

        private void check()
        {
            var optional = this.holder.get().shelfKey();
            optional.ifPresentOrElse(shelfKey -> {
                Network.getPlay().sendToServer(new MessageCheckShelfKey(this.holder.backpackIndex(), this.holder.position()));
                PENDING.put(this.key, valid -> {
                    this.status = valid ? Status.VALID : Status.INVALID;
                });
            }, () -> {
                this.status = Status.NOT_SET;
            });
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            graphics.blitSprite(this.getStatusSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            int textX = this.getX() + this.getWidth() / 2;
            int textY = this.getY() + (int) Math.ceil((this.getHeight() - 9) / 2.0);
            graphics.drawCenteredString(Minecraft.getInstance().font, this.getLabel(), textX, textY, 0xFFFFFFFF);
        }

        private ResourceLocation getStatusSprite()
        {
            return switch(this.status) {
                case NOT_SET -> SHELF_STATUS_DEFAULT_SPRITE;
                case INVALID -> SHELF_STATUS_INVALID_SPRITE;
                case VALID -> SHELF_STATUS_VALID_SPRITE;
            };
        }

        private Component getLabel()
        {
            if(PENDING.containsKey(this.key))
                return CHECKING_STATUS_LABEL;
            return switch(this.status) {
                case NOT_SET -> NOT_SET_LABEL;
                case INVALID -> NO_SHELF_LABEL;
                case VALID -> this.holder.get().shelfKey()
                    .map(k -> BlockPos.of(k.position()).toShortString())
                    .map(Component::literal)
                    .map(c -> (Component)c)
                    .orElse(NOT_SET_LABEL);
            };
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}

        public static void handle(int backpackIndex, Augments.Position position, boolean valid)
        {
            Key key = new Key(backpackIndex, position);
            Consumer<Boolean> consumer = PENDING.remove(key);
            if(consumer != null)
            {
                consumer.accept(valid);
            }
        }

        private enum Status
        {
            NOT_SET,
            INVALID,
            VALID
        }

        private record Key(int backpackIndex, Augments.Position position) {}
    }
}
