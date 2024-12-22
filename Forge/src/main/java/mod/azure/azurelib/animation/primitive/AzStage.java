package mod.azure.azurelib.animation.primitive;

import java.util.Objects;

/**
 * An animation stage for a {@link AzRawAnimation} builder.<br>
 * This is an entry object representing a single animation stage of the final compiled animation.
 */
public class AzStage {

    public String animationName;
    public AzLoopType loopType;
    public int additionalTicks;
    public static final String WAIT = "internal.wait";

    public AzStage(String animationName, AzLoopType loopType, int additionalTicks) {

    }
    public AzStage(String animationName, AzLoopType loopType) {
        this(animationName, loopType, 0);
    }

    public String animationName() {
        return animationName;
    }

    public AzLoopType loopType() {
        return loopType;
    }

    public int additionalTicks() {
        return additionalTicks;
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
