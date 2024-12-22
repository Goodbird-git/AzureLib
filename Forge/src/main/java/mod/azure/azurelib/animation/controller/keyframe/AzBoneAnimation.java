package mod.azure.azurelib.animation.controller.keyframe;

import mod.azure.azurelib.core.math.IValue;

public class AzBoneAnimation {
    public String boneName;
    public AzKeyframeStack<AzKeyframe<IValue>> rotationKeyframes;
    public AzKeyframeStack<AzKeyframe<IValue>> positionKeyframes;
    public AzKeyframeStack<AzKeyframe<IValue>> scaleKeyframes;

    public AzBoneAnimation(String boneName, AzKeyframeStack<AzKeyframe<IValue>> rotationKeyframes, AzKeyframeStack<AzKeyframe<IValue>> positionKeyframes, AzKeyframeStack<AzKeyframe<IValue>> scaleKeyframes) {
        this.boneName = boneName;
        this.rotationKeyframes = rotationKeyframes;
        this.positionKeyframes = positionKeyframes;
        this.scaleKeyframes = scaleKeyframes;
    }

    public String boneName() {
        return boneName;
    }

    public AzKeyframeStack<AzKeyframe<IValue>> rotationKeyframes() {
        return rotationKeyframes;
    }

    public AzKeyframeStack<AzKeyframe<IValue>> positionKeyframes() {
        return positionKeyframes;
    }

    public AzKeyframeStack<AzKeyframe<IValue>> scaleKeyframes() {
        return scaleKeyframes;
    }
}
