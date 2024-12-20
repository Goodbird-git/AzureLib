package mod.azure.azurelib.core2.render;

public class AzRendererConfig {

    private final float scaleHeight;

    private final float scaleWidth;

    public AzRendererConfig(float scaleHeight, float scaleWidth) {
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
    }

    public float scaleHeight() {
        return scaleHeight;
    }

    public float scaleWidth() {
        return scaleWidth;
    }

    public static class Builder {

        private float scaleHeight;

        private float scaleWidth;

        protected Builder() {
            this.scaleHeight = 1;
            this.scaleWidth = 1;
        }

        public Builder setScale(float scale) {
            return setScale(scale, scale);
        }

        public Builder setScale(float scaleWidth, float scaleHeight) {
            this.scaleHeight = scaleHeight;
            this.scaleWidth = scaleWidth;
            return this;
        }

        public AzRendererConfig build() {
            return new AzRendererConfig(scaleHeight, scaleWidth);
        }
    }
}
