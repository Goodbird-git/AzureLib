package mod.azure.azurelib.core2.render.pipeline.entity;

import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.function.Supplier;

import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.AzLayerRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;

public class AzEntityLayerRenderer<T extends Entity> extends AzLayerRenderer<T> {

    public AzEntityLayerRenderer(Supplier<Collection<AzRenderLayer<T>>> renderLayerSupplier) {
        super(renderLayerSupplier);
    }

    /**
     * Render the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer
     */
    @Override
    public void applyRenderLayers(AzRendererPipelineContext<T> context) {
        var animatable = context.animatable();

        if (!animatable.isSpectator()) {
            super.applyRenderLayers(context);
        }
    }
}
