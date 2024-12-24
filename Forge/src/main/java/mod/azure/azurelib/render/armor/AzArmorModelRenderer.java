package mod.azure.azurelib.render.armor;

import mod.azure.azurelib.animation.impl.AzItemAnimator;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzModelRenderer;
import mod.azure.azurelib.render.AzPhasedRenderer;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class AzArmorModelRenderer extends AzModelRenderer<ItemStack> {

    private final AzArmorRendererPipeline armorRendererPipeline;

    public AzArmorModelRenderer(
        AzArmorRendererPipeline armorRendererPipeline,
        AzLayerRenderer<ItemStack> layerRenderer
    ) {
        super(armorRendererPipeline, layerRenderer);
        this.armorRendererPipeline = armorRendererPipeline;
    }

    /**
     * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
     * {@link AzPhasedRenderer#preRender} has already been called by this stage, and {@link AzPhasedRenderer#postRender}
     * will be called directly after
     */
    @Override
    public void render(AzRendererPipelineContext<ItemStack> context, boolean isReRender) {
        GlStateManager poseStack = context.glStateManager();
        poseStack.pushMatrix();
        poseStack.translate(0, 24 / 16f, 0);
        poseStack.scale(-1, -1, 1);

        if (!isReRender) {
            ItemStack animatable = context.animatable();
            AzItemAnimator animator = armorRendererPipeline.renderer().animator();

            if (animator != null) {
                animator.animate(animatable, context.partialTick());
            }
        }

        armorRendererPipeline.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        super.render(context, isReRender);
        poseStack.popMatrix();
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    @Override
    public void renderRecursively(AzRendererPipelineContext<ItemStack> context, AzBone bone, boolean isReRender) {
        GlStateManager poseStack = context.glStateManager();
        // TODO: This is dangerous.
        AzArmorRendererPipelineContext ctx = armorRendererPipeline.context();

//        if (bone.isTrackingMatrices()) {
//            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
//            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(
//                poseState,
//                armorRendererPipeline.entityRenderTranslations
//            );
//
//            bone.setModelSpaceMatrix(
//                RenderUtils.invertAndMultiplyMatrices(poseState, armorRendererPipeline.modelRenderTranslations)
//            );
//            bone.setLocalSpaceMatrix(RenderUtils.translateMatrix(localMatrix, new Vector3f()));
//            bone.setWorldSpaceMatrix(
//                RenderUtils.translateMatrix(new Matrix4f(localMatrix), ctx.currentEntity().position().toVector3f())
//            );
//        }

        super.renderRecursively(context, bone, isReRender);
    }
}
