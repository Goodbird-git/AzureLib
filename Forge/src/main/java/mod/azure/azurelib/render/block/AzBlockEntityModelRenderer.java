package mod.azure.azurelib.render.block;

import mod.azure.azurelib.animation.impl.AzBlockAnimator;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzModelRenderer;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * The AzBlockEntityModelRenderer is a specialized model renderer class for rendering block entities in a 3D space. It
 * extends the AzModelRenderer class and provides functionality specific to handling and rendering block entities based
 * on their corresponding properties and transformations.
 *
 * @param <T> The type of TileEntity that this renderer is responsible for
 */
public class AzBlockEntityModelRenderer<T extends TileEntity> extends AzModelRenderer<T> {

    private final AzBlockEntityRendererPipeline<T> blockEntityRendererPipeline;

    public AzBlockEntityModelRenderer(
        AzBlockEntityRendererPipeline<T> blockEntityRendererPipeline,
        AzLayerRenderer<T> layerRenderer
    ) {
        super(blockEntityRendererPipeline, layerRenderer);
        this.blockEntityRendererPipeline = blockEntityRendererPipeline;
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link AzBlockEntityRendererPipeline#preRender} has already been called by this stage, and
     * {@link AzBlockEntityRendererPipeline#postRender} will be called directly after
     */
    @Override
    public void render(AzRendererPipelineContext<T> context, boolean isReRender) {
        T entity = context.animatable();

        if (!isReRender) {
            rotateBlock(getFacing(entity));

            GlStateManager.translate(0.5, 0, 0.5);
            AzBlockAnimator<T> animator = blockEntityRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                animator.animate(entity, context.partialTick());
            }
        }

        blockEntityRendererPipeline.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        ResourceLocation textureLocation = blockEntityRendererPipeline.config().textureLocation(entity);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureLocation);
        super.render(context, isReRender);
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    @Override
    public void renderRecursively(AzRendererPipelineContext<T> context, AzBone bone, boolean isReRender) {
        T entity = context.animatable();

        GlStateManager.pushMatrix();
        RenderUtils.translateMatrixToBone(bone);
        RenderUtils.translateToPivotPoint(bone);
        RenderUtils.rotateMatrixAroundBone(bone);
        RenderUtils.scaleMatrixForBone(bone);

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(
                poseState,
                blockEntityRendererPipeline.entityRenderTranslations
            );

            bone.setModelSpaceMatrix(
                RenderUtils.invertAndMultiplyMatrices(poseState, blockEntityRendererPipeline.modelRenderTranslations)
            );
            bone.setLocalSpaceMatrix(
                RenderUtils.translateMatrix(localMatrix, Vec3.ZERO.toVector3f())
            );
            bone.setWorldSpaceMatrix(
                RenderUtils.translateMatrix(
                    new Matrix4f(localMatrix),
                    new Vector3f(
                        entity.getBlockPos().getX(),
                        entity.getBlockPos().getY(),
                        entity.getBlockPos().getZ()
                    )
                )
            );
        }

        RenderUtils.translateAwayFromPivotPoint(bone);

        if (!isReRender && buffer instanceof BufferBuilder builder && !builder.building) {
            context.setVertexConsumer(bufferSource.getBuffer(renderType));
        }

        renderCubesOfBone(context, bone);

        if (!isReRender) {
            layerRenderer.applyRenderLayersForBone(context, bone);
        }

        renderChildBones(context, bone, isReRender);

        poseStack.popMatrix();
    }

    /**
     * Attempt to extract a direction from the block so that the model can be oriented correctly
     */
    protected EnumFacing getFacing(T block) {
        IBlockState blockState = block.getBlockState();

        if (blockState.hasProperty(HorizontalDirectionalBlock.FACING))
            return blockState.getValue(HorizontalDirectionalBlock.FACING);

        if (blockState.hasProperty(DirectionalBlock.FACING))
            return blockState.getValue(DirectionalBlock.FACING);

        return EnumFacing.NORTH;
    }

    /**
     * Rotate the {@link GlStateManager} based on the determined {@link EnumFacing} the block is facing
     */
    protected void rotateBlock(EnumFacing facing) {
        switch (facing) {
            case SOUTH:
                GlStateManager.rotate(180, 0, 1, 0);
            case WEST:
                GlStateManager.rotate(90, 0, 1, 0);
            case NORTH:
                GlStateManager.rotate(0, 0, 1, 0);
            case EAST:
                GlStateManager.rotate(270, 0, 1, 0);
            case UP:
                GlStateManager.rotate(90, 1,0,0);
            case DOWN:
                GlStateManager.rotate(90, -1, 0, 0);
        }
    }
}
