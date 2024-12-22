package mod.azure.azurelib.animation.controller.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.animation.easing.AzEasingType;
import mod.azure.azurelib.animation.easing.AzEasingTypes;
import mod.azure.azurelib.core.math.IValue;

import java.util.List;
import java.util.Objects;


public class AzKeyframe<T extends IValue> {
    public double length;
    public T startValue;
    public T endValue;
    public AzEasingType easingType;
    public List<T> easingArgs;

    /**
     * Animation keyframe data
     *
     * @param length     The length (in ticks) the keyframe lasts for
     * @param startValue The value to start the keyframe's transformation with
     * @param endValue   The value to end the keyframe's transformation with
     * @param easingType The {@code EasingType} to use for transformations
     * @param easingArgs The arguments to provide to the easing calculation
     */
    public AzKeyframe(double length, T startValue, T endValue, AzEasingType easingType, List<T> easingArgs) {
        this.length = length;
        this.startValue = startValue;
        this.endValue = endValue;
        this.easingType = easingType;
        this.easingArgs = easingArgs;
    }

    public double length() {
        return this.length;
    }

    public T startValue() {
        return this.startValue;
    }

    public T endValue() {
        return this.endValue;
    }

    public AzEasingType easingType() {
        return this.easingType;
    }

    public List<T> easingArgs() {
        return this.easingArgs;
    }

    public AzKeyframe(double length, T startValue, T endValue) {
        this(length, startValue, endValue, AzEasingTypes.LINEAR);
    }

    public AzKeyframe(double length, T startValue, T endValue, AzEasingType easingType) {
        this(length, startValue, endValue, easingType, new ObjectArrayList<>(0));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.length, this.startValue, this.endValue, this.easingType, this.easingArgs);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        return hashCode() == obj.hashCode();
    }
}
