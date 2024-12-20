package mod.azure.azurelib.core2.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

public abstract class AzRendererPipeline<T> implements AzPhasedRenderer<T> {

    protected final AzRendererConfig<T> config;

    private final AzRendererPipelineContext<T> context;

    private final AzLayerRenderer<T> layerRenderer;

    private final AzModelRenderer<T> modelRenderer;

    protected AzRendererPipeline(AzRendererConfig<T> config) {
        this.config = config;
        this.context = createContext(this);
        this.layerRenderer = createLayerRenderer(config);
        this.modelRenderer = createModelRenderer(layerRenderer);
    }

    protected abstract AzRendererPipelineContext<T> createContext(AzRendererPipeline<T> rendererPipeline);

    protected abstract AzModelRenderer<T> createModelRenderer(AzLayerRenderer<T> layerRenderer);

    protected abstract AzLayerRenderer<T> createLayerRenderer(AzRendererConfig<T> config);

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this
     * GeoRenderer.<br>
     * This should only be called immediately prior to rendering, and only
     *
     * @see AnimatableTexture#setAndUpdate
     */
    protected abstract void updateAnimatedTextureFrame(T animatable);

    /**
     * Initial access point for rendering. It all begins here.<br>
     * All AzureLib renderers should immediately defer their respective default {@code render} calls to this, for
     * consistent handling
     */
    public void render(
        PoseStack poseStack,
        AzBakedModel model,
        T animatable,
        MultiBufferSource bufferSource,
        @Nullable RenderType renderType,
        @Nullable VertexConsumer buffer,
        float yaw,
        float partialTick,
        int packedLight
    ) {
        context.populate(animatable, model, bufferSource, packedLight, partialTick, poseStack, renderType, buffer);

        poseStack.pushPose();

        preRender(context, false);

        // TODO:
        // if (firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
        layerRenderer.preApplyRenderLayers(context);
        modelRenderer.render(context, false);
        layerRenderer.applyRenderLayers(context);
        postRender(context, false);
        // TODO:
        // firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
        // }

        poseStack.popPose();

        renderFinal(context);
        doPostRenderCleanup();
    }

    /**
     * Re-renders the provided {@link AzBakedModel}.<br>
     * Usually you'd use this for rendering alternate {@link RenderType} layers or for sub-model rendering whilst inside
     * a {@link AzRenderLayer} or similar
     */
    public void reRender(AzRendererPipelineContext<T> context) {
        var poseStack = context.poseStack();

        poseStack.pushPose();

        preRender(context, true);
        modelRenderer.render(context, true);
        postRender(context, true);

        poseStack.popPose();
    }

    /**
     * Call after all other rendering work has taken place, including reverting the {@link PoseStack}'s state. This
     * method is <u>not</u> called in {@link AzRendererPipeline#reRender re-render}
     */
    protected void renderFinal(AzRendererPipelineContext<T> context) {}

    /**
     * Called after all render operations are completed and the render pass is considered functionally complete.
     * <p>
     * Use this method to clean up any leftover persistent objects stored during rendering or any other post-render
     * maintenance tasks as required
     */
    protected void doPostRenderCleanup() {}

    /**
     * Scales the {@link PoseStack} in preparation for rendering the model, excluding when re-rendering the model as
     * part of a {@link AzRenderLayer} or external render call.<br>
     * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child
     * entities)
     */
    protected void scaleModelForRender(
        AzRendererPipelineContext<T> context,
        float widthScale,
        float heightScale,
        boolean isReRender
    ) {
        if (!isReRender && (widthScale != 1 || heightScale != 1)) {
            var poseStack = context.poseStack();
            poseStack.scale(widthScale, heightScale, widthScale);
        }
    }

    public AzRendererConfig<T> config() {
        return config;
    }

    public AzRendererPipelineContext<T> context() {
        return context;
    }
}
