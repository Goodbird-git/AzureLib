package mod.azure.azurelib.render.entity;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.render.AzRendererConfig;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Configures the rendering behavior for custom entities in the game. This extends {@link AzRendererConfig}, adding
 * extra functionality specifically for handling entity death rotations.
 *
 * @param <T> the entity type this configuration applies to, extending {@link Entity}
 */
public class AzEntityRendererConfig<T extends Entity> extends AzRendererConfig<T> {

    private final Function<T, Float> deathMaxRotationProvider;

    private AzEntityRendererConfig(
        Supplier<AzAnimator<T>> animatorProvider,
        Function<T, Float> deathMaxRotationProvider,
        Function<T, ResourceLocation> modelLocationProvider,
        List<AzRenderLayer<T>> renderLayers,
        Function<T, ResourceLocation> textureLocationProvider,
        float scaleHeight,
        float scaleWidth
    ) {
        super(animatorProvider, modelLocationProvider, renderLayers, textureLocationProvider, scaleHeight, scaleWidth);
        this.deathMaxRotationProvider = deathMaxRotationProvider;
    }

    public float getDeathMaxRotation(T entity) {
        return deathMaxRotationProvider.apply(entity);
    }

    public static <T extends Entity> Builder<T> builder(
        @Nonnull ResourceLocation modelLocation,
        @Nonnull ResourceLocation textureLocation
    ) {
        return new Builder<>($ -> modelLocation, $ -> textureLocation);
    }

    public static <T extends Entity> Builder<T> builder(
        Function<T, ResourceLocation> modelLocationProvider,
        Function<T, ResourceLocation> textureLocationProvider
    ) {
        return new Builder<>(modelLocationProvider, textureLocationProvider);
    }

    public static class Builder<T extends Entity> extends AzRendererConfig.Builder<T> {

        private Function<T, Float> deathMaxRotationProvider;

        protected Builder(
            Function<T, ResourceLocation> modelLocationProvider,
            Function<T, ResourceLocation> textureLocationProvider
        ) {
            super(modelLocationProvider, textureLocationProvider);
            this.deathMaxRotationProvider = $ -> 90F;
        }

        @Override
        public Builder<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
            return (Builder<T>) super.addRenderLayer(renderLayer);
        }

        @Override
        public Builder<T> setAnimatorProvider(Supplier<AzAnimator<T>> animatorProvider) {
            return (Builder<T>) super.setAnimatorProvider(animatorProvider);
        }

        public Builder<T> setDeathMaxRotation(float angle) {
            this.deathMaxRotationProvider = $ -> angle;
            return this;
        }

        /**
         * Sets a provider for the max rotation value for dying entities.<br>
         * You might want to modify this for different aesthetics, such as a
         * {@link net.minecraft.entity.monster.EntitySpider} flipping upside down on death.<br>
         */
        public Builder<T> setDeathMaxRotation(Function<T, Float> deathMaxRotationProvider) {
            this.deathMaxRotationProvider = deathMaxRotationProvider;
            return this;
        }

        public AzEntityRendererConfig<T> build() {
            AzRendererConfig<T> baseConfig = super.build();

            return new AzEntityRendererConfig<>(
                baseConfig::createAnimator,
                deathMaxRotationProvider,
                baseConfig::modelLocation,
                baseConfig.renderLayers(),
                baseConfig::textureLocation,
                baseConfig.scaleHeight(),
                baseConfig.scaleWidth()
            );
        }
    }
}
