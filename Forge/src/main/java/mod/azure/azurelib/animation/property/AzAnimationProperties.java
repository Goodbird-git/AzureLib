package mod.azure.azurelib.animation.property;

import com.sun.istack.internal.NotNull;
import mod.azure.azurelib.animation.easing.AzEasingType;
import mod.azure.azurelib.animation.easing.AzEasingTypes;
import mod.azure.azurelib.animation.property.codec.AzAnimationPropertiesCodec;

import java.util.Objects;

public class AzAnimationProperties {

    public static final AzAnimationPropertiesCodec CODEC = new AzAnimationPropertiesCodec();

    public static final AzAnimationProperties DEFAULT = new AzAnimationProperties(1D, AzEasingTypes.NONE, 0F);

    public static final AzAnimationProperties EMPTY = new AzAnimationProperties(null, null, null);

    protected final Double animationSpeed;

    protected final AzEasingType easingType;

    protected final Float transitionLength;

    public AzAnimationProperties(
            Double animationSpeed,
            AzEasingType easingType,
            Float transitionLength
    ) {
        this.animationSpeed = animationSpeed;
        this.easingType = easingType;
        this.transitionLength = transitionLength;
    }

    public boolean hasAnimationSpeed() {
        return animationSpeed != null;
    }

    public boolean hasEasingType() {
        return easingType != null;
    }

    public boolean hasTransitionLength() {
        return transitionLength != null;
    }

    public AzAnimationProperties withAnimationSpeed(double animationSpeed) {
        return new AzAnimationProperties(animationSpeed, easingType, transitionLength);
    }

    public AzAnimationProperties withEasingType(@NotNull AzEasingType easingType) {
        return new AzAnimationProperties(animationSpeed, easingType, transitionLength);
    }

    public AzAnimationProperties withTransitionLength(float transitionLength) {
        return new AzAnimationProperties(animationSpeed, easingType, transitionLength);
    }

    public double animationSpeed() {
        return animationSpeed == null ? DEFAULT.animationSpeed() : animationSpeed;
    }

    public AzEasingType easingType() {
        return easingType == null ? DEFAULT.easingType() : easingType;
    }

    public float transitionLength() {
        return transitionLength == null ? DEFAULT.transitionLength() : transitionLength;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        AzAnimationProperties that = (AzAnimationProperties) object;

        return Objects.equals(animationSpeed, that.animationSpeed) && Objects.equals(easingType, that.easingType)
                && Objects.equals(transitionLength, that.transitionLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(animationSpeed, easingType, transitionLength);
    }
}