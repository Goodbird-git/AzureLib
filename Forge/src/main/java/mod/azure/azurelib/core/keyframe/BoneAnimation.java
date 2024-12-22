/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package mod.azure.azurelib.core.keyframe;

import mod.azure.azurelib.animation.controller.keyframe.AzKeyframe;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyframeStack;
import mod.azure.azurelib.core.math.IValue;


public class BoneAnimation {
	String boneName;
	AzKeyframeStack<AzKeyframe<IValue>> rotationKeyFrames;
	AzKeyframeStack<AzKeyframe<IValue>> positionKeyFrames;
	AzKeyframeStack<AzKeyframe<IValue>> scaleKeyFrames;

	/**
	 * A record of a deserialized animation for a given bone.<br>
	 * Responsible for holding the various {@link Keyframe Keyframes} for the bone's animation transformations
	 * @param boneName The name of the bone as listed in the {@code animation.json}
	 * @param rotationKeyFrames The deserialized rotation {@code Keyframe} stack
	 * @param positionKeyFrames The deserialized position {@code Keyframe} stack
	 * @param scaleKeyFrames The deserialized scale {@code Keyframe} stack
	 */
	public BoneAnimation(String boneName,
						 AzKeyframeStack<AzKeyframe<IValue>> rotationKeyFrames,
						 AzKeyframeStack<AzKeyframe<IValue>> positionKeyFrames,
						 AzKeyframeStack<AzKeyframe<IValue>> scaleKeyFrames){
		this.boneName = boneName;
		this.rotationKeyFrames = rotationKeyFrames;
		this.positionKeyFrames = positionKeyFrames;
		this.scaleKeyFrames = scaleKeyFrames;

	}

	public String boneName() {
		return boneName;
	}

	public AzKeyframeStack<AzKeyframe<IValue>> rotationKeyFrames() {
		return rotationKeyFrames;
	}

	public AzKeyframeStack<AzKeyframe<IValue>> positionKeyFrames() {
		return positionKeyFrames;
	}

	public AzKeyframeStack<AzKeyframe<IValue>> scaleKeyFrames() {
		return scaleKeyFrames;
	}
}
