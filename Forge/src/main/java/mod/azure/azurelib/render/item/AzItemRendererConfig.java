package mod.azure.azurelib.render.item;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.render.AzRendererConfig;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Configuration class for rendering items using customized settings in an animation framework. Extends
 * {@link AzRendererConfig} specifically for handling {@link ItemStack}. Provides additional settings specific to item
 * rendering, such as GUI lighting and custom offsets.
 */
public class AzItemRendererConfig extends AzRendererConfig<ItemStack> {

    private final boolean useEntityGuiLighting;

    private final boolean useNewOffset;

    private AzItemRendererConfig(
        Supplier<AzAnimator<ItemStack>> animatorProvider,
        Function<ItemStack, ResourceLocation> modelLocationProvider,
        List<AzRenderLayer<ItemStack>> renderLayers,
        Function<ItemStack, ResourceLocation> textureLocationProvider,
        float scaleHeight,
        float scaleWidth,
        boolean useEntityGuiLighting,
        boolean useNewOffset
    ) {
        super(animatorProvider, modelLocationProvider, renderLayers, textureLocationProvider, scaleHeight, scaleWidth);
        this.useEntityGuiLighting = useEntityGuiLighting;
        this.useNewOffset = useNewOffset;
    }

    public boolean useEntityGuiLighting() {
        return useEntityGuiLighting;
    }

    public boolean useNewOffset() {
        return useNewOffset;
    }

    public static Builder builder(
        ResourceLocation modelLocation,
        ResourceLocation textureLocation
    ) {
        return new Builder($ -> modelLocation, $ -> textureLocation);
    }

    public static Builder builder(
        Function<ItemStack, ResourceLocation> modelLocationProvider,
        Function<ItemStack, ResourceLocation> textureLocationProvider
    ) {
        return new Builder(modelLocationProvider, textureLocationProvider);
    }

    public static class Builder extends AzRendererConfig.Builder<ItemStack> {

        private boolean useEntityGuiLighting;

        private boolean useNewOffset;

        protected Builder(
            Function<ItemStack, ResourceLocation> modelLocationProvider,
            Function<ItemStack, ResourceLocation> textureLocationProvider
        ) {
            super(modelLocationProvider, textureLocationProvider);
            this.useEntityGuiLighting = false;
            this.useNewOffset = false;
        }

        @Override
        public Builder addRenderLayer(AzRenderLayer<ItemStack> renderLayer) {
            return (Builder) super.addRenderLayer(renderLayer);
        }

        @Override
        public Builder setAnimatorProvider(Supplier<AzAnimator<ItemStack>> animatorProvider) {
            return (Builder) super.setAnimatorProvider(animatorProvider);
        }

        public Builder useEntityGuiLighting() {
            this.useEntityGuiLighting = true;
            return this;
        }

        /**
         * @param useNewOffset Determines whether to apply the y offset for a model due to the change in BlockBench
         *                     4.11.
         */
        public AzRendererConfig.Builder<ItemStack> useNewOffset(boolean useNewOffset) {
            this.useNewOffset = useNewOffset;
            return this;
        }

        public AzItemRendererConfig build() {
            AzRendererConfig<ItemStack> baseConfig = super.build();

            return new AzItemRendererConfig(
                baseConfig::createAnimator,
                baseConfig::modelLocation,
                baseConfig.renderLayers(),
                baseConfig::textureLocation,
                baseConfig.scaleHeight(),
                baseConfig.scaleWidth(),
                useEntityGuiLighting,
                useNewOffset
            );
        }
    }
}
