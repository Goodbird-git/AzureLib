/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.common.internal.common.util;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import mod.azure.azurelib.common.internal.common.loading.json.raw.*;
import mod.azure.azurelib.common.internal.common.loading.json.typeadapter.BakedAnimationsAdapter;
import mod.azure.azurelib.common.internal.common.loading.json.typeadapter.KeyFramesAdapter;
import mod.azure.azurelib.common.internal.common.loading.object.BakedAnimations;
import mod.azure.azurelib.core.animation.Animation;
import mod.azure.azurelib.core2.animation.parse.AzBakedAnimationsAdapter;
import mod.azure.azurelib.core2.animation.parse.AzKeyframesAdapter;
import mod.azure.azurelib.core2.animation.primitive.AzBakedAnimations;
import mod.azure.azurelib.core2.animation.primitive.AzKeyframes;

/**
 * Json helper class for various json functions
 */
public record JsonUtil() {

    public static final Gson GEO_GSON = new GsonBuilder().setLenient()
        .registerTypeAdapter(Bone.class, Bone.deserializer())
        .registerTypeAdapter(Cube.class, Cube.deserializer())
        .registerTypeAdapter(FaceUV.class, FaceUV.deserializer())
        .registerTypeAdapter(LocatorClass.class, LocatorClass.deserializer())
        .registerTypeAdapter(LocatorValue.class, LocatorValue.deserializer())
        .registerTypeAdapter(MinecraftGeometry.class, MinecraftGeometry.deserializer())
        .registerTypeAdapter(Model.class, Model.deserializer())
        .registerTypeAdapter(ModelProperties.class, ModelProperties.deserializer())
        .registerTypeAdapter(PolyMesh.class, PolyMesh.deserializer())
        .registerTypeAdapter(PolysUnion.class, PolysUnion.deserializer())
        .registerTypeAdapter(TextureMesh.class, TextureMesh.deserializer())
        .registerTypeAdapter(UVFaces.class, UVFaces.deserializer())
        .registerTypeAdapter(UVUnion.class, UVUnion.deserializer())
        // TODO: Remove
        .registerTypeAdapter(Animation.Keyframes.class, new KeyFramesAdapter())
        // TODO: Remove
        .registerTypeAdapter(BakedAnimations.class, new BakedAnimationsAdapter())
        .registerTypeAdapter(AzKeyframes.class, new AzKeyframesAdapter())
        .registerTypeAdapter(AzBakedAnimations.class, new AzBakedAnimationsAdapter())
        .create();

    /**
     * Convert a {@link JsonArray} of doubles to a {@code double[]}.<br>
     * No type checking is done, so if the array contains anything other than doubles, this will throw an exception.<br>
     * Ensures a minimum size of 3, as this is the expected usage of this method
     */
    public static double[] jsonArrayToDoubleArray(@Nullable JsonArray array) throws JsonParseException {
        if (array == null)
            return new double[3];

        double[] output = new double[array.size()];

        for (int i = 0; i < array.size(); i++) {
            output[i] = array.get(i).getAsDouble();
        }

        return output;
    }

    /**
     * Converts a {@link JsonArray} of a given object type to an array of that object, deserialized from their
     * respective {@link JsonElement JsonElements}
     *
     * @param array       The array containing the objects to be converted
     * @param context     The {@link com.google.gson.Gson} context for deserialization
     * @param objectClass The object type that the array contains
     */
    public static <T> T[] jsonArrayToObjectArray(
        JsonArray array,
        JsonDeserializationContext context,
        Class<T> objectClass
    ) {
        T[] objArray = (T[]) Array.newInstance(objectClass, array.size());

        for (int i = 0; i < array.size(); i++) {
            objArray[i] = context.deserialize(array.get(i), objectClass);
        }

        return objArray;
    }

    /**
     * Converts a {@link JsonArray} to a {@link List} of elements of a pre-determined type.
     *
     * @param array              The {@code JsonArray} to convert
     * @param elementTransformer Transformation function that converts a {@link JsonElement} to the intended output
     *                           object
     */
    public static <T> List<T> jsonArrayToList(@Nullable JsonArray array, Function<JsonElement, T> elementTransformer) {
        if (array == null)
            return new ObjectArrayList<>();

        List<T> list = new ObjectArrayList<>(array.size());

        for (JsonElement element : array) {
            list.add(elementTransformer.apply(element));
        }

        return list;
    }

    /**
     * Converts a {@link JsonObject} to a {@link Map} of String keys to their respective objects
     *
     * @param obj        The base {@code JsonObject} to convert
     * @param context    The {@link Gson} deserialization context
     * @param objectType The object class that the map should contain
     */
    public static <T> Map<String, T> jsonObjToMap(
        JsonObject obj,
        JsonDeserializationContext context,
        Class<T> objectType
    ) {
        Map<String, T> map = new Object2ObjectOpenHashMap<>(obj.size());

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            map.put(entry.getKey(), context.deserialize(entry.getValue(), objectType));
        }

        return map;
    }

    /**
     * Retrieves an optionally present Long from the provided {@link JsonObject}, or null if the element isn't present
     */
    @Nullable
    public static Long getOptionalLong(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsLong(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Boolean from the provided {@link JsonObject}, or null if the element isn't
     * present
     */
    @Nullable
    public static Boolean getOptionalBoolean(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsBoolean(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Float from the provided {@link JsonObject}, or null if the element isn't present
     */
    @Nullable
    public static Float getOptionalFloat(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsFloat(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Double from the provided {@link JsonObject}, or null if the element isn't present
     */
    @Nullable
    public static Double getOptionalDouble(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsDouble(obj, elementName) : null;
    }

    /**
     * Retrieves an optionally present Integer from the provided {@link JsonObject}, or null if the element isn't
     * present
     */
    @Nullable
    public static Integer getOptionalInteger(JsonObject obj, String elementName) {
        return obj.has(elementName) ? GsonHelper.getAsInt(obj, elementName) : null;
    }
}
