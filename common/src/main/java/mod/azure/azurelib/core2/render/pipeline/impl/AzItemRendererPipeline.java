package mod.azure.azurelib.core2.render.pipeline.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import mod.azure.azurelib.common.internal.client.renderer.GeoRenderer;
import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.common.internal.common.cache.texture.AnimatableTexture;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.item.AzItemRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipeline;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;

public class AzItemRendererPipeline extends AzRendererPipeline<ItemStack> {

    private final AzItemRenderer itemRenderer;

    protected Matrix4f itemRenderTranslations = new Matrix4f();

    protected Matrix4f modelRenderTranslations = new Matrix4f();

    public AzItemRendererPipeline(AzItemRenderer itemRenderer) {
        this.itemRenderer = itemRenderer;
    }

    @Override
    protected AzRendererPipelineContext<ItemStack> createContext(AzRendererPipeline<ItemStack> rendererPipeline) {
        return new AzItemRendererPipelineContext(rendererPipeline);
    }

    @Override
    protected @NotNull ResourceLocation getTextureLocation(@NotNull ItemStack animatable) {
        return itemRenderer.getTextureLocation(animatable);
    }

    /**
     * Called before rendering the model to buffer. Allows for render modifications and preparatory work such as scaling
     * and translating.<br>
     * {@link PoseStack} translations made here are kept until the end of the render process
     */
    @Override
    public void preRender(AzRendererPipelineContext<ItemStack> context, boolean isReRender) {
        var poseStack = context.poseStack();
        this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());

        scaleModelForRender(context, this.itemRenderer.getScaleWidth(), this.itemRenderer.getScaleHeight(), isReRender);

        if (!isReRender) {
            poseStack.translate(0.5f, this.useNewOffset() ? 0.0f : 0.51f, 0.5f);
        }
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be
     * called directly after
     */
    @Override
    public void actuallyRender(AzRendererPipelineContext<ItemStack> context, boolean isReRender) {
        if (!isReRender) {
            var animatable = context.animatable();
            var animator = itemRenderer.getAnimator();

            if (animator != null) {
                animator.animate(animatable);
            }
        }

        var poseStack = context.poseStack();

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        super.actuallyRender(context, isReRender);
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    @Override
    public void renderRecursively(AzRendererPipelineContext<ItemStack> context, AzBone bone, boolean isReRender) {
        if (bone.isTrackingMatrices()) {
            var animatable = context.animatable();
            var poseStack = context.poseStack();
            var poseState = new Matrix4f(poseStack.last().pose());
            var localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.itemRenderTranslations);

            bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(
                RenderUtils.translateMatrix(localMatrix, getRenderOffset(animatable, 1).toVector3f())
            );
        }

        super.renderRecursively(context, bone, isReRender);
    }

    /**
     * Update the current frame of a {@link AnimatableTexture potentially animated} texture used by this
     * GeoRenderer.<br>
     * This should only be called immediately prior to rendering, and only
     *
     * @see AnimatableTexture#setAndUpdate(ResourceLocation, int)
     */
    @Override
    public void updateAnimatedTextureFrame(ItemStack animatable) {
        AnimatableTexture.setAndUpdate(
            getTextureLocation(animatable),
            Item.getId(animatable.getItem()) + (int) RenderUtils.getCurrentTick()
        );
    }

    public Vec3 getRenderOffset(ItemStack itemStack, float f) {
        return Vec3.ZERO;
    }

    /**
     * Determines whether to apply the y offset for a model due to the change in BlockBench 4.11.
     *
     * @return {@code false} by default, meaning the Y-offset will be {@code 0.51f}. Override this method or change the
     *         return value to {@code true} to use the new Y-offset of {@code 0.0f} for anything created in 4.11+ of
     *         Blockbench.
     */
    public boolean useNewOffset() {
        return false;
    }
}
