package mod.azure.azurelib.core2.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

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
        ResourceLocation modelLocation,
        ResourceLocation textureLocation
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
        public Builder<T> setAnimatorProvider(Supplier<@Nullable AzAnimator<T>> animatorProvider) {
            return (Builder<T>) super.setAnimatorProvider(animatorProvider);
        }

        public Builder<T> setDeathMaxRotation(float angle) {
            this.deathMaxRotationProvider = $ -> angle;
            return this;
        }

        /**
         * Sets a provider for the max rotation value for dying entities.<br>
         * You might want to modify this for different aesthetics, such as a
         * {@link net.minecraft.world.entity.monster.Spider} flipping upside down on death.<br>
         * Functionally equivalent to {@link net.minecraft.client.renderer.entity.LivingEntityRenderer#getFlipDegrees}
         */
        public Builder<T> setDeathMaxRotation(Function<T, Float> deathMaxRotationProvider) {
            this.deathMaxRotationProvider = deathMaxRotationProvider;
            return this;
        }

        public AzEntityRendererConfig<T> build() {
            var baseConfig = super.build();

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
