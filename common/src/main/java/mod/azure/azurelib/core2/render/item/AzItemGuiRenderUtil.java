package mod.azure.azurelib.core2.render.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import mod.azure.azurelib.core2.model.AzBakedModel;

public class AzItemGuiRenderUtil {

    /**
     * Wrapper method to handle rendering the item in a GUI context (defined by
     * {@link net.minecraft.world.item.ItemDisplayContext#GUI} normally).<br>
     * Just includes some additional required transformations and settings.
     */
    public static void renderInGui(
        AzItemRendererConfig config,
        AzItemRendererPipeline rendererPipeline,
        ItemStack animatable,
        AzBakedModel azBakedModel,
        ItemStack currentItemStack,
        ItemDisplayContext transformType,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        if (config.useEntityGuiLighting()) {
            Lighting.setupForEntityInInventory();
        } else {
            Lighting.setupForFlatItems();
        }
        MultiBufferSource.BufferSource defaultBufferSource =
            bufferSource instanceof MultiBufferSource.BufferSource bufferSource2
                ? bufferSource2
                : Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
        RenderType renderType = rendererPipeline.context()
            .getDefaultRenderType(
                animatable,
                config.textureLocation(animatable),
                defaultBufferSource,
                Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
            );
        VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(
            bufferSource,
            renderType,
            true,
            currentItemStack != null && currentItemStack.hasFoil()
        );

        poseStack.pushPose();
        rendererPipeline.render(
            poseStack,
            azBakedModel,
            animatable,
            defaultBufferSource,
            renderType,
            buffer,
            0,
            Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(),
            packedLight
        );
        defaultBufferSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        poseStack.popPose();
    }
}
