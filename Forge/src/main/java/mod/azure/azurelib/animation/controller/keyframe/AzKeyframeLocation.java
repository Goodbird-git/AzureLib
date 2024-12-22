package mod.azure.azurelib.animation.controller.keyframe;

public class AzKeyframeLocation<T extends AzKeyframe<?>> {
    public T keyframe;
    public double startTick;

    public AzKeyframeLocation(T keyframe, double startTick) {
        this.keyframe = keyframe;
        this.startTick = startTick;
    }

    public T keyframe() {
        return keyframe;
    }
    public double startTick() {
        return startTick;
    }
}
