package mod.azure.azurelib.render.layer;

import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.textures.AzAutoGlowingTexture;
import mod.azure.azurelib.render.textures.utils.EmissiveUtil;
import net.minecraft.client.Minecraft;

public class AzAutoGlowingLayer<T> implements AzRenderLayer<T> {

    @Override
    public void preRender(AzRendererPipelineContext<T> context) {}

    @Override
    public void render(AzRendererPipelineContext<T> context) {
        AzRendererPipeline<T> renderPipeline = context.rendererPipeline();
        EmissiveUtil.preEmissiveTextureRendering();

        Minecraft.getMinecraft().renderEngine.bindTexture(AzAutoGlowingTexture.get(context.rendererPipeline().config().textureLocation(context.animatable())));

        EmissiveUtil.postEmissiveTextureRendering();
    }

    @Override
    public void renderForBone(AzRendererPipelineContext<T> context, AzBone bone) {}
}