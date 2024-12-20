package mod.azure.azurelib.core2.render.pipeline;

import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

import java.util.Collection;
import java.util.function.Supplier;

public class AzLayerRenderer<T> {

    private final Supplier<Collection<AzRenderLayer<T>>> renderLayerSupplier;

    public AzLayerRenderer(Supplier<Collection<AzRenderLayer<T>>> renderLayerSupplier) {
        this.renderLayerSupplier = renderLayerSupplier;
    }

    /**
     * Calls back to the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer for their
     * {@link AzRenderLayer#preRender pre-render} actions.
     */
    protected void preApplyRenderLayers(AzRendererPipelineContext<T> context) {
        for (var renderLayer : renderLayerSupplier.get()) {
            renderLayer.preRender(context);
        }
    }

    /**
     * Calls back to the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer for their
     * {@link AzRenderLayer#renderForBone per-bone} render actions.
     */
    public void applyRenderLayersForBone(AzRendererPipelineContext<T> context, AzBone bone) {
        for (var renderLayer : renderLayerSupplier.get()) {
            renderLayer.renderForBone(context, bone);
        }
    }

    /**
     * Render the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer
     */
    protected void applyRenderLayers(AzRendererPipelineContext<T> context) {
        for (var renderLayer : renderLayerSupplier.get()) {
            renderLayer.render(context);
        }
    }
}
