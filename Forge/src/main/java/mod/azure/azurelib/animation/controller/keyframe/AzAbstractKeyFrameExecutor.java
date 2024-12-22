package mod.azure.azurelib.animation.controller.keyframe;

import mod.azure.azurelib.core.keyframe.AnimationPoint;
import mod.azure.azurelib.core.keyframe.Keyframe;
import mod.azure.azurelib.core.keyframe.KeyframeLocation;
import mod.azure.azurelib.core.math.Constant;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.object.Axis;

import java.util.List;

/**
 * AzAbstractKeyFrameExecutor is a base class designed to handle animations and transitions between keyframes
 * in a generic and reusable fashion. It provides the foundational logic for determining the current state
 * of an animation based on the tick time and computing the animation's required values.
 */
public class AzAbstractKeyFrameExecutor {

    protected AzAbstractKeyFrameExecutor() {}

    /**
     * Convert a {@link KeyframeLocation} to an {@link AnimationPoint}
     */
    protected AnimationPoint getAnimationPointAtTick(
        List<Keyframe<IValue>> frames,
        double tick,
        boolean isRotation,
        Axis axis
    ) {
        KeyframeLocation<Keyframe<IValue>> location = getCurrentKeyFrameLocation(frames, tick);
        Keyframe<IValue> currentFrame = location.keyframe();
        double startValue = currentFrame.startValue().get();
        double endValue = currentFrame.endValue().get();

        if (isRotation) {
            if (!(currentFrame.startValue() instanceof Constant)) {
                startValue = Math.toRadians(startValue);

                if (axis == Axis.X || axis == Axis.Y) {
                    startValue *= -1;
                }
            }

            if (!(currentFrame.endValue() instanceof Constant)) {
                endValue = Math.toRadians(endValue);

                if (axis == Axis.X || axis == Axis.Y) {
                    endValue *= -1;
                }
            }
        }

        return new AnimationPoint(currentFrame, location.startTick(), currentFrame.length(), startValue, endValue);
    }

    /**
     * Returns the {@link Keyframe} relevant to the current tick time
     *
     * @param frames     The list of {@code KeyFrames} to filter through
     * @param ageInTicks The current tick time
     * @return A new {@code KeyFrameLocation} containing the current {@code KeyFrame} and the tick time used to find it
     */
    protected KeyframeLocation<Keyframe<IValue>> getCurrentKeyFrameLocation(
        List<Keyframe<IValue>> frames,
        double ageInTicks
    ) {
        double totalFrameTime = 0.0;

        for (Keyframe<IValue> frame : frames) {
            totalFrameTime += frame.length();

            if (totalFrameTime > ageInTicks) {
                return new KeyframeLocation<>(frame, (ageInTicks - (totalFrameTime - frame.length())));
            }
        }

        return new KeyframeLocation<>(frames.get(frames.size() - 1), ageInTicks);
    }
}
