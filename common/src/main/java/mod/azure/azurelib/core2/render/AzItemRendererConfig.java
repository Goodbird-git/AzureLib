package mod.azure.azurelib.core2.render;

public class AzItemRendererConfig extends AzRendererConfig {

    private final boolean useEntityGuiLighting;

    private final boolean useNewOffset;

    private AzItemRendererConfig(
        float scaleHeight,
        float scaleWidth,
        boolean useEntityGuiLighting,
        boolean useNewOffset
    ) {
        super(scaleHeight, scaleWidth);
        this.useEntityGuiLighting = useEntityGuiLighting;
        this.useNewOffset = useNewOffset;
    }

    public boolean useEntityGuiLighting() {
        return useEntityGuiLighting;
    }

    public boolean useNewOffset() {
        return useNewOffset;
    }

    public static AzItemRendererConfig defaultConfig() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AzRendererConfig.Builder {

        private boolean useEntityGuiLighting;

        private boolean useNewOffset;

        protected Builder() {
            super();
            this.useEntityGuiLighting = false;
            this.useNewOffset = false;
        }

        public Builder useEntityGuiLighting() {
            this.useEntityGuiLighting = true;
            return this;
        }

        public boolean useNewOffset() {
            return useNewOffset;
        }

        /**
         * @param useNewOffset Determines whether to apply the y offset for a model due to the change in BlockBench
         *                     4.11.
         */
        public AzRendererConfig.Builder useNewOffset(boolean useNewOffset) {
            this.useNewOffset = useNewOffset;
            return this;
        }

        public AzItemRendererConfig build() {
            var baseConfig = super.build();
            return new AzItemRendererConfig(
                baseConfig.scaleHeight(),
                baseConfig.scaleWidth(),
                useEntityGuiLighting,
                useNewOffset
            );
        }
    }
}
