/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package mod.azure.azurelib.common.internal.common.core.keyframe;

/**
 * Animation state record that holds the state of an animation at a given point
 * @param currentTick The lerped tick time (current tick + partial tick) of the point
 * @param transitionLength The length of time (in ticks) that the point should take to transition
 * @param animationStartValue The start value to provide to the animation handling system
 * @param animationEndValue The end value to provide to the animation handling system
 * @param keyFrame The {@code Nullable} Keyframe
 */
public record AnimationPoint(Keyframe<?> keyFrame, double currentTick, double transitionLength, double animationStartValue, double animationEndValue) {
	@Override
	public String toString() {
		return "Tick: " + this.currentTick +
				" | Transition Length: " + this.transitionLength +
				" | Start Value: " + this.animationStartValue +
				" | End Value: " + this.animationEndValue;
	}
}
