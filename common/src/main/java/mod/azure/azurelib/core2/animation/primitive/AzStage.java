package mod.azure.azurelib.core2.animation.primitive;

import java.util.Objects;

/**
 * An animation stage for a {@link AzRawAnimation} builder.<br>
 * This is an entry object representing a single animation stage of the final compiled animation.
 */
public record AzStage(
    String animationName,
    AzLoopType loopType,
    int additionalTicks
) {

    public static final String WAIT = "internal.wait";

    public AzStage(String animationName, AzLoopType loopType) {
        this(animationName, loopType, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.animationName, this.loopType);
    }
}