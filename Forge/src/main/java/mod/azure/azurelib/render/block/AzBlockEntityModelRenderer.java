package mod.azure.azurelib.render.block;

import mod.azure.azurelib.animation.impl.AzBlockAnimator;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzModelRenderer;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

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
        PoseStack poseStack = context.poseStack();

        if (!isReRender) {
            rotateBlock(getFacing(entity), poseStack);

            poseStack.translate(0.5, 0, 0.5);
            AzBlockAnimator<T> animator = blockEntityRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                animator.animate(entity);
            }
        }

        blockEntityRendererPipeline.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        ResourceLocation textureLocation = blockEntityRendererPipeline.config().textureLocation(entity);
        RenderSystem.setShaderTexture(0, textureLocation);
        super.render(context, isReRender);
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    @Override
    public void renderRecursively(AzRendererPipelineContext<T> context, AzBone bone, boolean isReRender) {
        VertexConsumer buffer = context.vertexConsumer();
        MultiBufferSource bufferSource = context.multiBufferSource();
        T entity = context.animatable();
        PoseStack poseStack = context.poseStack();
        RenderType renderType = context.renderType();

        poseStack.pushPose();
        RenderUtils.translateMatrixToBone(poseStack, bone);
        RenderUtils.translateToPivotPoint(poseStack, bone);
        RenderUtils.rotateMatrixAroundBone(poseStack, bone);
        RenderUtils.scaleMatrixForBone(poseStack, bone);

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

        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

        if (!isReRender && buffer instanceof BufferBuilder builder && !builder.building) {
            context.setVertexConsumer(bufferSource.getBuffer(renderType));
        }

        renderCubesOfBone(context, bone);

        if (!isReRender) {
            layerRenderer.applyRenderLayersForBone(context, bone);
        }

        renderChildBones(context, bone, isReRender);

        poseStack.popPose();
    }

    /**
     * Attempt to extract a direction from the block so that the model can be oriented correctly
     */
    protected Direction getFacing(T block) {
        BlockState blockState = block.getBlockState();

        if (blockState.hasProperty(HorizontalDirectionalBlock.FACING))
            return blockState.getValue(HorizontalDirectionalBlock.FACING);

        if (blockState.hasProperty(DirectionalBlock.FACING))
            return blockState.getValue(DirectionalBlock.FACING);

        return Direction.NORTH;
    }

    /**
     * Rotate the {@link PoseStack} based on the determined {@link Direction} the block is facing
     */
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        switch (facing) {
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case DOWN -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
        }
    }
}
