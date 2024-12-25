package mod.azure.azurelib.render.layer;

import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

/**
 * A {@link AzRenderLayer} responsible for rendering {@link IBlockState
 * BlockStates} or {@link ItemStack ItemStacks} onto a specified {@link AzRendererPipeline}.
 * This layer handles the rendering of physical elements, such as blocks and items, associated with animation bones.
 */
public class AzBlockAndItemLayer<T> implements AzRenderLayer<T> {

    protected final Function<AzBone, ItemStack> itemStackProvider;

    protected final Function<AzBone, IBlockState> blockStateProvider;

    public AzBlockAndItemLayer() {
        this(bone -> null, bone -> null);
    }

    public AzBlockAndItemLayer(
        Function<AzBone, ItemStack> itemStackProvider,
        Function<AzBone, IBlockState> blockStateProvider
    ) {
        super();

        this.itemStackProvider = itemStackProvider;
        this.blockStateProvider = blockStateProvider;
    }

    @Override
    public void preRender(AzRendererPipelineContext<T> context) {}

    @Override
    public void render(AzRendererPipelineContext<T> context) {}

    /**
     * Renders an {@link ItemStack} or {@link IBlockState} associated with the specified bone in the rendering context.
     * If both the {@link ItemStack} and {@link IBlockState} are {@code null}, no rendering occurs.
     * <p>
     * This method applies the bone's transformations to the current rendering matrix stack before rendering, ensuring
     * the item or block appears correctly positioned and oriented relative to the bone.
     * </p>
     *
     * @param context the rendering pipeline context, containing rendering state and utilities
     * @param bone    the bone for which to render associated elements
     */
    @Override
    public void renderForBone(AzRendererPipelineContext<T> context, AzBone bone) {
        ItemStack stack = itemStackForBone(bone);
        IBlockState blockState = blockStateForBone(bone);

        if (stack == null && blockState == null)
            return;

        context.glStateManager().pushMatrix();
        RenderUtils.translateAndRotateMatrixForBone(context.glStateManager(), bone);

        if (stack != null)
            renderItemForBone(context, bone, stack);

        if (blockState != null)
            renderBlockForBone(context, bone, blockState);

        context.glStateManager().popMatrix();
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
     * Retrieves the {@link IBlockState} associated with the given bone for rendering purposes. Returns {@code null} if
     * there is no {@link IBlockState} to render for this bone.
     *
     * @param bone the bone for which to retrieve the {@link IBlockState}
     * @return the {@link IBlockState} relevant to the specified bone, or {@code null} if none exists
     */
    public IBlockState blockStateForBone(AzBone bone) {
        return blockStateProvider.apply(bone);
    }

    /**
     * Determines the specific {@link ItemCameraTransforms.TransformType} to use for rendering the given {@link ItemStack} on the
     * specified bone. By default, this method returns {@link ItemCameraTransforms.TransformType#NONE}.
     *
     * @param bone  the bone where the {@link ItemStack} will be rendered
     * @param stack the {@link ItemStack} to render
     * @return the {@link ItemCameraTransforms.TransformType} to use for rendering
     */
    protected ItemCameraTransforms.TransformType getTransformTypeForStack(AzBone bone, ItemStack stack) {
        return ItemCameraTransforms.TransformType.NONE;
    }

    /**
     * Renders the given {@link ItemStack} for the specified bone in the rendering context. The rendering adjusts based
     * on whether the animatable object is a {@link EntityLiving}.
     *
     * @param context   the rendering pipeline context
     * @param bone      the bone where the {@link ItemStack} will be rendered
     * @param itemStack the {@link ItemStack} to render
     */
    protected void renderItemForBone(AzRendererPipelineContext<T> context, AzBone bone, ItemStack itemStack) {
        if (context.animatable() instanceof EntityLiving) {
            Minecraft.getMinecraft()
                .getItemRenderer()
                .renderItem(
                    (EntityLiving) context.animatable(),
                    itemStack,
                    getTransformTypeForStack(bone, itemStack)
                );
        }
    }

    /**
     * Renders the given {@link IBlockState} for the specified bone in the rendering context. The block is rendered with
     * adjusted position and scale to fit within the bone's space.
     *
     * @param context    the rendering pipeline context
     * @param bone       the bone where the {@link IBlockState} will be rendered
     * @param blockState the {@link IBlockState} to render
     */
    protected void renderBlockForBone(AzRendererPipelineContext<T> context, AzBone bone, IBlockState blockState) {
        context.glStateManager().pushMatrix();

        context.glStateManager().translate(-0.25f, -0.25f, -0.25f);
        context.glStateManager().scale(0.5f, 0.5f, 0.5f);

        if (context.animatable() instanceof EntityLiving) {
            Minecraft.getMinecraft()
                    .getBlockRendererDispatcher()
                    .renderBlock(
                            blockState,
                            ((EntityLiving)context.animatable()).getPosition(),
                            ((EntityLiving)context.animatable()).world,
                            context.vertexConsumer()
                    );
        }

        context.glStateManager().popMatrix();
    }

}
