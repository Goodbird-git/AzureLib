package mod.azure.azurelib.core2.render;

import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Function;

import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

public class AzEntityRendererConfig<T extends Entity> extends AzRendererConfig<T> {

    private final Function<T, Float> deathMaxRotationProvider;

    private AzEntityRendererConfig(
        List<AzRenderLayer<T>> renderLayers,
        float scaleHeight,
        float scaleWidth,
        Function<T, Float> deathMaxRotationProvider
    ) {
        super(renderLayers, scaleHeight, scaleWidth);
        this.deathMaxRotationProvider = deathMaxRotationProvider;
    }

    public float getDeathMaxRotation(T entity) {
        return deathMaxRotationProvider.apply(entity);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> AzEntityRendererConfig<T> defaultConfig() {
        return (AzEntityRendererConfig<T>) builder().build();
    }

    public static <T extends Entity> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T extends Entity> extends AzRendererConfig.Builder<T> {

        private Function<T, Float> deathMaxRotationProvider;

        protected Builder() {
            super();
            this.deathMaxRotationProvider = $ -> 90F;
        }

        @Override
        public Builder<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
            return (Builder<T>) super.addRenderLayer(renderLayer);
        }

        /**
         * Sets a provider for the max rotation value for dying entities.<br>
         * You might want to modify this for different aesthetics, such as a
         * {@link net.minecraft.world.entity.monster.Spider} flipping upside down on death.<br>
         * Functionally equivalent to {@link net.minecraft.client.renderer.entity.LivingEntityRenderer#getFlipDegrees}
         */
        public Builder<T> setDeathMaxRotationProvider(Function<T, Float> deathMaxRotationProvider) {
            this.deathMaxRotationProvider = deathMaxRotationProvider;
            return this;
        }

        public AzEntityRendererConfig<T> build() {
            var baseConfig = super.build();

            return new AzEntityRendererConfig<T>(
                baseConfig.renderLayers(),
                baseConfig.scaleHeight(),
                baseConfig.scaleWidth(),
                deathMaxRotationProvider
            );
        }
    }
}
