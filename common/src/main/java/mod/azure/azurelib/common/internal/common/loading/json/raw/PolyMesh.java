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
 * Container class for poly mesh information, only used in deserialization at startup
 */
public record PolyMesh(
        @Nullable Boolean normalizedUVs,
        double[] normals,
        @Nullable PolysUnion polysUnion,
        double[] positions,
        double[] uvs
) {

    public static JsonDeserializer<PolyMesh> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject obj = json.getAsJsonObject();
            Boolean normalizedUVs = JsonUtil.getOptionalBoolean(obj, "normalized_uvs");
            double[] normals = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "normals", null));
            PolysUnion polysUnion = GsonHelper.getAsObject(obj, "polys", null, context, PolysUnion.class);
            double[] positions = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "positions", null));
            double[] uvs = JsonUtil.jsonArrayToDoubleArray(GsonHelper.getAsJsonArray(obj, "uvs", null));

            return new PolyMesh(normalizedUVs, normals, polysUnion, positions, uvs);
        };
    }
}
