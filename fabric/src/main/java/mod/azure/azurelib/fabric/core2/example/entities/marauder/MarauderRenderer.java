package mod.azure.azurelib.fabric.core2.example.entities.marauder;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;
import mod.azure.azurelib.core2.render.layer.AzAutoGlowingLayer;

public class MarauderRenderer extends AzEntityRenderer<MarauderEntity> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/entity/marauder.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/entity/marauder.png");

    public MarauderRenderer(EntityRendererProvider.Context context) {
        super(context);
        addRenderLayer(new AzAutoGlowingLayer<>());
        /**
         * TODO: Port this to new system, as it currently requries this be a GeoEntity and use the GeoLayers as well
         */
        // addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    protected @Nullable AzEntityAnimator<MarauderEntity> createAnimator() {
        return new MarauderAnimator();
    }

    @Override
    protected @NotNull ResourceLocation getModelLocation(MarauderEntity drone) {
        return MODEL;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MarauderEntity drone) {
        return TEXTURE;
    }
}
