package mod.azure.azurelib.render.item;

import mod.azure.azurelib.model.AzBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
        int packedLight
    ) {
        int partialTick = Minecraft.getMinecraft().getFrameTimer().getIndex();

        GlStateManager.pushMatrix();

        rendererPipeline.render(model, stack, 0, partialTick, packedLight);

        GlStateManager.popMatrix();
    }
}
