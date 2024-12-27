package mod.azure.azurelib.animation.parse;

import com.google.gson.*;
import com.mojang.realmsclient.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.controller.keyframe.AzBoneAnimation;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyframe;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyframeStack;
import mod.azure.azurelib.animation.easing.AzEasingType;
import mod.azure.azurelib.animation.easing.AzEasingTypeLoader;
import mod.azure.azurelib.animation.easing.AzEasingTypes;
import mod.azure.azurelib.animation.primitive.AzBakedAnimation;
import mod.azure.azurelib.animation.primitive.AzBakedAnimations;
import mod.azure.azurelib.animation.primitive.AzKeyframes;
import mod.azure.azurelib.animation.primitive.AzLoopType;
import mod.azure.azurelib.util.JSONUtils;
import mod.azure.azurelib.util.JsonUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import mod.azure.azurelib.core.math.Constant;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangException;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.expressions.MolangValue;

/**
 * {@link com.google.gson.Gson} {@link JsonDeserializer} for {@link AzBakedAnimations}.<br>
 * Acts as the deserialization interface for {@code BakedAnimations}
 */
public class AzBakedAnimationsAdapter implements JsonDeserializer<AzBakedAnimations> {

    /**
     * Processes a given JSON element and transforms it into a list of pairs, where each pair consists of a string key
     * and a corresponding JSON element. Depending on the type of the input element, it handles primitive values,
     * arrays, and objects differently, ensuring a uniform output structure. For JSON primitives, a synthetic triplet
     * array is generated. For JSON arrays, the array is paired with the key "0". For JSON objects, individual entries
     * are processed recursively, with special handling for nested objects without a "vector" key.
     *
     * @param element The JSON element to be processed. It can be a {@link JsonPrimitive}, {@link JsonObject}, or
     *                {@link JsonArray}. If null, an empty list is returned.
     * @return A list of {@link Pair} objects where each pair contains a string key and a corresponding
     *         {@link JsonElement}. This list represents the processed structure of the input JSON element.
     * @throws JsonParseException If the provided JSON element is of an unsupported type or is invalid.
     */
    private static List<Pair<String, JsonElement>> getTripletObj(JsonElement element) {
        if (element == null)
            return Collections.emptyList();

        if (element instanceof JsonPrimitive) {
            JsonArray array = new JsonArray();

            array.add(element);
            array.add(element);
            array.add(element);

            element = array;
        }

        if (element instanceof JsonArray) {
            ObjectArrayList<Pair<String, JsonElement>> list = new ObjectArrayList<>();
            list.add(Pair.of("0", element)); // Add the pair to the list
            return list;
        }

        if (element instanceof JsonObject) {
            List<Pair<String, JsonElement>> list = new ObjectArrayList<>();

            for (Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet()) {
                if (entry.getValue() instanceof JsonObject && !((JsonObject) entry.getValue()).has("vector")) {
                    list.add(getTripletObjBedrock(entry.getKey(), ((JsonObject) entry.getValue())));

                    continue;
                }

                list.add(Pair.of(entry.getKey(), entry.getValue()));
            }

            return list;
        }

        throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + element);
    }

    /**
     * Extracts and processes keyframe data from a given JSON object, returning a pair consisting of a timestamp and
     * associated JSON element data. The method focuses on retrieving either the "pre" or "post" keyframe data from the
     * input JSON object, applying specific handling for array or object-based representations.
     *
     * @param timestamp The string representation of the timestamp for the keyframe data. If the input value is not
     *                  valid as a numeric string, it defaults to "0".
     * @param keyframe  A {@link JsonObject} containing the keyframe data. Expected keys include "pre" or "post" with
     *                  their associated values either as JSON arrays or nested objects containing a "vector" element.
     * @return A {@link Pair} where the first element is the processed timestamp as a string, and the second element is
     *         a {@link JsonArray} representing the keyframe values.
     * @throws JsonParseException If the provided keyframe data is invalid or does not meet the expected structure, such
     *                            as missing or incorrectly formatted "pre" or "post" keys.
     */
    private static Pair<String, JsonElement> getTripletObjBedrock(String timestamp, JsonObject keyframe) {
        JsonArray keyframeValues = null;

        if (keyframe.has("pre")) {
            JsonElement pre = keyframe.get("pre");
            keyframeValues = pre.isJsonArray()
                    ? pre.getAsJsonArray()
                    : JSONUtils.getJsonArray(pre.getAsJsonObject(), "vector");
        } else if (keyframe.has("post")) {
            JsonElement post = keyframe.get("post");
            keyframeValues = post.isJsonArray()
                    ? post.getAsJsonArray()
                    : JSONUtils.getJsonArray(post.getAsJsonObject(), "vector");
        }

        if (keyframeValues != null)
            return Pair.of(NumberUtils.isCreatable(timestamp) ? timestamp : "0", keyframeValues);

        throw new JsonParseException("Invalid keyframe data - expected array, found " + keyframe);
    }

    /**
     * Calculates the overall length of the animation timeline across all provided bone animations. The calculation
     * considers the maximum keyframe time for rotation, position, and scale transformations for each bone and
     * determines the longest timeline among them.
     *
     * @param boneAnimations An array of {@link AzBoneAnimation} instances representing the animations for individual
     *                       bones. Each bone animation includes keyframe stacks for rotation, position, and scale
     *                       transformations.
     * @return The maximum length of the animation timeline. If no keyframes are present, it defaults to
     *         {@link Double#MAX_VALUE}.
     */
    private static double calculateAnimationLength(AzBoneAnimation[] boneAnimations) {
        double length = 0;

        for (AzBoneAnimation animation : boneAnimations) {
            length = Math.max(length, animation.rotationKeyframes().getLastKeyframeTime());
            length = Math.max(length, animation.positionKeyframes().getLastKeyframeTime());
            length = Math.max(length, animation.scaleKeyframes().getLastKeyframeTime());
        }

        return length == 0 ? Double.MAX_VALUE : length;
    }

    /**
     * Deserializes JSON data into an instance of {@link AzBakedAnimations}.
     *
     * @param json    The JSON element to deserialize, expected to contain a valid structure for animations and optional
     *                includes.
     * @param type    The type of object to deserialize to; this is typically {@link AzBakedAnimations}.
     * @param context A context for handling nested deserialization, such as for custom types embedded within the JSON
     *                structure.
     * @return A newly created {@link AzBakedAnimations} instance containing parsed animations and includes as specified
     *         in the provided JSON data.
     * @throws JsonParseException If the JSON structure is invalid or an error occurs during deserialization.
     */
    @Override
    public AzBakedAnimations deserialize(
            JsonElement json,
            Type type,
            JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();

        JsonObject animationJsonList = jsonObj.getAsJsonObject("animations");
        JsonArray includeListJSONObj = jsonObj.getAsJsonArray("includes");
        Map<String, ResourceLocation> includes = null;
        if (includeListJSONObj != null) {
            List<JsonElement> jsonElements = new ArrayList<>();
            if (includeListJSONObj.isJsonArray()) {
                JsonArray jsonArray = includeListJSONObj.getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    jsonElements.add(element);
                }
            } else if (includeListJSONObj.isJsonObject()) {
                JsonObject jsonObject = includeListJSONObj.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    jsonElements.add(entry.getValue()); // Add just values if needed
                }
            }
            includes = new Object2ObjectOpenHashMap<>(includeListJSONObj.size());

            for (JsonElement entry : jsonElements) {
                JsonObject obj = entry.getAsJsonObject();
                ResourceLocation fileId = new ResourceLocation(obj.get("file_id").getAsString());
                for (JsonElement animName : obj.getAsJsonArray("animations")) {
                    String ani = animName.getAsString();
                    if (includes.containsKey(ani)) {
                        AzureLib.LOGGER.warn(
                                "Animation {} is already included! File already including: {}  File trying to include from again: {}",
                                ani,
                                includes.get(ani),
                                fileId
                        );
                    } else {
                        includes.put(ani, fileId);
                    }
                }
            }
        }

        Map<String, AzBakedAnimation> animations = new Object2ObjectOpenHashMap<>(animationJsonList.size());

        for (Map.Entry<String, JsonElement> entry : animationJsonList.entrySet()) {
            try {
                animations.put(
                        entry.getKey(),
                        bakeAnimation(entry.getKey(), entry.getValue().getAsJsonObject(), context)
                );
            } catch (MolangException ex) {
                AzureLib.LOGGER.error("Unable to parse animation: {}", entry.getKey());
            }
        }

        return new AzBakedAnimations(animations, includes);
    }

    /**
     * Processes the provided JSON data to create an instance of {@link AzBakedAnimation}. This method interprets the
     * animation JSON object, constructs the necessary data structures such as bone animations and keyframes, and
     * applies logic to calculate the animation length if not explicitly defined.
     *
     * @param name         The name of the animation being created.
     * @param animationObj The JSON object containing the animation definition. This object may include details such as
     *                     animation length, loop type, bones, and keyframe data.
     * @param context      The deserialization context used for nested data structures such as {@link AzKeyframes}.
     * @return A constructed {@link AzBakedAnimation} instance containing the parsed animation details.
     * @throws MolangException If an error occurs while processing expressions or any other aspect of the Molang
     *                         language during animation creation.
     */
    private AzBakedAnimation bakeAnimation(
            String name,
            JsonObject animationObj,
            JsonDeserializationContext context
    ) throws MolangException {
        double length = animationObj.has("animation_length")
                ? JSONUtils.getAsDouble(animationObj, "animation_length") * 20d
                : -1;
        AzLoopType loopType = AzLoopType.fromJson(animationObj.get("loop"));
        AzBoneAnimation[] boneAnimations = bakeBoneAnimations(
                JSONUtils.getJsonObject(animationObj, "bones", new JsonObject())
        );
        AzKeyframes keyframes = context.deserialize(animationObj, AzKeyframes.class);

        if (length == -1)
            length = calculateAnimationLength(boneAnimations);

        return new AzBakedAnimation(name, length, loopType, boneAnimations, keyframes);
    }

    /**
     * Processes a JSON object representing bone animations and constructs an array of {@link AzBoneAnimation} instances.
     * Each bone's animation includes keyframe stacks for position, rotation, and scale transformations.
     *
     * @param bonesObj The JSON object containing bone animation data, where each key is the bone name and the value is
     *                 an object with keyframe data for scale, position, and rotation.
     * @return An array of {@link AzBoneAnimation} instances representing the deserialized animations for each bone.
     * @throws MolangException If an error occurs during the processing of keyframes or Molang expressions.
     */
    private AzBoneAnimation[] bakeBoneAnimations(JsonObject bonesObj) throws MolangException {
        AzBoneAnimation[] animations = new AzBoneAnimation[bonesObj.size()];
        int index = 0;

        for (Map.Entry<String, JsonElement> entry : bonesObj.entrySet()) {
            JsonObject entryObj = entry.getValue().getAsJsonObject();
            AzKeyframeStack<AzKeyframe<IValue>> scaleFrames = buildKeyframeStack(
                    getTripletObj(entryObj.get("scale")),
                    false
            );
            AzKeyframeStack<AzKeyframe<IValue>> positionFrames = buildKeyframeStack(
                    getTripletObj(entryObj.get("position")),
                    false
            );
            AzKeyframeStack<AzKeyframe<IValue>> rotationFrames = buildKeyframeStack(
                    getTripletObj(entryObj.get("rotation")),
                    true
            );

            animations[index] = new AzBoneAnimation(entry.getKey(), rotationFrames, positionFrames, scaleFrames);
            index++;
        }

        return animations;
    }

    /**
     * Builds a {@link AzKeyframeStack} containing keyframes for X, Y, and Z-axis transformations based on the provided
     * animation data. The method processes a list of paired time-stamped keyframe data, interprets the JSON structures,
     * applies appropriate transformations for rotations (if specified), and generates keyframes with defined easing
     * behaviors.
     *
     * @param entries       A list of {@link Pair} objects containing the timestamp as a {@link String} and associated
     *                      {@link JsonElement} data describing the keyframe. Each entry represents a point in time
     *                      within the animation timeline.
     * @param isForRotation A boolean indicating whether the keyframe transformations should account for rotation. If
     *                      true, the keyframe values undergo additional processing to convert angles to radians.
     * @return A {@link AzKeyframeStack} containing three lists of keyframes for X, Y, and Z transformations,
     *         respectively.
     * @throws MolangException If an error occurs during the parsing or interpretation of Molang expressions in the
     *                         keyframe data.
     */
    private AzKeyframeStack<AzKeyframe<IValue>> buildKeyframeStack(
            List<Pair<String, JsonElement>> entries,
            boolean isForRotation
    ) throws MolangException {
        if (entries.isEmpty())
            return new AzKeyframeStack<>();

        List<AzKeyframe<IValue>> xFrames = new ObjectArrayList<>();
        List<AzKeyframe<IValue>> yFrames = new ObjectArrayList<>();
        List<AzKeyframe<IValue>> zFrames = new ObjectArrayList<>();

        IValue xPrev = null;
        IValue yPrev = null;
        IValue zPrev = null;
        Pair<String, JsonElement> prevEntry = null;

        for (Pair<String, JsonElement> entry : entries) {
            String key = entry.first();
            JsonElement element = entry.second();

            if (key.equals("easing") || key.equals("easingArgs") || key.equals("lerp_mode"))
                continue;

            double prevTime = prevEntry != null ? Double.parseDouble(prevEntry.first()) : 0;
            double curTime = NumberUtils.isCreatable(key) ? Double.parseDouble(entry.first()) : 0;
            double timeDelta = curTime - prevTime;

            JsonArray keyframeVector = element instanceof JsonArray
                    ? ((JsonArray) element)
                    : JSONUtils.getJsonArray(element.getAsJsonObject(), "vector");
            MolangValue rawXValue = MolangParser.parseJson(keyframeVector.get(0));
            MolangValue rawYValue = MolangParser.parseJson(keyframeVector.get(1));
            MolangValue rawZValue = MolangParser.parseJson(keyframeVector.get(2));
            IValue xValue = isForRotation && rawXValue.isConstant()
                    ? new Constant(Math.toRadians(-rawXValue.get()))
                    : rawXValue;
            IValue yValue = isForRotation && rawYValue.isConstant()
                    ? new Constant(Math.toRadians(-rawYValue.get()))
                    : rawYValue;
            IValue zValue = isForRotation && rawZValue.isConstant()
                    ? new Constant(Math.toRadians(rawZValue.get()))
                    : rawZValue;

            JsonObject entryObj = element instanceof JsonObject ? ((JsonObject) element) : null;
            AzEasingType easingType = entryObj != null && entryObj.has("easing")
                    ? AzEasingTypeLoader.fromJson(entryObj.get("easing"))
                    : AzEasingTypes.LINEAR;
            List<IValue> easingArgs = entryObj != null && entryObj.has("easingArgs")
                    ? JsonUtil.jsonArrayToList(
                    JSONUtils.getJsonArray(entryObj, "easingArgs"),
                    ele -> new Constant(ele.getAsDouble())
            )
                    : new ObjectArrayList<>();

            xFrames.add(
                    new AzKeyframe<>(timeDelta * 20, prevEntry == null ? xValue : xPrev, xValue, easingType, easingArgs)
            );
            yFrames.add(
                    new AzKeyframe<>(timeDelta * 20, prevEntry == null ? yValue : yPrev, yValue, easingType, easingArgs)
            );
            zFrames.add(
                    new AzKeyframe<>(timeDelta * 20, prevEntry == null ? zValue : zPrev, zValue, easingType, easingArgs)
            );

            xPrev = xValue;
            yPrev = yValue;
            zPrev = zValue;
            prevEntry = entry;
        }

        return new AzKeyframeStack<>(xFrames, yFrames, zFrames);
    }
}
