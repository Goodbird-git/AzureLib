package mod.azure.azurelib.core2.render.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.AzRendererPipeline;
import mod.azure.azurelib.core2.render.AzRendererPipelineContext;

/**
 * A {@link AzRenderLayer} responsible for rendering {@link net.minecraft.world.level.block.state.BlockState
 * BlockStates} or {@link net.minecraft.world.item.ItemStack ItemStacks} onto a specified {@link AzRendererPipeline}.
 * This layer handles the rendering of physical elements, such as blocks and items, associated with animation bones.
 */
public class AzBlockAndItemLayer extends AzRenderLayer {

    protected final Function<AzBone, ItemStack> itemStackProvider;

    protected final Function<AzBone, BlockState> blockStateProvider;

    public AzBlockAndItemLayer() {
        this(bone -> null, bone -> null);
    }

    public AzBlockAndItemLayer(
        Function<AzBone, ItemStack> itemStackProvider,
        Function<AzBone, BlockState> blockStateProvider
    ) {
        super();

        this.itemStackProvider = itemStackProvider;
        this.blockStateProvider = blockStateProvider;
    }

    @Override
    public void preRender(AzRendererPipelineContext context) {}

    @Override
    public void render(AzRendererPipelineContext context) {}

    /**
     * Renders an {@link ItemStack} or {@link BlockState} associated with the specified bone in the rendering context.
     * If both the {@link ItemStack} and {@link BlockState} are {@code null}, no rendering occurs.
     * <p>
     * This method applies the bone's transformations to the current rendering matrix stack before rendering, ensuring
     * the item or block appears correctly positioned and oriented relative to the bone.
     * </p>
     *
     * @param context the rendering pipeline context, containing rendering state and utilities
     * @param bone    the bone for which to render associated elements
     */
    @Override
    public void renderForBone(AzRendererPipelineContext context, AzBone bone) {
        var stack = itemStackForBone(bone);
        var blockState = blockStateForBone(bone);

        if (stack == null && blockState == null)
            return;

        context.poseStack().pushPose();
        RenderUtils.translateAndRotateMatrixForBone(context.poseStack(), bone);

        if (stack != null)
            renderItemForBone(context, bone, stack);

        if (blockState != null)
            renderBlockForBone(context, bone, blockState);

        context.poseStack().popPose();
    }

    /**
     * Retrieves the {@link ItemStack} associated with the given bone for rendering purposes. Returns {@code null} if
     * there is no {@link ItemStack} to render for this bone.
     *
     * @param bone the bone for which to retrieve the {@link ItemStack}
     * @return the {@link ItemStack} relevant to the specified bone, or {@code null} if none exists
     */
    public ItemStack itemStackForBone(AzBone bone) {
        return itemStackProvider.apply(bone);
    }

    /**
     * Retrieves the {@link BlockState} associated with the given bone for rendering purposes. Returns {@code null} if
     * there is no {@link BlockState} to render for this bone.
     *
     * @param bone the bone for which to retrieve the {@link BlockState}
     * @return the {@link BlockState} relevant to the specified bone, or {@code null} if none exists
     */
    public BlockState blockStateForBone(AzBone bone) {
        return blockStateProvider.apply(bone);
    }

    /**
     * Determines the specific {@link ItemDisplayContext} to use for rendering the given {@link ItemStack} on the
     * specified bone. By default, this method returns {@link ItemDisplayContext#NONE}.
     *
     * @param bone  the bone where the {@link ItemStack} will be rendered
     * @param stack the {@link ItemStack} to render
     * @return the {@link ItemDisplayContext} to use for rendering
     */
    protected ItemDisplayContext getTransformTypeForStack(AzBone bone, ItemStack stack) {
        return ItemDisplayContext.NONE;
    }

    /**
     * Renders the given {@link ItemStack} for the specified bone in the rendering context. The rendering adjusts based
     * on whether the animatable object is a {@link LivingEntity}.
     *
     * @param context   the rendering pipeline context
     * @param bone      the bone where the {@link ItemStack} will be rendered
     * @param itemStack the {@link ItemStack} to render
     */
    protected void renderItemForBone(AzRendererPipelineContext context, AzBone bone, ItemStack itemStack) {
        if (context.animatable() instanceof LivingEntity livingEntity) {
            Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(
                    livingEntity,
                    itemStack,
                    getTransformTypeForStack(bone, itemStack),
                    false,
                    context.poseStack(),
                    context.multiBufferSource(),
                    livingEntity.level(),
                    context.packedLight(),
                    context.packedOverlay(),
                    livingEntity.getId()
                );
        } else {
            Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(
                    itemStack,
                    getTransformTypeForStack(bone, itemStack),
                    context.packedLight(),
                    context.packedOverlay(),
                    context.poseStack(),
                    context.multiBufferSource(),
                    Minecraft.getInstance().level,
                    context.animatable().hashCode()
                );
        }
    }

    /**
     * Renders the given {@link BlockState} for the specified bone in the rendering context. The block is rendered with
     * adjusted position and scale to fit within the bone's space.
     *
     * @param context    the rendering pipeline context
     * @param bone       the bone where the {@link BlockState} will be rendered
     * @param blockState the {@link BlockState} to render
     */
    protected void renderBlockForBone(AzRendererPipelineContext context, AzBone bone, BlockState blockState) {
        context.poseStack().pushPose();

        context.poseStack().translate(-0.25f, -0.25f, -0.25f);
        context.poseStack().scale(0.5f, 0.5f, 0.5f);

        Minecraft.getInstance()
            .getBlockRenderer()
            .renderSingleBlock(
                blockState,
                context.poseStack(),
                context.multiBufferSource(),
                context.packedLight(),
                OverlayTexture.NO_OVERLAY
            );

        context.poseStack().popPose();
    }

}
