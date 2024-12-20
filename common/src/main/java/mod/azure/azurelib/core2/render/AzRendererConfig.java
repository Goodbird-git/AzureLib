package mod.azure.azurelib.core2.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.List;

import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

public class AzRendererConfig<T> {

    private final List<AzRenderLayer<T>> renderLayers;

    private final float scaleHeight;

    private final float scaleWidth;

    public AzRendererConfig(
        List<AzRenderLayer<T>> renderLayers,
        float scaleHeight,
        float scaleWidth
    ) {
        this.renderLayers = Collections.unmodifiableList(renderLayers);
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
    }

    public List<AzRenderLayer<T>> renderLayers() {
        return renderLayers;
    }

    public float scaleHeight() {
        return scaleHeight;
    }

    public float scaleWidth() {
        return scaleWidth;
    }

    public static class Builder<T> {

        private final List<AzRenderLayer<T>> renderLayers;

        private float scaleHeight;

        private float scaleWidth;

        protected Builder() {
            this.renderLayers = new ObjectArrayList<>();
            this.scaleHeight = 1;
            this.scaleWidth = 1;
        }

        /**
         * Adds a {@link AzRenderLayer} to this config, to be called after the main model is rendered each frame
         */
        public Builder<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
            this.renderLayers.add(renderLayer);
            return this;
        }

        public Builder<T> setScale(float scale) {
            return setScale(scale, scale);
        }

        public Builder<T> setScale(float scaleWidth, float scaleHeight) {
            this.scaleHeight = scaleHeight;
            this.scaleWidth = scaleWidth;
            return this;
        }

        public AzRendererConfig<T> build() {
            return new AzRendererConfig<>(renderLayers, scaleHeight, scaleWidth);
        }
    }
}
