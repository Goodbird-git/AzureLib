/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.animation.parse;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.animation.primitive.AzKeyframes;
import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.SoundKeyframeData;
import mod.azure.azurelib.util.JSONUtils;
import mod.azure.azurelib.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * {@link Gson} {@link JsonDeserializer} for {@link AzKeyframes}.<br>
 * Acts as the deserialization interface for {@code Keyframes}
 */
public class AzKeyFramesAdapter implements JsonDeserializer<AzKeyframes> {

    /**
     * Builds an array of {@link SoundKeyframeData} objects from a given JSON object. This method parses a JSON object
     * containing sound effect data, extracting key-value pairs to create instances of {@link SoundKeyframeData}. The
     * keys represent the time in seconds, which are converted into ticks, and the values specify the sound effect.
     *
     * @param rootObj the root JSON object containing the "sound_effects" data
     * @return an array of {@link SoundKeyframeData} objects extracted from the JSON object
     */
    private static SoundKeyframeData[] buildSoundFrameData(JsonObject rootObj) {
        JsonObject soundsObj = JSONUtils.getJsonObject(rootObj, "sound_effects", new JsonObject());
        SoundKeyframeData[] sounds = new SoundKeyframeData[soundsObj.size()];
        int index = 0;

        for (Map.Entry<String, JsonElement> entry : soundsObj.entrySet()) {
            sounds[index] = new SoundKeyframeData(
                Double.parseDouble(entry.getKey()) * 20d,
                    JSONUtils.getString(entry.getValue().getAsJsonObject(), "effect")
            );
            index++;
        }

        return sounds;
    }

    /**
     * Builds an array of {@link ParticleKeyframeData} objects from a given JSON object. This method parses a JSON
     * object containing particle effect data, extracting key-value pairs to create instances of
     * {@link ParticleKeyframeData}. The keys are interpreted as time in ticks, whereas the values provide effect
     * details like effect type, locator, and script.
     *
     * @param rootObj the root JSON object containing the "particle_effects" data
     * @return an array of {@link ParticleKeyframeData} objects extracted from the JSON object
     */
    private static ParticleKeyframeData[] buildParticleFrameData(JsonObject rootObj) {
        JsonObject particlesObj = JSONUtils.getJsonObject(rootObj, "particle_effects", new JsonObject());
        ParticleKeyframeData[] particles = new ParticleKeyframeData[particlesObj.size()];
        int index = 0;

        for (Map.Entry<String, JsonElement> entry : particlesObj.entrySet()) {
            JsonObject obj = entry.getValue().getAsJsonObject();
            String effect = JSONUtils.getString(obj, "effect", "");
            String locator = JSONUtils.getString(obj, "locator", "");
            String script = JSONUtils.getString(obj, "pre_effect_script", "");

            particles[index] = new ParticleKeyframeData(
                Double.parseDouble(entry.getKey()) * 20d,
                effect,
                locator,
                script
            );
            index++;
        }

        return particles;
    }

    /**
     * Builds an array of {@link CustomInstructionKeyframeData} objects from a given JSON object.
     * <p>
     * This method parses a JSON object containing custom instructions for keyframes, extracting each key-value pair to
     * create instances of {@link CustomInstructionKeyframeData}. The keys are interpreted as time in ticks, and the
     * values as instructions.
     *
     * @param rootObj the root JSON object containing the "timeline" data for custom instructions
     * @return an array of {@link CustomInstructionKeyframeData} objects extracted from the JSON object
     */
    private static CustomInstructionKeyframeData[] buildCustomFrameData(JsonObject rootObj) {
        JsonObject customInstructionsObj = JSONUtils.getJsonObject(rootObj, "timeline", new JsonObject());
        CustomInstructionKeyframeData[] customInstructions = new CustomInstructionKeyframeData[customInstructionsObj
            .size()];
        int index = 0;

        for (Map.Entry<String, JsonElement> entry : customInstructionsObj.entrySet()) {
            String instructions = "";

            if (entry.getValue() instanceof JsonArray) {
                instructions = JsonUtil.GEO_GSON.fromJson(entry.getValue(), ObjectArrayList.class).toString();
            } else if (entry.getValue() instanceof JsonPrimitive) {
                instructions = entry.getValue().getAsString();
            }

            customInstructions[index] = new CustomInstructionKeyframeData(
                Double.parseDouble(entry.getKey()) * 20d,
                instructions
            );
            index++;
        }

        return customInstructions;
    }

    /**
     * Deserializes a JSON element into an {@code AzKeyframes} object. This method converts the JSON representation of
     * keyframe data into instances of {@code SoundKeyframeData}, {@code ParticleKeyframeData}, and
     * {@code CustomInstructionKeyframeData} to construct an {@code AzKeyframes} object.
     *
     * @param json    the JSON element containing the keyframe data
     * @param type    the type parameter for deserialization (not used in this method)
     * @param context the deserialization context provided by Gson
     * @return an instance of {@code AzKeyframes} containing deserialized keyframe data
     * @throws JsonParseException if the JSON is invalid or cannot be properly parsed
     */
    @Override
    public AzKeyframes deserialize(
        JsonElement json,
        Type type,
        JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        SoundKeyframeData[] sounds = buildSoundFrameData(obj);
        ParticleKeyframeData[] particles = buildParticleFrameData(obj);
        CustomInstructionKeyframeData[] customInstructions = buildCustomFrameData(obj);

        return new AzKeyframes(sounds, particles, customInstructions);
    }
}
