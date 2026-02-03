package com.mrcrayfish.backpacked.common.predicates;

import com.google.gson.*;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;

public record BlockPositionPredicate(MinMaxBounds.Ints x, MinMaxBounds.Ints y, MinMaxBounds.Ints z)
{
    public boolean test(BlockPos pos)
    {
        return this.x.matches(pos.getX()) && this.y.matches(pos.getY()) && this.z.matches(pos.getZ());
    }

    public static BlockPositionPredicate fromJson(JsonElement element)
    {
        if(element.isJsonArray())
        {
            JsonArray array = element.getAsJsonArray();
            if(array.size() != 3)
            {
                throw new JsonSyntaxException("Expected 3 elements in array, got " + array.size());
            }

            MinMaxBounds.Ints[] values = new MinMaxBounds.Ints[3];
            for(int i = 0; i < 3; i++)
            {
                JsonElement e = array.get(i);
                if(!e.isJsonPrimitive())
                    throw new JsonSyntaxException("Expected primitive element, got " + e.getAsString());

                JsonPrimitive primitive = e.getAsJsonPrimitive();
                if(!primitive.isNumber())
                    throw new JsonSyntaxException("Expected number, got " + primitive.getAsString());

                values[i] = MinMaxBounds.Ints.exactly(primitive.getAsInt());
            }
            return new BlockPositionPredicate(values[0], values[1], values[2]);
        }
        if(element.isJsonObject())
        {
            JsonObject object = element.getAsJsonObject();
            MinMaxBounds.Ints x = MinMaxBounds.Ints.fromJson(object.get("x"));
            MinMaxBounds.Ints y = MinMaxBounds.Ints.fromJson(object.get("y"));
            MinMaxBounds.Ints z = MinMaxBounds.Ints.fromJson(object.get("z"));
            return new BlockPositionPredicate(x, y, z);
        }
        return new BlockPositionPredicate(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY);
    }
}
