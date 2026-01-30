package com.mrcrayfish.backpacked.common.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.BackpackedCodecs;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.Set;

public record CraftedItemPredicate(Optional<Set<String>> modIds, Optional<TagKey<Item>> tag, Optional<HolderSet<Item>> items)
{
    public static final Codec<CraftedItemPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        BackpackedCodecs.STRING_SET.optionalFieldOf("namespace").forGetter(o -> o.modIds),
        TagKey.codec(Registries.ITEM).optionalFieldOf("tag").forGetter(CraftedItemPredicate::tag),
        BackpackedCodecs.ITEMS.optionalFieldOf("items").forGetter(CraftedItemPredicate::items)
    ).apply(builder, CraftedItemPredicate::new));

    public boolean test(ItemStack stack)
    {
        if(this.modIds.isPresent())
        {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if(!this.modIds.get().contains(key.getNamespace()))
            {
                return false;
            }
        }
        if(this.tag.isPresent() && !stack.is(this.tag.get()))
        {
            if(!stack.is(this.tag.get()))
            {
                return false;
            }
        }
        if(this.items.isPresent())
        {
            if(!this.items.get().contains(stack.getItemHolder()))
            {
                return false;
            }
        }
        return true;
    }
}
