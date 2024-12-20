package mod.azure.azurelib.core2.render.pipeline.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.block.AzBlockRenderer;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipeline;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class AzBlockEntityRendererPipeline<T extends BlockEntity> extends AzRendererPipeline<T> {

    private final AzBlockRenderer<T> blockRenderer;

    protected Matrix4f entityRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzBlockEntityRendererPipeline(AzBlockRenderer<T> entityRenderer) {
        this.blockRenderer = entityRenderer;
    }

    @Override
    protected AzBlockEntityRendererPipelineContext<T> createContext(AzRendererPipeline<T> rendererPipeline) {
        return new AzBlockEntityRendererPipelineContext<>(this);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T animatable) {
        return blockRenderer.getTextureLocation(animatable);
    }

    @Override
    protected List<AzRenderLayer<T>> getRenderLayers() {
        return blockRenderer.getRenderLayers();
    }

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this
     * GeoRenderer.<br>
     * This should only be called immediately prior to rendering, and only
     *
     * @see AnimatableTexture#setAndUpdate(ResourceLocation, int)
     */
    @Override
    public void updateAnimatedTextureFrame(T entity) {
        AnimatableTexture.setAndUpdate(
                getTextureLocation(entity),
                entity.getBlockPos().getX() + entity.getBlockPos().getY() + entity.getBlockPos().getZ()
                        + (int) RenderUtils.getCurrentTick()
        );
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(AzRendererPipelineContext<T> context, boolean isReRender) {
        var poseStack = context.poseStack();
        this.entityRenderTranslations.set(poseStack.last().pose());

        scaleModelForRender(
                context,
                this.blockRenderer.getScaleWidth(),
                this.blockRenderer.getScaleHeight(),
                isReRender
        );
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link AzBlockEntityRendererPipeline#preRender} has already been called by this stage, and
     * {@link AzBlockEntityRendererPipeline#postRender} will be called directly after
     */
    @Override
    public void actuallyRender(AzRendererPipelineContext<T> context, boolean isReRender) {
        var entity = context.animatable();
        var poseStack = context.poseStack();

        if (!isReRender) {
            rotateBlock(getFacing(entity), poseStack);

            poseStack.translate(0.5, 0, 0.5);
            var animator = blockRenderer.getAnimator();

            if (animator != null) {
                animator.animate(entity);
            }
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        RenderSystem.setShaderTexture(0, getTextureLocation(entity));
        super.actuallyRender(context, isReRender);
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    @Override
    public void renderRecursively(AzRendererPipelineContext<T> context, AzBone bone, boolean isReRender) {
        var buffer = context.vertexConsumer();
        var bufferSource = context.multiBufferSource();
        var entity = context.animatable();
        var poseStack = context.poseStack();
        var renderType = context.renderType();

        poseStack.pushPose();
        RenderUtils.translateMatrixToBone(poseStack, bone);
        RenderUtils.translateToPivotPoint(poseStack, bone);
        RenderUtils.rotateMatrixAroundBone(poseStack, bone);
        RenderUtils.scaleMatrixForBone(poseStack, bone);

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

            bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
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
            applyRenderLayersForBone(context, bone);
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
