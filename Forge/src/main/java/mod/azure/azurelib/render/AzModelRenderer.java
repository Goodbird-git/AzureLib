package mod.azure.azurelib.render;

import mod.azure.azurelib.cache.object.GeoCube;
import mod.azure.azurelib.cache.object.GeoQuad;
import mod.azure.azurelib.cache.object.GeoVertex;
import mod.azure.azurelib.core.object.Color;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.util.MatrixUtils;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * AzModelRenderer provides a generic and extensible base class for rendering models by processing hierarchical bone
 * structures recursively. It leverages a rendering pipeline and a layer renderer to facilitate advanced rendering
 * tasks, including layer application and animated texture processing.
 *
 * @param <T> the type of animatable object this renderer supports
 */
public class AzModelRenderer<T> {

    private final Matrix4f poseStateCache = new Matrix4f();

    private final Vector3f normalCache = new Vector3f();

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
        GlStateManager.pushMatrix();
        RenderUtils.prepMatrixForBone(bone);
        renderCubesOfBone(context, bone);

        if (!isReRender) {
            layerRenderer.applyRenderLayersForBone(context, bone);
        }

        renderChildBones(context, bone, isReRender);
        GlStateManager.popMatrix();
    }

    /**
     * Renders the {@link GeoCube GeoCubes} associated with a given {@link AzBone}
     */
    protected void renderCubesOfBone(AzRendererPipelineContext<T> context, AzBone bone) {
        if (bone.isHidden()) {
            return;
        }

        for (GeoCube cube : bone.getCubes()) {
            GlStateManager.pushMatrix();

            renderCube(context, cube);

            GlStateManager.popMatrix();
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

        RenderUtils.translateToPivotPoint(cube);
        RenderUtils.rotateMatrixAroundCube(cube);
        RenderUtils.translateAwayFromPivotPoint(cube);

        for (GeoQuad quad : cube.quads()) {
            if (quad == null) {
                continue;
            }
            Vector3f normal = new Vector3f(quad.getNormal().getX(), quad.getNormal().getY(), quad.getNormal().getZ());

            normalCache.set(quad.getNormal());
            GlStateManager.glNormal3f(quad.getNormal().getX(), quad.getNormal().getY(), quad.getNormal().getZ());
            RenderUtils.fixInvertedFlatCube(cube, normal);
            createVerticesOfQuad(context, quad, normal);
        }
    }

    protected void createVerticesOfQuad(
        AzRendererPipelineContext<T> context,
        GeoQuad quad,
        Vector3f normal
    ) {
        BufferBuilder buffer = context.vertexConsumer();
        int packedOverlay = context.packedOverlay();
        int packedLight = context.packedLight();
        Color color = context.getRenderColor(context.animatable(), context.partialTick(), context.packedLight());

        for (GeoVertex vertex : quad.getVertices()) {
            Vector4f vector4f = new Vector4f(vertex.position().getX(), vertex.position().getY(), vertex.position().getZ(),
                    1.0F);

            MatrixUtils.getCameraMatrix().transform(vector4f);

            buffer.pos(vector4f.getX(), vector4f.getY(), vector4f.getZ())
                    .tex(vertex.texU(), vertex.texV())
                    .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                    .normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
        }
    }
}
