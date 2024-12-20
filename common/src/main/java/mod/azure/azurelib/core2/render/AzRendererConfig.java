package mod.azure.azurelib.core2.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

public class AzRendererConfig<T> {

    private final Supplier<@Nullable AzAnimator<T>> animatorProvider;

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

    public @Nullable AzAnimator<T> createAnimator() {
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

        private Supplier<@Nullable AzAnimator<T>> animatorProvider;

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

        public Builder<T> setAnimatorProvider(Supplier<@Nullable AzAnimator<T>> animatorProvider) {
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

        public Builder<T> setScale(float scale) {
            return setScale(scale, scale);
        }

        public Builder<T> setScale(float scaleWidth, float scaleHeight) {
            this.scaleHeight = scaleHeight;
            this.scaleWidth = scaleWidth;
            return this;
        }

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
