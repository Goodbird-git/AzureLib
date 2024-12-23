package mod.azure.azurelib.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.BlockPos;

/**
 * Utility class for rendering entity leash visuals within the Minecraft rendering engine. This class provides static
 * methods to handle leash rendering logic, enabling flexible re-use and separation from the default rendering behavior.
 */
public class AzEntityLeashRenderUtil {

    /**
     * Static rendering code for rendering a leash segment.<br>
     */
    public static <T extends Entity, E extends Entity, M extends EntityMob> void renderLeash(
        AzEntityRenderer<T> azEntityRenderer,
        M mob,
        float partialTick,
        GlStateManager glStateManager,
        E leashHolder
    ) {
        double lerpBodyAngle = (Mth.lerp(partialTick, mob.yBodyRotO, mob.yBodyRot) * Mth.DEG_TO_RAD) + Mth.HALF_PI;
        Vec3 leashOffset = mob.getLeashOffset();
        double xAngleOffset = Math.cos(lerpBodyAngle) * leashOffset.z + Math.sin(lerpBodyAngle) * leashOffset.x;
        double zAngleOffset = Math.sin(lerpBodyAngle) * leashOffset.z - Math.cos(lerpBodyAngle) * leashOffset.x;
        double lerpOriginX = Mth.lerp(partialTick, mob.xo, mob.getX()) + xAngleOffset;
        double lerpOriginY = Mth.lerp(partialTick, mob.yo, mob.getY()) + leashOffset.y;
        double lerpOriginZ = Mth.lerp(partialTick, mob.zo, mob.getZ()) + zAngleOffset;
        Vec3 ropeGripPosition = leashHolder.getRopeHoldPosition(partialTick);
        float xDif = (float) (ropeGripPosition.x - lerpOriginX);
        float yDif = (float) (ropeGripPosition.y - lerpOriginY);
        float zDif = (float) (ropeGripPosition.z - lerpOriginZ);
        float offsetMod = Mth.invSqrt(xDif * xDif + zDif * zDif) * 0.025f / 2f;
        float xOffset = zDif * offsetMod;
        float zOffset = xDif * offsetMod;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.leash());
        BlockPos entityEyePos = BlockPos.containing(mob.getEyePosition(partialTick));
        BlockPos holderEyePos = BlockPos.containing(leashHolder.getEyePosition(partialTick));
        int entityBlockLight = azEntityRenderer.getBlockLightLevel((T) mob, entityEyePos);
        int holderBlockLight = leashHolder.isOnFire()
            ? 15
            : leashHolder.level()
                .getBrightness(
                    LightLayer.BLOCK,
                    holderEyePos
                );
        int entitySkyLight = mob.level().getBrightness(LightLayer.SKY, entityEyePos);
        int holderSkyLight = mob.level().getBrightness(LightLayer.SKY, holderEyePos);

        glStateManager.pushMatrix();
        glStateManager.translate(xAngleOffset, leashOffset.y, zAngleOffset);

        Matrix4f posMatrix = new Matrix4f(glStateManager.last().pose());

        for (int segment = 0; segment <= 24; ++segment) {
            renderLeashPiece(
                xDif,
                yDif,
                zDif,
                entityBlockLight,
                holderBlockLight,
                entitySkyLight,
                holderSkyLight,
                0.025f,
                0.025f,
                xOffset,
                zOffset,
                segment,
                false
            );
        }

        for (int segment = 24; segment >= 0; --segment) {
            renderLeashPiece(
                xDif,
                yDif,
                zDif,
                entityBlockLight,
                holderBlockLight,
                entitySkyLight,
                holderSkyLight,
                0.025f,
                0.0f,
                xOffset,
                zOffset,
                segment,
                true
            );
        }

        glStateManager.popMatrix();
    }

    /**
     * Static rendering code for rendering a leash segment.
     */
    private static void renderLeashPiece(
        float xDif,
        float yDif,
        float zDif,
        int entityBlockLight,
        int holderBlockLight,
        int entitySkyLight,
        int holderSkyLight,
        float width,
        float yOffset,
        float xOffset,
        float zOffset,
        int segment,
        boolean isLeashKnot
    ) {
        float piecePosPercent = segment / 24f;
        var lerpBlockLight = (int) Math.lerp(piecePosPercent, entityBlockLight, holderBlockLight);
        var lerpSkyLight = (int) Mth.lerp(piecePosPercent, entitySkyLight, holderSkyLight);
        var packedLight = LightTexture.pack(lerpBlockLight, lerpSkyLight);
        float knotColourMod = segment % 2 == (isLeashKnot ? 1 : 0) ? 0.7f : 1f;
        float red = 0.5f * knotColourMod;
        float green = 0.4f * knotColourMod;
        float blue = 0.3f * knotColourMod;
        float x = xDif * piecePosPercent;
        float y = yDif > 0.0f
            ? yDif * piecePosPercent * piecePosPercent
            : yDif - yDif * (1.0f - piecePosPercent) * (1.0f - piecePosPercent);
        float z = zDif * piecePosPercent;

        buffer.addVertex(positionMatrix, x - xOffset, y + yOffset, z + zOffset)
            .setColor(red, green, blue, 1)
            .setLight(packedLight);
        buffer.addVertex(positionMatrix, x + xOffset, y + width - yOffset, z - zOffset)
            .setColor(red, green, blue, 1)
            .setLight(packedLight);
    }

    private AzEntityLeashRenderUtil() {
        throw new UnsupportedOperationException();
    }
}
