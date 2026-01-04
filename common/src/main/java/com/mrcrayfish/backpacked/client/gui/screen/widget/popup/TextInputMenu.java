package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.BackpackButtons;
import com.mrcrayfish.backpacked.client.gui.screen.widget.Divider;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TextInputMenu extends PopupMenu
{
    private static final int WIDTH = 160;

    private final PaddedLinearLayout layout = (PaddedLinearLayout) PaddedLinearLayout.vertical().padding(8).spacing(2);

    public TextInputMenu(PopupMenuHandler handler, String initialInput, int maxLength, Consumer<String> onSave)
    {
        super(handler);
        this.setAlignment(Alignment.CENTERED);
        this.setBackground(Utils.rl("augment/menu_background"));
        TitleWidget title = new TitleWidget(Component.literal("Rename"), Minecraft.getInstance().font);
        title.setWidth(WIDTH);
        this.layout.addChild(title);
        this.layout.addChild(Divider.horizontal(WIDTH).colour(0xFFE0CDB7));

        FrameworkEditBox editBox = this.layout.addChild(FrameworkEditBox.builder()
            .setSize(WIDTH, 16)
            .setInitialText(initialInput)
            .setMaxTextLength(maxLength)
            .setBackground(new WidgetSprites(
                Utils.rl("backpack/editbox/background"),
                Utils.rl("backpack/editbox/background_focused")
            )).build()
        );
        this.layout.addChild(BackpackButtons.builder()
            .setSize(WIDTH / 3, 18)
            .setLabel(Component.literal("Save"))
            .setAction(btn -> {
                onSave.accept(editBox.getText());
            }).build(), LayoutSettings::alignHorizontallyRight);
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }
}
