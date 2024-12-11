package mod.azure.azurelib.core2.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipeline;

/**
 * Render layer base class for rendering additional layers of effects or textures over an existing model at runtime.<br>
 * Contains the base boilerplate and helper code for various render layer features
 */
public abstract class AzRenderLayer<T> {

    public AzRenderLayer() {}

    /**
     * This method is called by the {@link AzRendererPipeline} before rendering, immediately after
     * {@link AzRendererPipeline#preRender} has been called.<br>
     * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
     */
    public abstract void preRender(
        PoseStack poseStack,
        T animatable,
        AzBakedModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    );

    /**
     * This is the method that is actually called by the render for your render layer to function.<br>
     * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags.
     */
    public abstract void render(
        PoseStack poseStack,
        T animatable,
        AzBakedModel bakedModel,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    );

    /**
     * This method is called by the {@link AzRendererPipeline} for each bone being rendered.<br>
     * This is a more expensive call, particularly if being used to render something on a different buffer.<br>
     * It does however have the benefit of having the matrix translations and other transformations already applied from
     * render-time.<br>
     * It's recommended to avoid using this unless necessary.<br>
     * <br>
     * The {@link AzBone} in question has already been rendered by this stage.<br>
     * <br>
     * If you <i>do</i> use it, and you render something that changes the {@link VertexConsumer buffer}, you need to
     * reset it back to the previous buffer using {@link MultiBufferSource#getBuffer} before ending the method
     */
    public abstract void renderForBone(
        PoseStack poseStack,
        T animatable,
        AzBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    );
}
