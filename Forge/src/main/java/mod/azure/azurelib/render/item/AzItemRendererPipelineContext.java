package mod.azure.azurelib.render.item;

import mod.azure.azurelib.render.AzRendererPipeline;
import mod.azure.azurelib.render.AzRendererPipelineContext;
import net.minecraft.item.ItemStack;

/**
 * A specialized subclass of {@link AzRendererPipelineContext} designed for rendering {@link ItemStack} objects.
 * Provides the default rendering context and pipeline for rendering item models within a custom rendering framework.
 * <br>
 * This context delegates rendering operations to its associated {@link AzRendererPipeline} while providing additional
 * configuration and control over the rendering process of an {@link ItemStack}.
 */
public class AzItemRendererPipelineContext extends AzRendererPipelineContext<ItemStack> {

    public AzItemRendererPipelineContext(AzRendererPipeline<ItemStack> rendererPipeline) {
        super(rendererPipeline);
    }

    // TODO: This is what Geckolib does, but it feels wrong to have this render type getter for an ITEM...
//    @Override
//    public @NotNull RenderType getDefaultRenderType(
//        ItemStack animatable,
//        ResourceLocation texture,
//        @Nullable MultiBufferSource bufferSource,
//        float partialTick
//    ) {
//        return RenderType.entityCutoutNoCull(texture);
//    }
}
