package mod.azure.azurelib.render;

import mod.azure.azurelib.cache.object.GeoCube;
import mod.azure.azurelib.cache.object.GeoQuad;
import mod.azure.azurelib.cache.object.GeoVertex;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec3d;

/**
 * AzModelRenderer provides a generic and extensible base class for rendering models by processing hierarchical bone
 * structures recursively. It leverages a rendering pipeline and a layer renderer to facilitate advanced rendering
 * tasks, including layer application and animated texture processing.
 *
 * @param <T> the type of animatable object this renderer supports
 */
public class AzModelRenderer<T> {

    private final Matrix4f poseStateCache = new Matrix4f();

    private final Vec3d normalCache = new Vec3d(0,0,0);

    private final AzRendererPipeline<T> rendererPipeline;

    protected final AzLayerRenderer<T> layerRenderer;

    public AzModelRenderer(AzRendererPipeline<T> rendererPipeline, AzLayerRenderer<T> layerRenderer) {
        this.layerRenderer = layerRenderer;
        this.rendererPipeline = rendererPipeline;
    }

    /**
     * The actual render method that sub-type renderers should override to handle their specific rendering tasks.<br>
     */
    protected void render(AzRendererPipelineContext<T> context, boolean isReRender) {
        T animatable = context.animatable();
        AzBakedModel model = context.bakedModel();

        rendererPipeline.updateAnimatedTextureFrame(animatable);

        for (AzBone bone : model.getTopLevelBones()) {
            renderRecursively(context, bone, isReRender);
        }
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    protected void renderRecursively(AzRendererPipelineContext<T> context, AzBone bone, boolean isReRender) {
        GlStateManager poseStack = context.glStateManager();

        poseStack.pushMatrix();
        RenderUtils.prepMatrixForBone(poseStack, bone);
        renderCubesOfBone(context, bone);

        if (!isReRender) {
            layerRenderer.applyRenderLayersForBone(context, bone);
        }

        renderChildBones(context, bone, isReRender);
        poseStack.popMatrix();
    }

    /**
     * Renders the {@link GeoCube GeoCubes} associated with a given {@link AzBone}
     */
    protected void renderCubesOfBone(AzRendererPipelineContext<T> context, AzBone bone) {
        if (bone.isHidden()) {
            return;
        }

        GlStateManager poseStack = context.glStateManager();

        for (GeoCube cube : bone.getCubes()) {
            poseStack.pushMatrix();

            renderCube(context, cube);

            poseStack.popMatrix();
        }
    }

    /**
     * Render the child bones of a given {@link AzBone}.<br>
     * Note that this does not render the bone itself. That should be done through
     * {@link AzModelRenderer#renderCubesOfBone} separately
     */
    protected void renderChildBones(AzRendererPipelineContext<T> context, AzBone bone, boolean isReRender) {
        if (bone.isHidingChildren())
            return;

        for (AzBone childBone : bone.getChildBones()) {
            renderRecursively(context, childBone, isReRender);
        }
    }

    /**
     * Renders an individual {@link GeoCube}.<br>
     * This tends to be called recursively from something like {@link AzModelRenderer#renderCubesOfBone}
     */
    protected void renderCube(AzRendererPipelineContext<T> context, GeoCube cube) {
        GlStateManager poseStack = context.glStateManager();

        RenderUtils.translateToPivotPoint(poseStack, cube);
        RenderUtils.rotateMatrixAroundCube(poseStack, cube);
        RenderUtils.translateAwayFromPivotPoint(poseStack, cube);

        var normalisedPoseState = poseStack.last().normal();
        var poseState = poseStateCache.set(poseStack.last().pose());

        for (GeoQuad quad : cube.quads()) {
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
        VertexConsumer buffer = context.vertexConsumer();
        int color = context.renderColor();
        int packedOverlay = context.packedOverlay();
        int packedLight = context.packedLight();

        for (GeoVertex vertex : quad.getVertices()) {
            Vector3f position = vertex.position();
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
}
