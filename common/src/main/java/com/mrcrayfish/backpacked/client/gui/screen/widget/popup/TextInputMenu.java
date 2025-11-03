package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomEditBox;
import com.mrcrayfish.backpacked.client.gui.screen.widget.TitleWidget;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageRenameBackpack;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

public class TextInputMenu extends PopupMenu
{
    private static final int WIDTH = 160;

    private final PaddedLinearLayout layout = (PaddedLinearLayout) PaddedLinearLayout.vertical().padding(8).spacing(2);

    public TextInputMenu(PopupMenuHandler handler, String initialInput)
    {
        super(handler);
        this.setAlignment(Alignment.CENTERED);
        this.setBackground(Utils.rl("augment/menu_background"));
        TitleWidget title = new TitleWidget(Component.literal("Rename"), Minecraft.getInstance().font);
        title.setWidth(WIDTH);
        this.layout.addChild(title);
        EditBox editBox = this.layout.addChild(CustomEditBox.create(WIDTH, 16, null, new WidgetSprites(
            Utils.rl("backpack/editbox/background"),
            Utils.rl("backpack/editbox/background_focused")
        ))).getEditBox();
        editBox.setValue(ChatFormatting.stripFormatting(initialInput));
        editBox.setMaxLength(50);
        this.layout.addChild(CustomButton.builder()
            .setSize(WIDTH / 3, 16)
            .setMessage(Component.literal("Save"))
            .setAction(btn -> {
                String value = StringUtils.truncate(editBox.getValue(), 50);
                Network.PLAY.sendToServer(new MessageRenameBackpack(value));
            }).build(), LayoutSettings::alignHorizontallyRight);
    }

    @Override
    protected Layout layout()
    {
        return this.layout;
    }
}
