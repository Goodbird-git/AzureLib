package mod.azure.azurelib.core2.render.layer;

import mod.azure.azurelib.common.internal.common.cache.texture.AutoGlowingTexture;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;

public class AzAutoGlowingLayer extends AzRenderLayer {

    @Override
    public void preRender(AzRendererPipelineContext context) {}

    @Override
    public void render(AzRendererPipelineContext context) {
        var renderPipeline = context.rendererPipeline();
        context.setRenderType(AutoGlowingTexture.getRenderType(renderPipeline.getTextureLocation(context.animatable())));
        context.setPackedLight(15728640);
        if (context.renderType() != null) {
            context.rendererPipeline().reRender(context);
        }
    }

    @Override
    public void renderForBone(AzRendererPipelineContext context, AzBone bone) {}
}
