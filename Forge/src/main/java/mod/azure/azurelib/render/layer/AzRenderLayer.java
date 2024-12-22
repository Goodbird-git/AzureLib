package mod.azure.azurelib.render.layer;

import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;

/**
 * Render layer base class for rendering additional layers of effects or textures over an existing model at runtime.<br>
 * Contains the base boilerplate and helper code for various render layer features
 */
public interface AzRenderLayer<T> {

    /**
     * This method is called by the {@link AzRendererPipeline} before rendering, immediately after
     * {@link AzRendererPipeline#preRender} has been called.<br>
     * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
     */
    void preRender(AzRendererPipelineContext<T> context);

    /**
     * This is the method that is actually called by the render for your render layer to function.<br>
     * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags.
     */
    void render(AzRendererPipelineContext<T> context);

    /**
     * This method is called by the {@link AzRendererPipeline} for each bone being rendered.<br>
     * This is a more expensive call, particularly if being used to render something on a different buffer.<br>
     * It does however have the benefit of having the matrix translations and other transformations already applied from
     * render-time.<br>
     * It's recommended to avoid using this unless necessary.<br>
     * <br>
     * The {@link AzBone} in question has already been rendered by this stage.<br>
     * <br>
     */
    void renderForBone(AzRendererPipelineContext<T> context, AzBone bone);
}
