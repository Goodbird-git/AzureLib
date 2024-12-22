package mod.azure.azurelib.render.armor;

import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.render.AzRendererConfig;
import mod.azure.azurelib.render.armor.bone.AzArmorBoneProvider;
import mod.azure.azurelib.render.armor.bone.AzDefaultArmorBoneProvider;
import mod.azure.azurelib.render.layer.AzRenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AzArmorRendererConfig extends AzRendererConfig<ItemStack> {

    private final AzArmorBoneProvider boneProvider;

    private AzArmorRendererConfig(
        Supplier<AzAnimator<ItemStack>> animatorProvider,
        AzArmorBoneProvider boneProvider,
        Function<ItemStack, ResourceLocation> modelLocationProvider,
        List<AzRenderLayer<ItemStack>> renderLayers,
        Function<ItemStack, ResourceLocation> textureLocationProvider,
        float scaleHeight,
        float scaleWidth
    ) {
        super(animatorProvider, modelLocationProvider, renderLayers, textureLocationProvider, scaleHeight, scaleWidth);
        this.boneProvider = boneProvider;
    }

    public AzArmorBoneProvider boneProvider() {
        return boneProvider;
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

        private AzArmorBoneProvider boneProvider;

        protected Builder(
            Function<ItemStack, ResourceLocation> modelLocationProvider,
            Function<ItemStack, ResourceLocation> textureLocationProvider
        ) {
            super(modelLocationProvider, textureLocationProvider);
            this.boneProvider = new AzDefaultArmorBoneProvider();
        }

        @Override
        public Builder addRenderLayer(AzRenderLayer<ItemStack> renderLayer) {
            return (Builder) super.addRenderLayer(renderLayer);
        }

        @Override
        public Builder setAnimatorProvider(Supplier<AzAnimator<ItemStack>> animatorProvider) {
            return (Builder) super.setAnimatorProvider(animatorProvider);
        }

        public Builder setBoneProvider(AzArmorBoneProvider boneProvider) {
            this.boneProvider = boneProvider;
            return this;
        }

        public AzArmorRendererConfig build() {
            AzRendererConfig<ItemStack> baseConfig = super.build();

            return new AzArmorRendererConfig(
                baseConfig::createAnimator,
                boneProvider,
                baseConfig::modelLocation,
                baseConfig.renderLayers(),
                baseConfig::textureLocation,
                baseConfig.scaleHeight(),
                baseConfig.scaleWidth()
            );
        }
    }
}
