package mod.azure.azurelib.render.layer;

import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;

public class AzAutoGlowingLayer<T> implements AzRenderLayer<T> {

    @Override
    public void preRender(AzRendererPipelineContext<T> context) {}

    @Override
    public void render(AzRendererPipelineContext<T> context) {
        AzRendererPipeline<T> renderPipeline = context.rendererPipeline();
        int prevPackedLight = context.packedLight();

        context.setPackedLight(0xF00000);
        renderPipeline.reRender(context);
        context.setPackedLight(prevPackedLight);
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<T> context, AzBone bone) {}
}