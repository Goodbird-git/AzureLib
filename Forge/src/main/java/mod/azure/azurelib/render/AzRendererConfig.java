package mod.azure.azurelib.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The {@code AzRendererConfig} class is a configuration class used for defining rendering configurations for generic
 * animatable objects. It allows customization of model and texture locations, animators, render layers, and scale
 * factors.
 *
 * @param <T> The type of animatable object this configuration applies to.
 */
public class AzRendererConfig<T> {

    private final Supplier<AzAnimator<T>> animatorProvider;

    private final Function<T, ResourceLocation> modelLocationProvider;

    private final List<AzRenderLayer<T>> renderLayers;

    private final Function<T, ResourceLocation> textureLocationProvider;

    private final float scaleHeight;

    private final float scaleWidth;

    public AzRendererConfig(
        Supplier<AzAnimator<T>> animatorProvider,
        Function<T, ResourceLocation> modelLocationProvider,
        List<AzRenderLayer<T>> renderLayers,
        Function<T, ResourceLocation> textureLocationProvider,
        float scaleHeight,
        float scaleWidth
    ) {
        this.animatorProvider = animatorProvider;
        this.modelLocationProvider = modelLocationProvider;
        this.renderLayers = Collections.unmodifiableList(renderLayers);
        this.textureLocationProvider = textureLocationProvider;
        this.scaleHeight = scaleHeight;
        this.scaleWidth = scaleWidth;
    }

    public AzAnimator<T> createAnimator() {
        return animatorProvider.get();
    }

    public ResourceLocation modelLocation(T animatable) {
        return modelLocationProvider.apply(animatable);
    }

    public ResourceLocation textureLocation(T animatable) {
        return textureLocationProvider.apply(animatable);
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

        private final Function<T, ResourceLocation> modelLocationProvider;

        private final List<AzRenderLayer<T>> renderLayers;

        private final Function<T, ResourceLocation> textureLocationProvider;

        private Supplier<AzAnimator<T>> animatorProvider;

        private float scaleHeight;

        private float scaleWidth;

        protected Builder(
            Function<T, ResourceLocation> modelLocationProvider,
            Function<T, ResourceLocation> textureLocationProvider
        ) {
            this.animatorProvider = () -> null;
            this.modelLocationProvider = modelLocationProvider;
            this.renderLayers = new ObjectArrayList<>();
            this.textureLocationProvider = textureLocationProvider;
            this.scaleHeight = 1;
            this.scaleWidth = 1;
        }

        /**
         * Sets the animator provider for the builder. The animator provider is responsible for supplying an instance of
         * {@link AzAnimator} that defines the animation logic for the target object.
         *
         * @param animatorProvider a {@link Supplier} that provides a {@link AzAnimator} instance or null if no custom
         *                         animation logic is required
         * @return the updated {@code Builder} instance for chaining configuration methods
         */
        public Builder<T> setAnimatorProvider(Supplier<AzAnimator<T>> animatorProvider) {
            this.animatorProvider = animatorProvider;
            return this;
        }

        /**
         * Adds a {@link AzRenderLayer} to this config, to be called after the main model is rendered each frame
         */
        public Builder<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
            this.renderLayers.add(renderLayer);
            return this;
        }

        /**
         * Sets the scaling factor uniformly for both width and height dimensions.
         *
         * @param scale the uniform scaling factor to be applied to both width and height
         * @return the {@code Builder} instance for method chaining
         */
        public Builder<T> setScale(float scale) {
            return setScale(scale, scale);
        }

        /**
         * Sets the scaling factors for both width and height.
         *
         * @param scaleWidth  the scaling factor for the width
         * @param scaleHeight the scaling factor for the height
         * @return the updated builder instance for chaining operations
         */
        public Builder<T> setScale(float scaleWidth, float scaleHeight) {
            this.scaleHeight = scaleHeight;
            this.scaleWidth = scaleWidth;
            return this;
        }

        /**
         * Builds and returns a finalized {@link AzRendererConfig} instance with the current configuration settings
         * provided through the builder.
         *
         * @return a new instance of {@link AzRendererConfig} configured with the specified animator provider, model
         *         location provider, texture location provider, render layers, and scale factors.
         */
        public AzRendererConfig<T> build() {
            return new AzRendererConfig<>(
                animatorProvider,
                modelLocationProvider,
                renderLayers,
                textureLocationProvider,
                scaleHeight,
                scaleWidth
            );
        }
    }
}
