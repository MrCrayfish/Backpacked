package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.common.backpack.ModelProperty;
import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.util.ItemStackHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public record MessageUpdateBackpack(int entityId, ItemStack backpack, boolean fullTag)
{
    public static void encode(MessageUpdateBackpack message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityId);
        writeBackpack(buffer, message.backpack, message.fullTag);
    }

    public static MessageUpdateBackpack decode(FriendlyByteBuf buffer)
    {
        return new MessageUpdateBackpack(buffer.readInt(), buffer.readItem(), false);
    }

    public static void handle(MessageUpdateBackpack message, MessageContext context)
    {
        context.execute(() -> ClientPlayHandler.handleUpdateBackpack(message));
        context.setHandled(true);
    }

    private static void writeBackpack(FriendlyByteBuf buffer, ItemStack stack, boolean fullTag)
    {
        if(!stack.isEmpty())
        {
            buffer.writeBoolean(true);
            buffer.writeVarInt(Item.getId(stack.getItem()));
            buffer.writeByte(stack.getCount());
            buffer.writeNbt(getBackpackTag(stack, fullTag));
            return;
        }
        buffer.writeBoolean(false);
    }

    @Nullable
    private static CompoundTag getBackpackTag(ItemStack stack, boolean fullTag)
    {
        Item item = stack.getItem();
        if(!ItemStackHelper.isDamageable(stack) && !item.shouldOverrideMultiplayerNbt())
            return null;

        CompoundTag realTag = stack.getOrCreateTag();
        if(fullTag)
            return realTag;

        CompoundTag tag = new CompoundTag();
        tag.putString("BackpackModel", realTag.getString("BackpackModel"));
        for(ModelProperty property : ModelProperty.values())
        {
            String tagName = property.getTagName();
            boolean value = realTag.contains(tagName, Tag.TAG_BYTE) ? realTag.getBoolean(tagName) : property.getDefaultValue();
            tag.putBoolean(tagName, value);
        }
        tag.put("Enchantments", stack.getEnchantmentTags());
        tag.put("display", stack.getOrCreateTagElement("display"));
        return tag;
    }
}
