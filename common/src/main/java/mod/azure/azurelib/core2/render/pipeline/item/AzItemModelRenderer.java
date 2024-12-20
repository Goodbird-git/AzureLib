package mod.azure.azurelib.core2.render.pipeline.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.pipeline.AzLayerRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzModelRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzPhasedRenderer;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;

public class AzItemModelRenderer extends AzModelRenderer<ItemStack> {

    private final AzItemRendererPipeline itemRendererPipeline;

    public AzItemModelRenderer(AzItemRendererPipeline itemRendererPipeline, AzLayerRenderer<ItemStack> layerRenderer) {
        super(itemRendererPipeline, layerRenderer);
        this.itemRendererPipeline = itemRendererPipeline;
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link AzPhasedRenderer#preRender} has already been called by this stage, and {@link AzPhasedRenderer#postRender}
     * will be called directly after
     */
    @Override
    public void render(AzRendererPipelineContext<ItemStack> context, boolean isReRender) {
        if (!isReRender) {
            var animatable = context.animatable();
            var animator = itemRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                animator.animate(animatable);
            }
        }

        var poseStack = context.poseStack();

        itemRendererPipeline.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        super.render(context, isReRender);
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
            var localMatrix = RenderUtils.invertAndMultiplyMatrices(
                poseState,
                itemRendererPipeline.itemRenderTranslations
            );

            bone.setModelSpaceMatrix(
                RenderUtils.invertAndMultiplyMatrices(poseState, itemRendererPipeline.modelRenderTranslations)
            );
            bone.setLocalSpaceMatrix(
                RenderUtils.translateMatrix(localMatrix, getRenderOffset(animatable, 1).toVector3f())
            );
        }

        super.renderRecursively(context, bone, isReRender);
    }

    public Vec3 getRenderOffset(ItemStack itemStack, float f) {
        return Vec3.ZERO;
    }
}
