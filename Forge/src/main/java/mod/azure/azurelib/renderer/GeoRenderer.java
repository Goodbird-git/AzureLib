/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.renderer;

import mod.azure.azurelib.cache.object.*;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.object.Color;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base interface for all AzureLib renderers.<br>
 */
public interface GeoRenderer<T extends GeoAnimatable> {
    /**
     * Gets the model instance for this renderer
     */
    GeoModel<T> getGeoModel();

    /**
     * Gets the {@link GeoAnimatable} instance currently being rendered
     */
    T getAnimatable();

    /**
     * Gets the texture resource location to render for the given animatable
     */
    default ResourceLocation getTextureLocation(T animatable) {
        return getGeoModel().getTextureResource(animatable);
    }

    /**
     * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
     */
    default List<GeoRenderLayer<T>> getRenderLayers() {
        return new ArrayList<>();
    }

    /**
     * Override this to change the way a model will render (such as translucent models, etc)
     */
    default void getRenderType(T animatable, ResourceLocation texture, float partialTick) {

    }

    /**
     * Gets a tint-applying color to render the given animatable with.<br>
     * Returns {@link Color#WHITE} by default
     */
    default Color getRenderColor(T animatable, float partialTick, int packedLight) {
        return Color.WHITE;
    }

    /**
     * Gets a packed overlay coordinate pair for rendering.<br>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the {@link EntityCreeper} white tint when exploding.
     */
    @Deprecated()
    default int getPackedOverlay(T animatable, float u) {
        return OverlayTexture.NO_OVERLAY;
    }

    /**
     * Gets a packed overlay coordinate pair for rendering.<br>
     * Mostly just used for the red tint when an entity is hurt, but can be used for other things like the {@link EntityCreeper} white tint when exploding.
     */
    default int getPackedOverlay(T animatable, float u, float partialTick) {
        return getPackedOverlay(animatable, u);
    }

    /**
     * Gets the id that represents the current animatable's instance for animation purposes. This is mostly useful for things like items, which have a single registered instance for all objects
     */
    default long getInstanceId(T animatable) {
        return animatable.hashCode();
    }

    /**
     * Determines the threshold value before the animatable should be considered moving for animation purposes.<br>
     * The default value and usage for this varies depending on the renderer.<br>
     * <ul>
     * <li>For entities, it represents the averaged lateral velocity of the object.</li>
     * <li>For {@link mod.azure.azurelib.animatable.GeoBlockEntity Tile Entities} and {@link mod.azure.azurelib.animatable.GeoItem Items}, it's currently unused</li>
     * </ul>
     * The lower the value, the more sensitive the {@link AnimationState#isMoving()} check will be.<br>
     * Particularly low values may have adverse effects however
     */
    default float getMotionAnimThreshold(T animatable) {
        return 0.015f;
    }

    /**
     * Initial access point for rendering. It all begins here.<br>
     * All AzureLib renderers should immediately defer their respective default {@code render} calls to this, for consistent handling
     */
    default void defaultRender(GlStateManager poseStack, T animatable, IRenderTypeBuffer bufferSource, @Nullable RenderType renderType, @Nullable IVertexBuilder buffer, float yaw, float partialTick, int packedLight) {
        poseStack.push();

        Color renderColor = getRenderColor(animatable, partialTick, packedLight);
        float red = renderColor.getRedFloat();
        float green = renderColor.getGreenFloat();
        float blue = renderColor.getBlueFloat();
        float alpha = renderColor.getAlphaFloat();
        int packedOverlay = getPackedOverlay(animatable, 0, partialTick);
        BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));

        if (renderType == null)
            renderType = getRenderType(animatable, getTextureLocation(animatable), bufferSource, partialTick);

        if (buffer == null)
            buffer = bufferSource.getBuffer(renderType);

        preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);

        preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, packedLight, packedLight,
                packedOverlay);
        actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight,
                packedOverlay, red, green, blue, alpha);
        applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight,
                packedOverlay);
        postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);

        poseStack.pop();
    }

    /**
     * Re-renders the provided {@link BakedGeoModel} using the existing {@link GeoRenderer}.<br>
     * Usually you'd use this for rendering alternate {@link RenderType} layers or for sub-model rendering whilst inside a {@link GeoRenderLayer} or similar
     */
    default void reRender(BakedGeoModel model, GlStateManager poseStack, IRenderTypeBuffer bufferSource, T animatable, RenderType renderType, IVertexBuilder buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.push();
        preRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
        actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight,
                packedOverlay, red, green, blue, alpha);
        postRender(poseStack, animatable, model, bufferSource, buffer, true, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
        poseStack.pop();
    }

    /**
     * The actual render method that sub-type renderers should override to handle their specific rendering tasks.<br>
     * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
     */
    default void actuallyRender(GlStateManager poseStack, T animatable, BakedGeoModel model, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        for (GeoBone group : model.topLevelBones()) {
            renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender, partialTick,
                    packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    /**
     * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#preRender pre-render} actions.
     */
    default void preApplyRenderLayers(GlStateManager poseStack, T animatable, BakedGeoModel model, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, float partialTick, int packedLight, int packedOverlay) {
        for (GeoRenderLayer<T> renderLayer : getRenderLayers()) {
            renderLayer.preRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick,
                    packedLight, packedOverlay);
        }
    }

    /**
     * Calls back to the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer for their {@link GeoRenderLayer#renderForBone per-bone} render actions.
     */
    default void applyRenderLayersForBone(GlStateManager poseStack, T animatable, GeoBone bone, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, float partialTick, int packedLight, int packedOverlay) {
        for (GeoRenderLayer<T> renderLayer : getRenderLayers()) {
            renderLayer.renderForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick,
                    packedLight, packedOverlay);
        }
    }

    /**
     * Render the various {@link GeoRenderLayer RenderLayers} that have been registered to this renderer
     */
    default void applyRenderLayers(GlStateManager poseStack, T animatable, BakedGeoModel model, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, float partialTick, int packedLight, int packedOverlay) {
        for (GeoRenderLayer<T> renderLayer : getRenderLayers()) {
            renderLayer.render(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight,
                    packedOverlay);
        }
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling and translating.<br>
     * {@link GlStateManager} translations made here are kept until the end of the render process
     */
    default void preRender(GlStateManager poseStack, T animatable, BakedGeoModel model, @Nullable IRenderTypeBuffer bufferSource, @Nullable IVertexBuilder buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    }

    /**
     * Called after rendering the model to buffer. Post-render modifications should be performed here.<br>
     * {@link GlStateManager} transformations will be unused and lost once this method ends
     */
    default void postRender(GlStateManager poseStack, T animatable, BakedGeoModel model, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    }

    /**
     * Call after all other rendering work has taken place, including reverting the {@link MatrixStack}'s state. This method is <u>not</u> called in {@link GeoRenderer#reRender re-render}
     */
    default void renderFinal(GlStateManager poseStack, T animatable, BakedGeoModel model, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    }

    /**
     * Renders the provided {@link GeoBone} and its associated child bones
     */
    default void renderRecursively(GlStateManager poseStack, T animatable, GeoBone bone, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.push();
        RenderUtils.prepMatrixForBone(bone);
        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        if (!isReRender) {
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick,
                    packedLight, packedOverlay);
            if (buffer instanceof BufferBuilder && !((BufferBuilder) buffer).isDrawing)
                buffer = bufferSource.getBuffer(renderType);
        }

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.pop();
    }

    /**
     * Renders the {@link GeoCube GeoCubes} associated with a given {@link GeoBone}
     */
    default void renderCubesOfBone(GlStateManager poseStack, GeoBone bone, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.isHidden())
            return;

        for (GeoCube cube : bone.getCubes()) {
            poseStack.push();
            renderCube(poseStack, cube, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.pop();
        }
    }

    /**
     * Render the child bones of a given {@link GeoBone}.<br>
     * Note that this does not render the bone itself. That should be done through {@link GeoRenderer#renderCubesOfBone} separately
     */
    default void renderChildBones(GlStateManager poseStack, T animatable, GeoBone bone, RenderType renderType, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.isHidingChildren())
            return;

        for (GeoBone childBone : bone.getChildBones()) {
            renderRecursively(poseStack, animatable, childBone, renderType, bufferSource, buffer, isReRender,
                    partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    /**
     * Renders an individual {@link GeoCube}.<br>
     * This tends to be called recursively from something like {@link GeoRenderer#renderCubesOfBone}
     */
    default void renderCube(GlStateManager poseStack, GeoCube cube, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RenderUtils.translateToPivotPoint(cube);
        RenderUtils.rotateMatrixAroundCube(cube);
        RenderUtils.translateAwayFromPivotPoint(cube);

        Matrix3f normalisedPoseState = poseStack.getLast().getNormal();
        Matrix4f poseState = poseStack.getLast().getMatrix();

        for (GeoQuad quad : cube.quads()) {
            if (quad == null)
                continue;

            Vector3f normal = quad.normal().copy();

            normal.transform(normalisedPoseState);

            RenderUtils.fixInvertedFlatCube(cube, normal);
            createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    /**
     * Applies the {@link GeoQuad Quad's} {@link GeoVertex vertices} to the given {@link IVertexBuilder buffer} for rendering
     */
    default void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        for (GeoVertex vertex : quad.vertices()) {
            Vector4f vector4f = new Vector4f(vertex.position().getX(), vertex.position().getY(), vertex.position().getZ(), 1);

            vector4f.transform(poseState);

            buffer.addVertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.texU(),
                    vertex.texV(), packedOverlay, packedLight, normal.getX(), normal.getY(), normal.getZ());
        }
    }

    /**
     * Scales the {@link GlStateManager} in preparation for rendering the model, excluding when re-rendering the model as part of a {@link GeoRenderLayer} or external render call.<br>
     * Override and call super with modified scale values as needed to further modify the scale of the model (E.G. child entities)
     */
    default void scaleModelForRender(float widthScale, float heightScale, GlStateManager poseStack, T animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        if (!isReRender && (widthScale != 1 || heightScale != 1))
            poseStack.scale(widthScale, heightScale, widthScale);
    }
}
