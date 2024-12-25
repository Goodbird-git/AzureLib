package mod.azure.azurelib.render.entity;

import mod.azure.azurelib.render.AzRendererPipelineContext;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;

/**
 * Utility class for rendering entity leash visuals within the Minecraft rendering engine. This class provides static
 * methods to handle leash rendering logic, enabling flexible re-use and separation from the default rendering behavior.
 */
public class AzEntityLeashRenderUtil {

    /**
     * Static rendering code for rendering a leash segment.<br>
     */
    public static <T extends Entity> void renderLeash(AzRendererPipelineContext<T> context, float partialTick, Entity leashHolder) {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        double interpolatedYawRadians = AzEntityLeashRenderUtil.interpolateValue(leashHolder.prevRotationYaw, leashHolder.rotationYaw, partialTick * 0.5F) * 0.01745329238474369D;
        double interpolatedPitchRadians = AzEntityLeashRenderUtil.interpolateValue(leashHolder.prevRotationPitch, leashHolder.rotationPitch, partialTick * 0.5F) * 0.01745329238474369D;
        double cosYaw = Math.cos(interpolatedYawRadians);
        double sinYaw = Math.sin(interpolatedYawRadians);
        double sinPitch = Math.sin(interpolatedPitchRadians);

        if (leashHolder instanceof EntityHanging) {
            cosYaw = 0.0D;
            sinYaw = 0.0D;
            sinPitch = -1.0D;
        }

        double cossed = Math.cos(interpolatedPitchRadians);
        double ropeGripPositionX = AzEntityLeashRenderUtil.interpolateValue(leashHolder.prevPosX, leashHolder.posX, partialTick) - cosYaw * 0.7D - sinYaw * 0.5D * cossed;
        double ropeGripPositionY = AzEntityLeashRenderUtil.interpolateValue(leashHolder.prevPosY + leashHolder.getEyeHeight() * 0.7D, leashHolder.posY + leashHolder.getEyeHeight() * 0.7D, partialTick) - sinPitch * 0.5D - 0.25D;
        double ropeGripPositionZ = AzEntityLeashRenderUtil.interpolateValue(leashHolder.prevPosZ, leashHolder.posZ, partialTick) - sinYaw * 0.7D + cosYaw * 0.5D * cossed;
        double d9 = AzEntityLeashRenderUtil.interpolateValue(((EntityLivingBase) context.animatable()).prevRenderYawOffset, ((EntityLivingBase) context.animatable()).renderYawOffset, partialTick) * 0.01745329238474369D + (Math.PI / 2D);
        cosYaw = Math.cos(d9) * (double) context.animatable().width * 0.4D;
        sinYaw = Math.sin(d9) * (double) context.animatable().width * 0.4D;
        double lerpOriginX = AzEntityLeashRenderUtil.interpolateValue(context.animatable().prevPosX, context.animatable().posX, partialTick) + cosYaw;
        double lerpOriginY = AzEntityLeashRenderUtil.interpolateValue(context.animatable().prevPosY, context.animatable().posY, partialTick);
        double lerpOriginZ = AzEntityLeashRenderUtil.interpolateValue(context.animatable().prevPosZ, context.animatable().posZ, partialTick) + sinYaw;
        float xDif = ((float)(ropeGripPositionX - lerpOriginX));
        float yDif = ((float)(ropeGripPositionY - lerpOriginY));
        float zDif = ((float)(ropeGripPositionZ - lerpOriginZ));
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

        for (int segment = 0; segment <= 24; ++segment) {
            renderLeashPiece(bufferbuilder, segment, xDif, yDif, zDif);
        }

        tessellator.draw();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

        for (int segment = 0; segment <= 24; ++segment) {
            renderLeashPiece(bufferbuilder, segment, xDif, yDif, zDif);
        }

        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
    }

    public static void renderLeashPiece(
            BufferBuilder bufferbuilder,
            float segment,
            float xDif,
            float yDif,
            float zDif
    ) {
        float piecePosPercent = segment / 24f;
        float x = xDif * piecePosPercent;
        float y = yDif > 0.0f
                ? yDif * piecePosPercent * piecePosPercent
                : yDif - yDif * (1.0f - piecePosPercent) * (1.0f - piecePosPercent);
        float z = zDif * piecePosPercent;
        float baseRed = 0.5F;  // Base red color component
        float baseGreen = 0.4F;  // Base green color component
        float baseBlue = 0.3F;  // Base blue color component

        if (segment % 2 == 0) {
            baseRed *= 0.7F;
            baseGreen *= 0.7F;
            baseBlue *= 0.7F;
        }

        float positionFactor = segment / 24.0F;  // Factor based on segment position
        double verticalOffset = yDif * (positionFactor * positionFactor + positionFactor) * 0.5D;  // Calculated vertical offset

        // Adding vertices to the buffer
        bufferbuilder.pos(
                x + xDif * positionFactor + 0.0D,
                y + verticalOffset + ((24.0F - segment) / 18.0F + 0.125F),
                z + zDif * positionFactor
        ).color(baseRed, baseGreen, baseBlue, 1.0F).endVertex();

        bufferbuilder.pos(
                x + xDif * positionFactor + 0.025D,
                y + verticalOffset + ((24.0F - segment) / 18.0F + 0.125F) + 0.025D,
                z + zDif * positionFactor
        ).color(baseRed, baseGreen, baseBlue, 1.0F).endVertex();
    }

    private static double interpolateValue(double start, double end, double pct)
    {
        return start + (end - start) * pct;
    }

    private AzEntityLeashRenderUtil() {
        throw new UnsupportedOperationException();
    }
}
