package mod.azure.azurelib.animation.controller.keyframe;

public class AzAnimationPoint {

    public final AzKeyframe<?> keyframe;
    public final double currentTick;
    public final double transitionLength;
    public final double animationStartValue;
    public final double animationEndValue;

    public AzAnimationPoint(AzKeyframe<?> keyframe, double currentTick, double transitionLength, double animationStartValue, double animationEndValue) {
        this.keyframe = keyframe;
        this.currentTick = currentTick;
        this.transitionLength = transitionLength;
        this.animationStartValue = animationStartValue;
        this.animationEndValue = animationEndValue;
    }

    public AzKeyframe<?> keyframe() {
        return keyframe;
    }

    public double currentTick() {
        return currentTick;
    }

    public double transitionLength() {
        return transitionLength;
    }

    public double animationStartValue() {
        return animationStartValue;
    }

    public double animationEndValue() {
        return animationEndValue;
    }

    @Override
    public String toString() {
        return "Tick: " + this.currentTick +
                " | Transition Length: " + this.transitionLength +
                " | Start Value: " + this.animationStartValue +
                " | End Value: " + this.animationEndValue;
    }
}
