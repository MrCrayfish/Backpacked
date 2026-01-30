package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mrcrayfish.backpacked.client.TextureDefinitions;
import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedGridLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import com.mrcrayfish.framework.api.client.screen.widget.texture.WidgetTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TextInputMenu extends PopupMenu // TODO DONE
{
    private static final int WIDTH = 160;

    private final PaddedGridLayout layout = (PaddedGridLayout) new PaddedGridLayout().padding(8).spacing(2);
    private final GridLayout.RowHelper helper = this.layout.createRowHelper(1);

    public TextInputMenu(PopupMenuHandler handler, String initialInput, int maxLength, Consumer<String> onSave)
    {
        super(handler);
        this.setAlignment(Alignment.CENTERED);
        this.setBackground(TextureDefinitions.AUGMENT_MENU_BACKGROUND);
        TitleWidget title = new TitleWidget(Component.translatable("backpacked.gui.rename"), Minecraft.getInstance().font);
        title.setWidth(WIDTH);
        this.helper.addChild(title);
        this.helper.addChild(Divider.horizontal(WIDTH).colour(0xFFE0CDB7));

        FrameworkEditBox editBox = this.helper.addChild(FrameworkEditBox.builder()
            .setSize(WIDTH, 16)
            .setInitialText(initialInput)
            .setMaxTextLength(maxLength)
            .setBackground(new WidgetTextures(
                TextureDefinitions.EDIT_BOX_ENABLED,
                TextureDefinitions.EDIT_BOX_ENABLED_HOVERED
            )).build()
        );
        this.helper.addChild(BackpackButtons.builder()
            .setSize(WIDTH / 3, 18)
            .setLabel(Component.translatable("backpacked.button.save"))
            .setAction(btn -> {
                onSave.accept(editBox.getText());
            }).build(), LayoutSettings.defaults().alignHorizontallyRight());
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }
}
