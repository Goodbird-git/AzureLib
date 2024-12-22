package mod.azure.azurelib.render.item;

import mod.azure.azurelib.model.AzBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

public class AzItemGuiRenderUtil {

    /**
     * Wrapper method to handle rendering the item in a GUI context (defined by
     * {@link ItemCameraTransforms.TransformType#GUI} normally).<br>
     * Just includes some additional required transformations and settings.
     */
    public static void renderInGui(
        AzItemRendererConfig config,
        AzItemRendererPipeline rendererPipeline,
        ItemStack stack,
        AzBakedModel model,
        ItemStack currentItemStack,
        PoseStack poseStack,
        MultiBufferSource source,
        int packedLight
    ) {
        if (config.useEntityGuiLighting()) {
            Lighting.setupForEntityInInventory();
        } else {
            Lighting.setupForFlatItems();
        }

        var partialTick = Minecraft.getMinecraft().getTimer().getGameTimeDeltaTicks();
        var bSource =
            source instanceof MultiBufferSource.BufferSource bufferSource
                ? bufferSource
                : Minecraft.getMinecraft().levelRenderer.renderBuffers.bufferSource();
        net.minecraft.util.ResourceLocation textureLocation = config.textureLocation(stack);
        var renderType = rendererPipeline.context()
            .getDefaultRenderType(stack, textureLocation, bSource, partialTick);
        var withGlint = currentItemStack != null && currentItemStack.hasFoil();
        var buffer = ItemRenderer.getFoilBufferDirect(source, renderType, true, withGlint);

        poseStack.pushPose();

        rendererPipeline.render(poseStack, model, stack, bSource, renderType, buffer, 0, partialTick, packedLight);

        bSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();

        poseStack.popPose();
    }
}
