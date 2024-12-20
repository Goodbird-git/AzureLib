package mod.azure.azurelib.core2.render.layer;

import net.minecraft.client.renderer.RenderType;

import mod.azure.azurelib.common.internal.common.cache.texture.AutoGlowingTexture;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.AzRendererPipelineContext;

/**
 * A {@link AzRenderLayer} dedicated to rendering the auto-generated glow layer functionality provided by AzureLib. This
 * utilizes texture files with the <i>_glowing</i> suffix to create glowing effects for models.
 */
public class AzAutoGlowingLayer<T> implements AzRenderLayer<T> {

    @Override
    public void preRender(AzRendererPipelineContext<T> context) {}

    /**
     * Handles the main rendering logic for the animatable object in the pipeline context. This includes switching to a
     * custom {@link RenderType} for glowing textures and rendering the object using the pipeline's re-render mechanism.
     * <p>
     * The rendering context's state is modified temporarily to apply a custom render type and packed light. After
     * rendering, the context is restored to its original state for consistency.
     * </p>
     *
     * @param context the rendering pipeline context, containing the animatable object and rendering state
     */
    @Override
    public void render(AzRendererPipelineContext<T> context) {
        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();
        var textureLocation = renderPipeline.config().textureLocation(animatable);
        var renderType = AutoGlowingTexture.getRenderType(textureLocation);

        if (context.renderType() != null) {
            var prevRenderType = context.renderType();
            var prevPackedLight = context.packedLight();
            var prevVertexConsumer = context.vertexConsumer();

            context.setRenderType(renderType);
            context.setPackedLight(0xF00000);
            context.setVertexConsumer(context.multiBufferSource().getBuffer(renderType));

            renderPipeline.reRender(context);

            // Restore context for sanity
            // TODO: Should probably cache the context as a whole somewhere and then restore it (a "previous" context).
            context.setRenderType(prevRenderType);
            context.setPackedLight(prevPackedLight);
            context.setVertexConsumer(prevVertexConsumer);
        }
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<T> context, AzBone bone) {}
}
