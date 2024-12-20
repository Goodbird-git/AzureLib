package mod.azure.azurelib.core2.render;

import net.minecraft.world.item.ItemStack;

import java.util.List;

import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

public class AzItemRendererConfig extends AzRendererConfig<ItemStack> {

    private final boolean useEntityGuiLighting;

    private final boolean useNewOffset;

    private AzItemRendererConfig(
        List<AzRenderLayer<ItemStack>> renderLayers,
        float scaleHeight,
        float scaleWidth,
        boolean useEntityGuiLighting,
        boolean useNewOffset
    ) {
        super(renderLayers, scaleHeight, scaleWidth);
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

    public static class Builder extends AzRendererConfig.Builder<ItemStack> {

        private boolean useEntityGuiLighting;

        private boolean useNewOffset;

        protected Builder() {
            super();
            this.useEntityGuiLighting = false;
            this.useNewOffset = false;
        }

        @Override
        public Builder addRenderLayer(AzRenderLayer<ItemStack> renderLayer) {
            return (Builder) super.addRenderLayer(renderLayer);
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
            var baseConfig = super.build();

            return new AzItemRendererConfig(
                baseConfig.renderLayers(),
                baseConfig.scaleHeight(),
                baseConfig.scaleWidth(),
                useEntityGuiLighting,
                useNewOffset
            );
        }
    }
}
