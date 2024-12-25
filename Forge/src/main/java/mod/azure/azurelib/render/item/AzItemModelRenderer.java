package mod.azure.azurelib.render.item;

import mod.azure.azurelib.animation.impl.AzItemAnimator;
import mod.azure.azurelib.render.AzLayerRenderer;
import mod.azure.azurelib.render.AzModelRenderer;
import mod.azure.azurelib.render.AzPhasedRenderer;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

/**
 * AzItemModelRenderer is a specialized implementation of {@link AzModelRenderer} for rendering {@link ItemStack}
 * objects. It provides customized rendering logic for rendering item models in a layered and recursive manner.
 */
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
            ItemStack animatable = context.animatable();
            AzItemAnimator animator = itemRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                animator.animate(animatable, context.partialTick());
            }
        }

        super.render(context, isReRender);
    }

    public Vec3d getRenderOffset(ItemStack itemStack, float f) {
        return Vec3d.ZERO;
    }
}
