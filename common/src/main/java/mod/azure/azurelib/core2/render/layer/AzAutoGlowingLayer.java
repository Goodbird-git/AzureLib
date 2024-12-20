package mod.azure.azurelib.core2.render.layer;

import mod.azure.azurelib.common.internal.common.cache.texture.AutoGlowingTexture;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;

public class AzAutoGlowingLayer<T> extends AzRenderLayer<T> {

    @Override
    public void preRender(AzRendererPipelineContext<T> context) {}

    @Override
    public void render(AzRendererPipelineContext<T> context) {
        var animatable = context.animatable();
        var renderPipeline = context.rendererPipeline();
        var textureLocation = renderPipeline.getTextureLocation(animatable);
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
