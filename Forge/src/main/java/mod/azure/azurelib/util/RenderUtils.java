/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.util;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.cache.object.GeoCube;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Quaternion;

import javax.annotation.Nullable;

/**
 * Helper class for various methods and functions useful while rendering
 */
public final class RenderUtils {

	public static boolean isMultipleOf(int p_265754_, int p_265543_) {
		return p_265754_ % p_265543_ == 0;
	}

	public static void translateMatrixToBone(MatrixStack poseStack, CoreGeoBone bone) {
		poseStack.translate(-bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
	}

	public static void rotateMatrixAroundBone(MatrixStack poseStack, CoreGeoBone bone) {
		if (bone.getRotZ() != 0)
			poseStack.rotate(Vector3f.ZP.rotation(bone.getRotZ()));

		if (bone.getRotY() != 0)
			poseStack.rotate(Vector3f.YP.rotation(bone.getRotY()));

		if (bone.getRotX() != 0)
			poseStack.rotate(Vector3f.XP.rotation(bone.getRotX()));
	}

	public static void rotateMatrixAroundCube(MatrixStack poseStack, GeoCube cube) {
		Vec3d rotation = cube.rotation();

		poseStack.rotate(new Quaternion(0, 0, (float) rotation.z, false));
		poseStack.rotate(new Quaternion(0, (float) rotation.y, 0, false));
		poseStack.rotate(new Quaternion((float) rotation.x, 0, 0, false));
	}

	public static void scaleMatrixForBone(MatrixStack poseStack, CoreGeoBone bone) {
		poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translateToPivotPoint(MatrixStack poseStack, GeoCube cube) {
		Vec3d pivot = cube.pivot();
		poseStack.translate(pivot.x / 16f, pivot.y / 16f, pivot.z / 16f);
	}

	public static void translateToPivotPoint(MatrixStack poseStack, CoreGeoBone bone) {
		poseStack.translate(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);
	}

	public static void translateAwayFromPivotPoint(MatrixStack poseStack, GeoCube cube) {
		Vec3d pivot = cube.pivot();

		poseStack.translate(-pivot.x / 16f, -pivot.y / 16f, -pivot.z / 16f);
	}

	public static void translateAwayFromPivotPoint(MatrixStack poseStack, CoreGeoBone bone) {
		poseStack.translate(-bone.getPivotX() / 16f, -bone.getPivotY() / 16f, -bone.getPivotZ() / 16f);
	}

	public static void translateAndRotateMatrixForBone(MatrixStack poseStack, CoreGeoBone bone) {
		translateToPivotPoint(poseStack, bone);
		rotateMatrixAroundBone(poseStack, bone);
	}

	public static void prepMatrixForBone(MatrixStack poseStack, CoreGeoBone bone) {
		translateMatrixToBone(poseStack, bone);
		translateToPivotPoint(poseStack, bone);
		rotateMatrixAroundBone(poseStack, bone);
		scaleMatrixForBone(poseStack, bone);
		translateAwayFromPivotPoint(poseStack, bone);
	}

	public static Matrix4f invertAndMultiplyMatrices(Matrix4f baseMatrix, Matrix4f inputMatrix) {
		inputMatrix = new Matrix4f(inputMatrix);

		inputMatrix.invert();
		inputMatrix.mul(baseMatrix);

		return inputMatrix;
	}

	/**
	 * Translates the provided {@link MatrixStack} to face towards the given {@link Entity}'s rotation.<br>
	 * Usually used for rotating projectiles towards their trajectory<br>
	 */
	public static void faceRotation(MatrixStack poseStack, Entity animatable, float partialTick) {
		poseStack.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTick, animatable.prevRotationYaw, animatable.rotationYaw) - 90));
		poseStack.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTick, animatable.prevRotationPitch, animatable.rotationPitch)));
	}

	/**
	 * Gets the actual dimensions of a texture resource from a given path.<br>
	 * Not performance-efficient, and should not be relied upon
	 *
	 * @param texture The path of the texture resource to check
	 * @return The dimensions (width x height) of the texture, or null if unable to find or read the file
	 */
	@Nullable
	public static Tuple<Integer, Integer> getTextureDimensions(ResourceLocation texture) {
		if (texture == null)
			return null;

		ITextureObject originalTexture = null;
		Minecraft mc = Minecraft.getMinecraft();

		try {
			originalTexture = mc.supplyAsync(() -> mc.getTextureManager().getTexture(texture)).get();
		} catch (Exception e) {
			AzureLib.LOGGER.warn("Failed to load image for id {}", texture);
			e.printStackTrace();
		}

		if (originalTexture == null)
			return null;

		NativeImage image = null;

		try {
			image = originalTexture instanceof DynamicTexture ? ((DynamicTexture) originalTexture).getTextureData() : NativeImage.read(mc.getResourceManager().getResource(texture).getInputStream());
		} catch (Exception e) {
			AzureLib.LOGGER.error("Failed to read image for id {}", texture);
			e.printStackTrace();
		}

		return image == null ? null : new Tuple<Integer, Integer>(image.getWidth(), image.getHeight());
	}

	public static double getCurrentSystemTick() {
		return System.nanoTime() / 1E6 / 50d;
	}

	/**
	 * Returns the current time (in ticks) that the instance has been running. This is effectively a permanent timer that counts up since the game was launched.
	 */
	public static double getCurrentTick() {
		return NativeUtil.getTime() * 20d;
	}

	/**
	 * Returns a float equivalent of a boolean.<br>
	 * Output table:
	 * <ul>
	 * <li>true -> 1</li>
	 * <li>false -> 0</li>
	 * </ul>
	 */
	public static float booleanToFloat(boolean input) {
		return input ? 1f : 0f;
	}

	/**
	 * Converts a given double array to its {@link Vec3d} equivalent
	 */
	public static Vec3d arrayToVec(double[] array) {
		return new Vec3d(array[0], array[1], array[2]);
	}

	/**
	 * Rotates a {@link CoreGeoBone} to match a provided {@link ModelRenderer}'s rotations.<br>
	 * Usually used for items or armor rendering to match the rotations of other non-geo model parts.
	 */
	public static void matchModelPartRot(ModelRenderer from, CoreGeoBone to) {
		to.updateRotation(-from.rotateAngleX, -from.rotateAngleY, from.rotateAngleZ);
	}

	/**
	 * If a {@link GeoCube} is a 2d plane the {@link mod.azure.azurelib.cache.object.GeoQuad Quad's} normal is inverted in an intersecting plane,it can cause issues with shaders and other lighting tasks.<br>
	 * This performs a pseudo-ABS function to help resolve some of those issues.
	 */
	public static void fixInvertedFlatCube(GeoCube cube, Vec3d normal) {
		if (normal.x < 0 && (cube.size().y == 0 || cube.size().z == 0))
			normal.mul(-1, 1, 1);

		if (normal.y < 0 && (cube.size().x == 0 || cube.size().z == 0))
			normal.mul(1, -1, 1);

		if (normal.z < 0 && (cube.size().x == 0 || cube.size().y == 0))
			normal.mul(1, 1, -1);
	}

	/**
	 * Converts a {@link Direction} to a rotational float for rotation purposes
	 */
	public static float getDirectionAngle(Direction direction) {
		if (direction.equals(Direction.NORTH))
			return 270f;
		else if (direction.equals(Direction.SOUTH))
			return 90f;
		else if (direction.equals(Direction.EAST))
			return 180f;
		else
			return 0f;
	}
}
