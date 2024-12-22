package mod.azure.azurelib.render.block;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.render.AzRendererConfig;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The {@code AzBlockEntityRendererConfig} class is a specialized configuration for rendering block entities. It extends
 * the generic {@link AzRendererConfig} and provides additional methods to streamline the creation of configurations
 * specifically for block entity renderers.
 *
 * @param <T> The type of block entity this configuration is tailored for.
 */
public class AzBlockEntityRendererConfig<T extends TileEntity> extends AzRendererConfig<T> {

    private AzBlockEntityRendererConfig(
        Supplier<AzAnimator<T>> animatorProvider,
        Function<T, ResourceLocation> modelLocationProvider,
        List<AzRenderLayer<T>> renderLayers,
        Function<T, ResourceLocation> textureLocationProvider,
        float scaleHeight,
        float scaleWidth
    ) {
        super(animatorProvider, modelLocationProvider, renderLayers, textureLocationProvider, scaleHeight, scaleWidth);
    }

    public static <T extends TileEntity> Builder<T> builder(
        ResourceLocation modelLocation,
        ResourceLocation textureLocation
    ) {
        return new Builder<>($ -> modelLocation, $ -> textureLocation);
    }

    public static <T extends TileEntity> Builder<T> builder(
        Function<T, ResourceLocation> modelLocationProvider,
        Function<T, ResourceLocation> textureLocationProvider
    ) {
        return new Builder<>(modelLocationProvider, textureLocationProvider);
    }

    public static class Builder<T extends TileEntity> extends AzRendererConfig.Builder<T> {

        protected Builder(
            Function<T, ResourceLocation> modelLocationProvider,
            Function<T, ResourceLocation> textureLocationProvider
        ) {
            super(modelLocationProvider, textureLocationProvider);
        }

        @Override
        public Builder<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
            return (Builder<T>) super.addRenderLayer(renderLayer);
        }

        @Override
        public Builder<T> setAnimatorProvider(Supplier<AzAnimator<T>> animatorProvider) {
            return (Builder<T>) super.setAnimatorProvider(animatorProvider);
        }

        public AzBlockEntityRendererConfig<T> build() {
            AzRendererConfig<T> baseConfig = super.build();

            return new AzBlockEntityRendererConfig<>(
                baseConfig::createAnimator,
                baseConfig::modelLocation,
                baseConfig.renderLayers(),
                baseConfig::textureLocation,
                baseConfig.scaleHeight(),
                baseConfig.scaleWidth()
            );
        }
    }
}
