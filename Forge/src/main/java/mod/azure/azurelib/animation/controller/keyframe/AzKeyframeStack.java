package mod.azure.azurelib.animation.controller.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class AzKeyframeStack<T extends AzKeyframe<?>> {
    public List<T> xKeyframes;
    public List<T> yKeyframes;
    public List<T> zKeyframes;

    public AzKeyframeStack(List<T> xKeyframes, List<T> yKeyframes, List<T> zKeyframes) {
        this.xKeyframes = xKeyframes;
        this.yKeyframes = yKeyframes;
        this.zKeyframes = zKeyframes;
    }

    public AzKeyframeStack() {
        this(new ObjectArrayList<>(), new ObjectArrayList<>(), new ObjectArrayList<>());
    }

    public List<T> xKeyframes() {
        return xKeyframes;
    }

    public List<T> yKeyframes() {
        return yKeyframes;
    }

    public List<T> zKeyframes() {
        return zKeyframes;
    }

    public static <F extends AzKeyframe<?>> AzKeyframeStack<F> from(AzKeyframeStack<F> otherStack) {
        return new AzKeyframeStack<>(otherStack.xKeyframes, otherStack.yKeyframes, otherStack.zKeyframes);
    }

    public double getLastKeyframeTime() {
        double xTime = 0;
        double yTime = 0;
        double zTime = 0;

        for (T frame : xKeyframes()) {
            xTime += frame.length();
        }

        for (T frame : yKeyframes()) {
            yTime += frame.length();
        }

        for (T frame : zKeyframes()) {
            zTime += frame.length();
        }

        return Math.max(xTime, Math.max(yTime, zTime));
    }
}
