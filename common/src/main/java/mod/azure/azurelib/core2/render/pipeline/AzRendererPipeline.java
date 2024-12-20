package mod.azure.azurelib.core2.render.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.cache.object.GeoCube;
import mod.azure.azurelib.common.internal.common.cache.object.GeoQuad;
import mod.azure.azurelib.common.internal.common.cache.object.GeoVertex;
import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

public abstract class AzRendererPipeline<T> {

    private final AzRendererPipelineContext<T> context;

    private final Matrix4f poseStateCache = new Matrix4f();

    private final Vector3f normalCache = new Vector3f();

    protected AzRendererPipeline() {
        this.context = createContext(this);
    }

    protected abstract AzRendererPipelineContext<T> createContext(AzRendererPipeline<T> rendererPipeline);

    public abstract @NotNull ResourceLocation getTextureLocation(@NotNull T animatable);

    /**
     * Returns the list of registered {@link AzRenderLayer GeoRenderLayers} for this renderer
     */
    protected abstract List<AzRenderLayer<T>> getRenderLayers();

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
        preApplyRenderLayers(context);
        actuallyRender(context, false);
        applyRenderLayers(context);
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
        actuallyRender(context, true);
        postRender(context, true);

        poseStack.popPose();
    }

    /**
     * The actual render method that sub-type renderers should override to handle their specific rendering tasks.<br>
     */
    protected void actuallyRender(AzRendererPipelineContext<T> context, boolean isReRender) {
        var animatable = context.animatable();
        var model = context.bakedModel();

        updateAnimatedTextureFrame(animatable);

        for (var bone : model.getTopLevelBones()) {
            renderRecursively(context, bone, isReRender);
        }
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    protected void renderRecursively(AzRendererPipelineContext<T> context, AzBone bone, boolean isReRender) {
        var poseStack = context.poseStack();

        poseStack.pushPose();
        RenderUtils.prepMatrixForBone(poseStack, bone);
        renderCubesOfBone(context, bone);

        if (!isReRender) {
            applyRenderLayersForBone(context, bone);
        }

        renderChildBones(context, bone, isReRender);
        poseStack.popPose();
    }

    /**
     * Renders the {@link GeoCube GeoCubes} associated with a given {@link AzBone}
     */
    protected void renderCubesOfBone(AzRendererPipelineContext<T> context, AzBone bone) {
        if (bone.isHidden()) {
            return;
        }

        var poseStack = context.poseStack();

        for (var cube : bone.getCubes()) {
            poseStack.pushPose();

            renderCube(context, cube);

            poseStack.popPose();
        }
    }

    /**
     * Render the child bones of a given {@link AzBone}.<br>
     * Note that this does not render the bone itself. That should be done through
     * {@link AzRendererPipeline#renderCubesOfBone} separately
     */
    protected void renderChildBones(AzRendererPipelineContext<T> context, AzBone bone, boolean isReRender) {
        if (bone.isHidingChildren())
            return;

        for (var childBone : bone.getChildBones()) {
            renderRecursively(context, childBone, isReRender);
        }
    }

    /**
     * Renders an individual {@link GeoCube}.<br>
     * This tends to be called recursively from something like {@link AzRendererPipeline#renderCubesOfBone}
     */
    protected void renderCube(AzRendererPipelineContext<T> context, GeoCube cube) {
        var poseStack = context.poseStack();

        RenderUtils.translateToPivotPoint(poseStack, cube);
        RenderUtils.rotateMatrixAroundCube(poseStack, cube);
        RenderUtils.translateAwayFromPivotPoint(poseStack, cube);

        var normalisedPoseState = poseStack.last().normal();
        var poseState = poseStateCache.set(poseStack.last().pose());

        for (var quad : cube.quads()) {
            if (quad == null) {
                continue;
            }

            normalCache.set(quad.normal());
            var normal = normalisedPoseState.transform(normalCache);

            RenderUtils.fixInvertedFlatCube(cube, normal);
            createVerticesOfQuad(context, quad, poseState, normal);
        }
    }

    private final Vector4f poseStateTransformCache = new Vector4f();

    /**
     * Applies the {@link GeoQuad Quad's} {@link GeoVertex vertices} to the given {@link VertexConsumer buffer} for
     * rendering
     */
    protected void createVerticesOfQuad(
        AzRendererPipelineContext<T> context,
        GeoQuad quad,
        Matrix4f poseState,
        Vector3f normal
    ) {
        var buffer = context.vertexConsumer();
        var color = context.renderColor();
        var packedOverlay = context.packedOverlay();
        var packedLight = context.packedLight();

        for (var vertex : quad.vertices()) {
            var position = vertex.position();
            poseStateTransformCache.set(position.x(), position.y(), position.z(), 1.0f);
            var vector4f = poseState.transform(poseStateTransformCache);

            buffer.addVertex(
                vector4f.x(),
                vector4f.y(),
                vector4f.z(),
                color,
                vertex.texU(),
                vertex.texV(),
                packedOverlay,
                packedLight,
                normal.x(),
                normal.y(),
                normal.z()
            );
        }
    }

    /**
     * Calls back to the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer for their
     * {@link mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer#preRender pre-render} actions.
     */
    protected void preApplyRenderLayers(AzRendererPipelineContext<T> context) {
        for (var renderLayer : getRenderLayers()) {
            renderLayer.preRender(context);
        }
    }

    /**
     * Calls back to the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer for their
     * {@link mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer#renderForBone per-bone} render actions.
     */
    protected void applyRenderLayersForBone(AzRendererPipelineContext<T> context, AzBone bone) {
        for (var renderLayer : getRenderLayers()) {
            renderLayer.renderForBone(context, bone);
        }
    }

    /**
     * Render the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer
     */
    protected void applyRenderLayers(AzRendererPipelineContext<T> context) {
        for (var renderLayer : getRenderLayers()) {
            renderLayer.render(context);
        }
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    protected void preRender(AzRendererPipelineContext<T> context, boolean isReRender) {}

    /**
     * Called after rendering the model to buffer. Post-render modifications should be performed here.<br>
     * {@link PoseStack} transformations will be unused and lost once this method ends
     */
    protected void postRender(AzRendererPipelineContext<T> context, boolean isReRender) {}

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
     * part of a {@link mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer} or external render call.<br>
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

    public AzRendererPipelineContext<T> getContext() {
        return context;
    }
}
