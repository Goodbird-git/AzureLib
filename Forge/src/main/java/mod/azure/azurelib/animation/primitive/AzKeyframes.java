package mod.azure.azurelib.animation.primitive;

import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.SoundKeyframeData;

/**
 * Represents a collection of keyframe data used for animations. <br/>
 * The AzKeyframes record combines different types of keyframe data into a single structure:
 * <ul>
 * <li>{@link SoundKeyframeData} for sound-related keyframes.</li>
 * <li>{@link ParticleKeyframeData} for particle effect-related keyframes.</li>
 * <li>{@link CustomInstructionKeyframeData} for custom instruction keyframes.</li>
 * </ul>
 * <br/>
 * This record organizes and provides access to all three types of keyframe data, enabling cohesive handling of
 * animation sequences defined in an animation system.
 */
public class AzKeyframes {
    public SoundKeyframeData[] sounds;
    public ParticleKeyframeData[] particles;
    public CustomInstructionKeyframeData[] customInstructions;

    public AzKeyframes(SoundKeyframeData[] sounds, ParticleKeyframeData[] particles, CustomInstructionKeyframeData[] customInstructions) {
        this.sounds = sounds;
        this.particles = particles;
        this.customInstructions = customInstructions;
    }

    public SoundKeyframeData[] sounds() {
        return sounds;
    }

    public ParticleKeyframeData[] particles() {
        return particles;
    }

    public CustomInstructionKeyframeData[] customInstructions() {
        return customInstructions;
    }
}