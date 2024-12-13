package mod.azure.azurelib.core2.animation;

/**
 * @param boneResetTime                       The speed with which bones lacking animation should reset back to their
 *                                            default position. 1 by default.
 * @param crashIfBoneMissing                  Crash when a bone cannot be found while animating. False by default.
 * @param shouldPlayAnimationsWhileGamePaused Whether animations should continue playing in the background when the game
 *                                            is paused. False by default.
 */
public record AzAnimatorConfig(
    double boneResetTime,
    boolean crashIfBoneMissing,
    boolean shouldPlayAnimationsWhileGamePaused
) {

    public static Builder builder() {
        return new Builder();
    }

    public static AzAnimatorConfig defaultConfig() {
        return builder().build();
    }

    public static class Builder {

        private double boneResetTime;

        private boolean crashIfBoneMissing;

        private boolean shouldPlayAnimationsWhileGamePaused;

        private Builder() {
            this.boneResetTime = 1;
            this.crashIfBoneMissing = false;
            this.shouldPlayAnimationsWhileGamePaused = false;
        }

        public Builder crashIfBoneMissing() {
            this.crashIfBoneMissing = true;
            return this;
        }

        public Builder shouldPlayAnimationsWhileGamePaused() {
            this.shouldPlayAnimationsWhileGamePaused = true;
            return this;
        }

        public Builder withBoneResetTime(double boneResetTime) {
            this.boneResetTime = boneResetTime;
            return this;
        }

        public AzAnimatorConfig build() {
            return new AzAnimatorConfig(
                boneResetTime,
                crashIfBoneMissing,
                shouldPlayAnimationsWhileGamePaused
            );
        }
    }
}
