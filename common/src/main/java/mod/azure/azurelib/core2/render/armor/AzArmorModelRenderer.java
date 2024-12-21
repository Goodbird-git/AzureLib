package mod.azure.azurelib.core2.render.armor;

import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import mod.azure.azurelib.common.internal.client.util.RenderUtils;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.render.AzLayerRenderer;
import mod.azure.azurelib.core2.render.AzModelRenderer;
import mod.azure.azurelib.core2.render.AzPhasedRenderer;
import mod.azure.azurelib.core2.render.AzRendererPipelineContext;

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
        var poseStack = context.poseStack();
        poseStack.pushPose();
        poseStack.translate(0, 24 / 16f, 0);
        poseStack.scale(-1, -1, 1);

        if (!isReRender) {
            var animatable = context.animatable();
            var animator = armorRendererPipeline.renderer().animator();

            if (animator != null) {
                animator.animate(animatable);
            }
        }

        armorRendererPipeline.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        super.render(context, isReRender);
        poseStack.popPose();
    }

    /**
     * Renders the provided {@link AzBone} and its associated child bones
     */
    @Override
    public void renderRecursively(AzRendererPipelineContext<ItemStack> context, AzBone bone, boolean isReRender) {
        var poseStack = context.poseStack();
        // TODO: This is dangerous.
        var ctx = armorRendererPipeline.context();

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(
                poseState,
                armorRendererPipeline.entityRenderTranslations
            );

            bone.setModelSpaceMatrix(
                RenderUtils.invertAndMultiplyMatrices(poseState, armorRendererPipeline.modelRenderTranslations)
            );
            bone.setLocalSpaceMatrix(RenderUtils.translateMatrix(localMatrix, new Vector3f()));
            bone.setWorldSpaceMatrix(
                RenderUtils.translateMatrix(new Matrix4f(localMatrix), ctx.currentEntity().position().toVector3f())
            );
        }

        super.renderRecursively(context, bone, isReRender);
    }
}
