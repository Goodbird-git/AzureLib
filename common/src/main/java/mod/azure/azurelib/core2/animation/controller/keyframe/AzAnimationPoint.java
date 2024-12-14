/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core2.animation.controller.keyframe;

import mod.azure.azurelib.core2.animation.AzKeyframe;

/**
 * Animation state record that holds the state of an animation at a given point
 */
public class AzAnimationPoint {

    public static AzAnimationPoint create() {
        return new AzAnimationPoint(null, 0, 0, 0, 0);
    }

    private AzKeyframe<?> keyFrame;

    private double currentTick;

    private double transitionLength;

    private double animationStartValue;

    private double animationEndValue;

    /**
     * @param currentTick         The lerped tick time (current tick + partial tick) of the point
     * @param transitionLength    The length of time (in ticks) that the point should take to transition
     * @param animationStartValue The start value to provide to the animation handling system
     * @param animationEndValue   The end value to provide to the animation handling system
     * @param keyFrame            The {@code Nullable} Keyframe
     */
    public AzAnimationPoint(
        AzKeyframe<?> keyFrame,
        double currentTick,
        double transitionLength,
        double animationStartValue,
        double animationEndValue
    ) {
        set(keyFrame, currentTick, transitionLength, animationStartValue, animationEndValue);
    }

    public boolean isEmpty() {
        return keyFrame == null;
    }

    public void set(
        AzKeyframe<?> keyFrame,
        double currentTick,
        double transitionLength,
        double animationStartValue,
        double animationEndValue
    ) {
        this.keyFrame = keyFrame;
        this.currentTick = currentTick;
        this.transitionLength = transitionLength;
        this.animationStartValue = animationStartValue;
        this.animationEndValue = animationEndValue;
    }

    public double animationEndValue() {
        return animationEndValue;
    }

    public double animationStartValue() {
        return animationStartValue;
    }

    public double currentTick() {
        return currentTick;
    }

    public AzKeyframe<?> keyFrame() {
        return keyFrame;
    }

    public double transitionLength() {
        return transitionLength;
    }

    @Override
    public String toString() {
        return "Tick: " + this.currentTick +
            " | Transition Length: " + this.transitionLength +
            " | Start Value: " + this.animationStartValue +
            " | End Value: " + this.animationEndValue;
    }
}
