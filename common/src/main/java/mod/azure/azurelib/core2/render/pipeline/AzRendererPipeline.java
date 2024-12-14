package mod.azure.azurelib.core2.render.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.cache.object.GeoCube;
import mod.azure.azurelib.common.internal.common.cache.object.GeoQuad;
import mod.azure.azurelib.common.internal.common.cache.object.GeoVertex;
import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core.object.Color;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;

public abstract class AzRendererPipeline<T> {

    protected abstract @NotNull ResourceLocation getTextureLocation(@NotNull T animatable);

    /**
     * Gets the {@link RenderType} to render the given animatable with.<br>
     * Uses the {@link RenderType#entityCutoutNoCull} {@code RenderType} by default.<br>
     * Override this to change the way a model will render (such as translucent models, etc)
     */
    public abstract RenderType getDefaultRenderType(
        T animatable,
        ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource,
        float partialTick
    );

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this
     * GeoRenderer.<br>
     * This should only be called immediately prior to rendering, and only
     *
     * @see AnimatableTexture#setAndUpdate
     */
    protected abstract void updateAnimatedTextureFrame(T animatable);

    /**
     * Create and fire the relevant {@code CompileLayers} event hook for this renderer
     */
    protected abstract void fireCompileRenderLayersEvent();

    /**
     * Create and fire the relevant {@code Pre-Render} event hook for this renderer.<br>
     *
     * @return Whether the renderer should proceed based on the cancellation state of the event
     */
    protected abstract boolean firePreRenderEvent(
        PoseStack poseStack,
        AzBakedModel model,
        MultiBufferSource bufferSource,
        float partialTick,
        int packedLight
    );

    /**
     * Create and fire the relevant {@code Post-Render} event hook for this renderer
     */
    protected abstract void firePostRenderEvent(
        PoseStack poseStack,
        AzBakedModel model,
        MultiBufferSource bufferSource,
        float partialTick,
        int packedLight
    );

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
        poseStack.pushPose();

        var renderColor = getRenderColor(animatable, partialTick, packedLight).argbInt();
        var packedOverlay = getPackedOverlay(animatable, 0, partialTick);

        if (renderType == null) {
            renderType = getDefaultRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);
        }

        if (buffer == null) {
            buffer = bufferSource.getBuffer(renderType);
        }

        preRender(
            poseStack,
            animatable,
            model,
            bufferSource,
            buffer,
            false,
            partialTick,
            packedLight,
            packedOverlay,
            renderColor
        );

        if (firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
            preApplyRenderLayers(
                poseStack,
                animatable,
                model,
                renderType,
                bufferSource,
                buffer,
                packedLight,
                packedLight,
                packedOverlay
            );
            actuallyRender(
                poseStack,
                animatable,
                model,
                renderType,
                bufferSource,
                buffer,
                false,
                partialTick,
                packedLight,
                packedOverlay,
                renderColor
            );
            applyRenderLayers(
                poseStack,
                animatable,
                model,
                renderType,
                bufferSource,
                buffer,
                partialTick,
                packedLight,
                packedOverlay
            );
            postRender(
                poseStack,
                animatable,
                model,
                bufferSource,
                buffer,
                false,
                partialTick,
                packedLight,
                packedOverlay,
                renderColor
            );
            firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
        }

        poseStack.popPose();

        renderFinal(
            poseStack,
            animatable,
            model,
            bufferSource,
            buffer,
            partialTick,
            packedLight,
            packedOverlay,
            renderColor
        );
        doPostRenderCleanup();
    }

    /**
     * Re-renders the provided {@link AzBakedModel}.<br>
     * Usually you'd use this for rendering alternate {@link RenderType} layers or for sub-model rendering whilst inside
     * a {@link AzRenderLayer} or similar
     */
    protected void reRender(
        AzBakedModel model,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        T animatable,
        RenderType renderType,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        poseStack.pushPose();
        preRender(
            poseStack,
            animatable,
            model,
            bufferSource,
            buffer,
            true,
            partialTick,
            packedLight,
            packedOverlay,
            colour
        );
        actuallyRender(
            poseStack,
            animatable,
            model,
            renderType,
            bufferSource,
            buffer,
            true,
            partialTick,
            packedLight,
            packedOverlay,
            colour
        );
        postRender(
            poseStack,
            animatable,
            model,
            bufferSource,
            buffer,
            true,
            partialTick,
            packedLight,
            packedOverlay,
            colour
        );
        poseStack.popPose();
    }

    /**
     * The actual render method that sub-type renderers should override to handle their specific rendering tasks.<br>
     */
    protected void actuallyRender(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        updateAnimatedTextureFrame(animatable);

        for (var bone : model.getTopLevelBones()) {
            renderRecursively(
                poseStack,
                animatable,
                bone,
                renderType,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight,
                packedOverlay,
                colour
            );
        }
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    protected void renderRecursively(
        PoseStack poseStack,
        T animatable,
        AzBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        poseStack.pushPose();
        RenderUtils.prepMatrixForBone(poseStack, bone);
        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);

        if (!isReRender) {
            applyRenderLayersForBone(
                poseStack,
                animatable,
                bone,
                renderType,
                bufferSource,
                buffer,
                partialTick,
                packedLight,
                packedOverlay
            );
        }

        renderChildBones(
            poseStack,
            animatable,
            bone,
            renderType,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            colour
        );
        poseStack.popPose();
    }

    /**
     * Renders the {@link GeoCube GeoCubes} associated with a given {@link AzBone}
     */
    protected void renderCubesOfBone(
        PoseStack poseStack,
        AzBone bone,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        if (bone.isHidden())
            return;

        for (GeoCube cube : bone.getCubes()) {
            poseStack.pushPose();
            renderCube(poseStack, cube, buffer, packedLight, packedOverlay, colour);
            poseStack.popPose();
        }
    }

    /**
     * Render the child bones of a given {@link AzBone}.<br>
     * Note that this does not render the bone itself. That should be done through
     * {@link AzRendererPipeline#renderCubesOfBone} separately
     */
    protected void renderChildBones(
        PoseStack poseStack,
        T animatable,
        AzBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        if (bone.isHidingChildren())
            return;

        for (var childBone : bone.getChildBones()) {
            renderRecursively(
                poseStack,
                animatable,
                childBone,
                renderType,
                bufferSource,
                buffer,
                isReRender,
                partialTick,
                packedLight,
                packedOverlay,
                colour
            );
        }
    }

    /**
     * Renders an individual {@link GeoCube}.<br>
     * This tends to be called recursively from something like {@link AzRendererPipeline#renderCubesOfBone}
     */
    protected void renderCube(
        PoseStack poseStack,
        GeoCube cube,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        RenderUtils.translateToPivotPoint(poseStack, cube);
        RenderUtils.rotateMatrixAroundCube(poseStack, cube);
        RenderUtils.translateAwayFromPivotPoint(poseStack, cube);

        Matrix3f normalisedPoseState = poseStack.last().normal();
        Matrix4f poseState = new Matrix4f(poseStack.last().pose());

        for (GeoQuad quad : cube.quads()) {
            if (quad == null)
                continue;

            // TODO: Optimize
            Vector3f normal = normalisedPoseState.transform(new Vector3f(quad.normal()));

            RenderUtils.fixInvertedFlatCube(cube, normal);
            createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, colour);
        }
    }

    /**
     * Applies the {@link GeoQuad Quad's} {@link GeoVertex vertices} to the given {@link VertexConsumer buffer} for
     * rendering
     */
    protected void createVerticesOfQuad(
        GeoQuad quad,
        Matrix4f poseState,
        Vector3f normal,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        for (var vertex : quad.vertices()) {
            var position = vertex.position();
            // TODO: Optimize
            var vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0f));

            buffer.addVertex(
                vector4f.x(),
                vector4f.y(),
                vector4f.z(),
                colour,
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
    protected void preApplyRenderLayers(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        for (var renderLayer : getRenderLayers()) {
            renderLayer.preRender(
                poseStack,
                animatable,
                model,
                renderType,
                bufferSource,
                buffer,
                partialTick,
                packedLight,
                packedOverlay
            );
        }
    }

    /**
     * Calls back to the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer for their
     * {@link mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer#renderForBone per-bone} render actions.
     */
    protected void applyRenderLayersForBone(
        PoseStack poseStack,
        T animatable,
        AzBone bone,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        for (var renderLayer : getRenderLayers()) {
            renderLayer.renderForBone(
                poseStack,
                animatable,
                bone,
                renderType,
                bufferSource,
                buffer,
                partialTick,
                packedLight,
                packedOverlay
            );
        }
    }

    /**
     * Render the various {@link AzRenderLayer RenderLayers} that have been registered to this renderer
     */
    protected void applyRenderLayers(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        for (var renderLayer : getRenderLayers()) {
            renderLayer.render(
                poseStack,
                animatable,
                model,
                renderType,
                bufferSource,
                buffer,
                partialTick,
                packedLight,
                packedOverlay
            );
        }
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    protected void preRender(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        @Nullable MultiBufferSource bufferSource,
        @Nullable VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {}

    /**
     * Called after rendering the model to buffer. Post-render modifications should be performed here.<br>
     * {@link PoseStack} transformations will be unused and lost once this method ends
     */
    protected void postRender(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {}

    /**
     * Call after all other rendering work has taken place, including reverting the {@link PoseStack}'s state. This
     * method is <u>not</u> called in {@link AzRendererPipeline#reRender re-render}
     */
    protected void renderFinal(
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {}

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
        float widthScale,
        float heightScale,
        PoseStack poseStack,
        T animatable,
        AzBakedModel model,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay
    ) {
        if (!isReRender && (widthScale != 1 || heightScale != 1)) {
            poseStack.scale(widthScale, heightScale, widthScale);
        }
    }

    /**
     * Gets a tint-applying color to render the given animatable with.<br>
     * Returns {@link Color#WHITE} by default
     */
    protected Color getRenderColor(T animatable, float partialTick, int packedLight) {
        return Color.WHITE;
    }

    /**
     * Gets a packed overlay coordinate pair for rendering.<br>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the
     * {@link net.minecraft.world.entity.monster.Creeper} white tint when exploding.
     */
    protected int getPackedOverlay(T animatable, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    /**
     * Returns the list of registered {@link AzRenderLayer GeoRenderLayers} for this renderer
     */
    protected List<AzRenderLayer<T>> getRenderLayers() {
        return List.of();
    }
}
