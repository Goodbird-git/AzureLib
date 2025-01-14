/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.common.internal.common.loading.json.raw;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mod.azure.azurelib.common.internal.common.util.JsonUtil;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Container class for cube information, only used in deserialization at startup
 */
public record Cube(
        @Nullable Double inflate,
        @Nullable Boolean mirror,
        double[] origin,
        double[] pivot,
        double[] rotation,
        double[] size,
        UVUnion uv
) {

    public static JsonDeserializer<Cube> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject obj = json.getAsJsonObject();
            Double inflate = JsonUtil.getOptionalDouble(obj, "inflate");
            Boolean mirror = JsonUtil.getOptionalBoolean(obj, "mirror");
            double[] origin = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "origin", null));
            double[] pivot = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "pivot", null));
            double[] rotation = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "rotation", null));
            double[] size = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "size", null));
            UVUnion uvUnion = GsonHelper.getAsObject(obj, "uv", null, context, UVUnion.class);

            return new Cube(inflate, mirror, origin, pivot, rotation, size, uvUnion);
        };
    }
}
