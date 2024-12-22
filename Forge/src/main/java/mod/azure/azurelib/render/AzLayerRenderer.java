package mod.azure.azurelib.render;

import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.layer.AzRenderLayer;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Provides a mechanism to manage and apply multiple render layers for a given animatable context. This class acts as a
 * wrapper for handling collections of render layers and delegates the rendering responsibilities to the individual
 * layers.
 *
 * @param <T> The type of animatable entity or object the render layers apply to.
 */
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
        for (AzRenderLayer<T> renderLayer : renderLayerSupplier.get()) {
            renderLayer.preRender(context);
        }
    }

    /**
     * Calls back to the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer for their
     * {@link AzRenderLayer#renderForBone per-bone} render actions.
     */
    public void applyRenderLayersForBone(AzRendererPipelineContext<T> context, AzBone bone) {
        for (AzRenderLayer<T> renderLayer : renderLayerSupplier.get()) {
            renderLayer.renderForBone(context, bone);
        }
    }

    /**
     * Render the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer
     */
    protected void applyRenderLayers(AzRendererPipelineContext<T> context) {
        for (AzRenderLayer<T> renderLayer : renderLayerSupplier.get()) {
            renderLayer.render(context);
        }
    }
}
