package mod.azure.azurelib.core2.animation.controller;

import mod.azure.azurelib.core.animation.EasingType;

public class AzAnimationProperties {

    private double animationSpeed;

    private EasingType easingType;

    private double transitionLength;

    public AzAnimationProperties() {
        this.animationSpeed = 1;
        this.easingType = null;
        this.transitionLength = 0;
    }

    public double animationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public EasingType easingType() {
        return easingType;
    }

    public void setEasingType(EasingType easingType) {
        this.easingType = easingType;
    }

    public double transitionLength() {
        return transitionLength;
    }

    public void setTransitionLength(double transitionLength) {
        this.transitionLength = transitionLength;
    }
}
