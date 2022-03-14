package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Panda;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class BambooBasketBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "bamboo_basket");

    public BambooBasketBackpack()
    {
        super(ID);
    }

    @Override
    public Supplier<Object> getModelSupplier()
    {
        return ClientHandler.getModelInstances()::getBambooBasketModel;
    }

    @Override
    @Nullable
    protected IProgressTracker createProgressTracker()
    {
        return new ProgressTracker();
    }

    public static class ProgressTracker implements IProgressTracker
    {
        private static final int COUNT = 10;
        private final Set<UUID> fedPandas = new HashSet<>();

        public void addPanda(Panda panda, ServerPlayer player)
        {
            this.fedPandas.add(panda.getUUID());
            this.markForCompletionTest(player);
        }

        @Override
        public boolean isComplete()
        {
            return this.fedPandas.size() >= COUNT;
        }

        @Override
        public void read(CompoundTag tag)
        {
            this.fedPandas.clear();
            ListTag list = tag.getList("FedPandas", Tag.TAG_COMPOUND);
            list.forEach(t -> {
                CompoundTag uuidTag = (CompoundTag) t;
                long mostBits = uuidTag.getLong("Most");
                long leastBits = uuidTag.getLong("Least");
                this.fedPandas.add(new UUID(mostBits, leastBits));
            });
        }

        @Override
        public void write(CompoundTag tag)
        {
            ListTag list = new ListTag();
            this.fedPandas.forEach(uuid -> {
                CompoundTag uuidTag = new CompoundTag();
                uuidTag.putLong("Most", uuid.getMostSignificantBits());
                uuidTag.putLong("Least", uuid.getLeastSignificantBits());
                list.add(uuidTag);
            });
            tag.put("FedPandas", list);
        }

        @Override
        public Component getDisplayComponent()
        {
            return ProgressFormatters.FED_X_OF_X.apply(this.fedPandas.size(), COUNT);
        }
    }
}
