package mod.azure.azurelib.render.armor;

import mod.azure.azurelib.animation.impl.AzItemAnimator;
import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzModelRenderer;
import mod.azure.azurelib.render.AzPhasedRenderer;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import javax.vecmath.Matrix4f;

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
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 24 / 16f, 0);
        GlStateManager.scale(-1, -1, 1);

        if (!isReRender) {
            ItemStack animatable = context.animatable();
            AzItemAnimator animator = armorRendererPipeline.renderer().animator();

            if (animator != null) {
                animator.animate(animatable, context.partialTick());
            }
        }

        armorRendererPipeline.modelRenderTranslations = new Matrix4f(RenderUtils.getCurrentMatrix());

        super.render(context, isReRender);
        GlStateManager.popMatrix();
    }
}
