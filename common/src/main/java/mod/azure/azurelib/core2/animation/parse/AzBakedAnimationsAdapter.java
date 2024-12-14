package mod.azure.azurelib.core2.animation.parse;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.util.JsonUtil;
import mod.azure.azurelib.core.math.Constant;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangException;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core2.animation.AzBoneAnimation;
import mod.azure.azurelib.core2.animation.AzEasingType;
import mod.azure.azurelib.core2.animation.AzKeyframe;
import mod.azure.azurelib.core2.animation.AzKeyframeStack;
import mod.azure.azurelib.core2.animation.primitive.AzAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzBakedAnimations;
import mod.azure.azurelib.core2.animation.primitive.AzKeyframes;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;

/**
 * {@link com.google.gson.Gson} {@link JsonDeserializer} for {@link AzBakedAnimations}.<br>
 * Acts as the deserialization interface for {@code BakedAnimations}
 */
public class AzBakedAnimationsAdapter implements JsonDeserializer<AzBakedAnimations> {

    private static List<Pair<String, JsonElement>> getTripletObj(JsonElement element) {
        if (element == null) {
            return List.of();
        }

        if (element instanceof JsonPrimitive primitive) {
            var array = new JsonArray(3);

            array.add(primitive);
            array.add(primitive);
            array.add(primitive);

            element = array;
        }

        if (element instanceof JsonArray array)
            return ObjectArrayList.of(Pair.of("0", array));

        if (element instanceof JsonObject obj) {
            var list = new ObjectArrayList<Pair<String, JsonElement>>();

            for (var entry : obj.entrySet()) {
                if (entry.getValue() instanceof JsonObject entryObj && !entryObj.has("vector")) {
                    list.add(getTripletObjBedrock(entry.getKey(), entryObj));
                    continue;
                }

                list.add(Pair.of(entry.getKey(), entry.getValue()));
            }

            return list;
        }

        throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + element);
    }

    private static Pair<String, JsonElement> getTripletObjBedrock(String timestamp, JsonObject keyframe) {
        JsonArray keyframeValues = null;

        if (keyframe.has("pre")) {
            var pre = keyframe.get("pre");
            keyframeValues = pre.isJsonArray()
                ? pre.getAsJsonArray()
                : GsonHelper.getAsJsonArray(pre.getAsJsonObject(), "vector");
        } else if (keyframe.has("post")) {
            var post = keyframe.get("post");
            keyframeValues = post.isJsonArray()
                ? post.getAsJsonArray()
                : GsonHelper.getAsJsonArray(post.getAsJsonObject(), "vector");
        }

        if (keyframeValues != null) {
            return Pair.of(NumberUtils.isCreatable(timestamp) ? timestamp : "0", keyframeValues);
        }

        throw new JsonParseException("Invalid keyframe data - expected array, found " + keyframe);
    }

    private static double calculateAnimationLength(AzBoneAnimation[] boneAnimations) {
        var length = 0.0;

        for (var animation : boneAnimations) {
            length = Math.max(length, animation.rotationKeyFrames().getLastKeyframeTime());
            length = Math.max(length, animation.positionKeyFrames().getLastKeyframeTime());
            length = Math.max(length, animation.scaleKeyFrames().getLastKeyframeTime());
        }

        return length == 0 ? Double.MAX_VALUE : length;
    }

    @Override
    public AzBakedAnimations deserialize(
        JsonElement json,
        Type type,
        JsonDeserializationContext context
    ) throws JsonParseException {
        var jsonObj = json.getAsJsonObject();

        var animationJsonList = jsonObj.getAsJsonObject("animations");
        var includeListJSONObj = jsonObj.getAsJsonArray("includes");
        Map<String, ResourceLocation> includes = null;

        if (includeListJSONObj != null) {
            includes = new Object2ObjectOpenHashMap<>(includeListJSONObj.size());

            for (var entry : includeListJSONObj.asList()) {
                var obj = entry.getAsJsonObject();
                var fileId = ResourceLocation.parse(obj.get("file_id").getAsString());

                for (var animName : obj.getAsJsonArray("animations")) {
                    var ani = animName.getAsString();

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

        var animations = new Object2ObjectOpenHashMap<String, AzAnimation>(animationJsonList.size());

        for (var entry : animationJsonList.entrySet()) {
            try {
                animations.put(
                    entry.getKey(),
                    bakeAnimation(entry.getKey(), entry.getValue().getAsJsonObject(), context)
                );
            } catch (MolangException ex) {
                AzureLib.LOGGER.error("Unable to parse animation: {}", entry.getKey());
                ex.printStackTrace();
            }
        }

        return new AzBakedAnimations(animations, includes);
    }

    private AzAnimation bakeAnimation(
        String name,
        JsonObject animationObj,
        JsonDeserializationContext context
    ) throws MolangException {
        var length = animationObj.has("animation_length")
            ? GsonHelper.getAsDouble(animationObj, "animation_length") * 20d
            : -1.0;
        var loopType = AzLoopType.fromJson(animationObj.get("loop"));
        var boneAnimations = bakeBoneAnimations(
            GsonHelper.getAsJsonObject(animationObj, "bones", new JsonObject())
        );
        var keyframes = (AzKeyframes) context.deserialize(animationObj, AzKeyframes.class);

        if (length == -1) {
            length = calculateAnimationLength(boneAnimations);
        }

        return new AzAnimation(name, length, loopType, boneAnimations, keyframes);
    }

    private AzBoneAnimation[] bakeBoneAnimations(JsonObject bonesObj) throws MolangException {
        var animations = new AzBoneAnimation[bonesObj.size()];
        var index = 0;

        for (var entry : bonesObj.entrySet()) {
            var entryObj = entry.getValue().getAsJsonObject();
            var scaleFrames = buildKeyframeStack(
                getTripletObj(entryObj.get("scale")),
                false
            );
            var positionFrames = buildKeyframeStack(
                getTripletObj(entryObj.get("position")),
                false
            );
            var rotationFrames = buildKeyframeStack(
                getTripletObj(entryObj.get("rotation")),
                true
            );

            animations[index] = new AzBoneAnimation(entry.getKey(), rotationFrames, positionFrames, scaleFrames);
            index++;
        }

        return animations;
    }

    private AzKeyframeStack<AzKeyframe<IValue>> buildKeyframeStack(
        List<Pair<String, JsonElement>> entries,
        boolean isForRotation
    ) throws MolangException {
        if (entries.isEmpty()) {
            return new AzKeyframeStack<>();
        }

        var xFrames = new ObjectArrayList<AzKeyframe<IValue>>();
        var yFrames = new ObjectArrayList<AzKeyframe<IValue>>();
        var zFrames = new ObjectArrayList<AzKeyframe<IValue>>();

        IValue xPrev = null;
        IValue yPrev = null;
        IValue zPrev = null;
        Pair<String, JsonElement> prevEntry = null;

        for (var entry : entries) {
            var key = entry.getFirst();
            var element = entry.getSecond();

            if (key.equals("easing") || key.equals("easingArgs") || key.equals("lerp_mode")) {
                continue;
            }

            double prevTime = prevEntry != null ? Double.parseDouble(prevEntry.getFirst()) : 0;
            double curTime = NumberUtils.isCreatable(key) ? Double.parseDouble(entry.getFirst()) : 0;
            double timeDelta = curTime - prevTime;

            var keyFrameVector = element instanceof JsonArray array
                ? array
                : GsonHelper.getAsJsonArray(element.getAsJsonObject(), "vector");
            var rawXValue = MolangParser.parseJson(keyFrameVector.get(0));
            var rawYValue = MolangParser.parseJson(keyFrameVector.get(1));
            var rawZValue = MolangParser.parseJson(keyFrameVector.get(2));
            var xValue = isForRotation && rawXValue.isConstant()
                ? new Constant(Math.toRadians(-rawXValue.get()))
                : rawXValue;
            var yValue = isForRotation && rawYValue.isConstant()
                ? new Constant(Math.toRadians(-rawYValue.get()))
                : rawYValue;
            var zValue = isForRotation && rawZValue.isConstant()
                ? new Constant(Math.toRadians(rawZValue.get()))
                : rawZValue;

            var entryObj = element instanceof JsonObject obj ? obj : null;
            var easingType = entryObj != null && entryObj.has("easing")
                ? AzEasingType.fromJson(entryObj.get("easing"))
                : AzEasingType.LINEAR;
            var easingArgs = entryObj != null && entryObj.has("easingArgs")
                ? JsonUtil.<IValue>jsonArrayToList(
                    GsonHelper.getAsJsonArray(entryObj, "easingArgs"),
                    ele -> new Constant(ele.getAsDouble())
                )
                : new ObjectArrayList<IValue>();

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
