package mod.azure.azurelib.core2.render.pipeline.impl;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.core2.render.pipeline.AzRendererPipeline;
import mod.azure.azurelib.core2.render.pipeline.AzRendererPipelineContext;

public class AzItemRendererPipelineContext extends AzRendererPipelineContext<ItemStack> {

    public AzItemRendererPipelineContext(AzRendererPipeline<ItemStack> rendererPipeline) {
        super(rendererPipeline);
    }

    // TODO: This is what Geckolib does, but it feels wrong to have this render type getter for an ITEM...
    @Override
    public @NotNull RenderType getDefaultRenderType(
        ItemStack animatable,
        ResourceLocation texture,
        @Nullable MultiBufferSource bufferSource,
        float partialTick
    ) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
