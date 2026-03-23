package com.mrcrayfish.backpacked.client.augment.menu;

import com.mrcrayfish.backpacked.client.augment.AugmentHolder;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.impl.ImmortalAugment;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.text.DecimalFormat;

public class ImmortalMenu extends AugmentSettingsMenu
{
    private static final Component COOLDOWN_LABEL = Component.translatable("augment.backpacked.immortal.cooldown");

    private static final int MIN_CONTENT_WIDTH = 90;

    public ImmortalMenu(PopupMenuHandler handler, AugmentHolder<ImmortalAugment> holder)
    {
        super(handler, menu -> {
            LinearLayout layout = LinearLayout.vertical().spacing(2);
            TitleWidget title = layout.addChild(new TitleWidget(COOLDOWN_LABEL, Minecraft.getInstance().font));
            Divider divider = layout.addChild(Divider.horizontal(Math.max(MIN_CONTENT_WIDTH, title.getWidth())).colour(0xFFE0CDB7));
            title.setWidth(divider.getWidth());
            layout.addChild(new CooldownStatus(divider.getWidth(), 16));
            return layout;
        });
    }

    public static class CooldownStatus extends AbstractWidget
    {
        private static final Component READY_LABEL = Component.translatable("augment.backpacked.immortal.ready");
        private static final Identifier ON_COOLDOWN_SPRITE = Utils.id("backpack/immortal_on_cooldown");
        private static final Identifier READY_SPRITE = Utils.id("backpack/immortal_ready");
        private static final DecimalFormat FORMAT = new DecimalFormat("0.0s");

        public CooldownStatus(int width, int height)
        {
            super(0, 0, width, height, CommonComponents.EMPTY);
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick)
        {
            int cooldown = this.getCooldown();
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, this.getStatusSprite(cooldown), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            int textX = this.getX() + this.getWidth() / 2;
            int textY = this.getY() + (int) Math.ceil((this.getHeight() - 9) / 2.0);
            extractor.centeredText(Minecraft.getInstance().font, this.getLabel(cooldown), textX, textY, 0xFFFFFFFF);
        }

        private Identifier getStatusSprite(int cooldown)
        {
            return cooldown > 0 ? ON_COOLDOWN_SPRITE : READY_SPRITE;
        }

        private Component getLabel(int cooldown)
        {
            if(cooldown > 0)
            {
                float seconds = cooldown / 20F;
                return Component.translatable("augment.backpacked.immortal.wait", FORMAT.format(seconds));
            }
            return READY_LABEL;
        }

        private int getCooldown()
        {
            Minecraft mc = Minecraft.getInstance();
            return mc.player != null ? ModSyncedDataKeys.IMMORTAL_COOLDOWN.getValue(mc.player) : 0;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}
    }
}
