package mod.azure.azurelib.animation.controller;

import mod.azure.azurelib.animation.easing.AzEasingType;

public class AzAnimationProperties {
    private double animationSpeed;

    private AzEasingType easingType;

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

    public AzEasingType easingType() {
        return easingType;
    }

    public void setEasingType(AzEasingType easingType) {
        this.easingType = easingType;
    }

    public double transitionLength() {
        return transitionLength;
    }

    public void setTransitionLength(double transitionLength) {
        this.transitionLength = transitionLength;
    }
}
