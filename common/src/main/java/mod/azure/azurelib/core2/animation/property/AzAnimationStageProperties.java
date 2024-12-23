package mod.azure.azurelib.core2.animation.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import mod.azure.azurelib.core2.animation.easing.AzEasingType;
import mod.azure.azurelib.core2.animation.easing.AzEasingTypes;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.core2.animation.property.codec.AzAnimationStagePropertiesCodec;

public class AzAnimationStageProperties extends AzAnimationProperties {

    public static final AzAnimationStagePropertiesCodec CODEC = new AzAnimationStagePropertiesCodec();

    public static final AzAnimationStageProperties DEFAULT = new AzAnimationStageProperties(
        1D,
        AzEasingTypes.NONE,
        AzLoopType.PLAY_ONCE,
        0F
    );

    public static final AzAnimationStageProperties EMPTY = new AzAnimationStageProperties(null, null, null, null);

    private final AzLoopType loopType;

    public AzAnimationStageProperties(
        @Nullable Double animationSpeed,
        @Nullable AzEasingType easingType,
        @Nullable AzLoopType loopType,
        @Nullable Float transitionLength
    ) {
        super(animationSpeed, easingType, transitionLength);
        this.loopType = loopType;
    }

    public boolean hasLoopType() {
        return loopType != null;
    }

    @Override
    public AzAnimationStageProperties withAnimationSpeed(double animationSpeed) {
        return new AzAnimationStageProperties(animationSpeed, easingType, loopType, transitionLength);
    }

    @Override
    public AzAnimationStageProperties withEasingType(@NotNull AzEasingType easingType) {
        return new AzAnimationStageProperties(animationSpeed, easingType, loopType, transitionLength);
    }

    public AzAnimationStageProperties withLoopType(@NotNull AzLoopType loopType) {
        return new AzAnimationStageProperties(animationSpeed, easingType, loopType, transitionLength);
    }

    @Override
    public AzAnimationStageProperties withTransitionLength(float transitionLength) {
        return new AzAnimationStageProperties(animationSpeed, easingType, loopType, transitionLength);
    }

    public AzLoopType loopType() {
        return loopType == null ? DEFAULT.loopType() : loopType;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        if (!super.equals(object)) {
            return false;
        }

        AzAnimationStageProperties that = (AzAnimationStageProperties) object;

        return Objects.equals(loopType, that.loopType) && super.equals(object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loopType);
    }
}