package mod.azure.azurelib.core2.animation.primitive;

import mod.azure.azurelib.core.keyframe.event.data.CustomInstructionKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.ParticleKeyframeData;
import mod.azure.azurelib.core.keyframe.event.data.SoundKeyframeData;

public record AzKeyframes(
    SoundKeyframeData[] sounds,
    ParticleKeyframeData[] particles,
    CustomInstructionKeyframeData[] customInstructions
) {}