/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.util;

import mod.azure.azurelib.cache.object.GeoCube;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Quaternion;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * Helper class for various methods and functions useful while rendering
 */
public final class RenderUtils {

	public static boolean isMultipleOf(int p_265754_, int p_265543_) {
		return p_265754_ % p_265543_ == 0;
	}

	private static final long START_TIME = System.nanoTime();

	public static void translateMatrixToBone(CoreGeoBone bone) {
		GlStateManager.translate(-bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
	}

	public static void rotateMatrixAroundBone(CoreGeoBone bone) {
		if (bone.getRotZ() != 0)
			GlStateManager.rotate(bone.getRotZ(), 0, 0, 1);

		if (bone.getRotY() != 0)
			GlStateManager.rotate(bone.getRotY(), 0, 1, 0);

		if (bone.getRotX() != 0)
			GlStateManager.rotate(bone.getRotX(), 1, 0, 0);
	}

	public static void rotateMatrixAroundCube(GeoCube cube) {
		Vec3d rotation = cube.rotation();

		GlStateManager.rotate(new Quaternion(0, 0, (float) rotation.z, 0));
		GlStateManager.rotate(new Quaternion(0, (float) rotation.y, 0, 0));
		GlStateManager.rotate(new Quaternion((float) rotation.x, 0, 0, 0));
	}

	public static void scaleMatrixForBone(CoreGeoBone bone) {
		GlStateManager.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translateToPivotPoint(GeoCube cube) {
		Vec3d pivot = cube.pivot();
		GlStateManager.translate(pivot.x / 16f, pivot.y / 16f, pivot.z / 16f);
	}

	public static void translateToPivotPoint(CoreGeoBone bone) {
		GlStateManager.translate(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);
	}

	public static void translateAwayFromPivotPoint(GeoCube cube) {
		Vec3d pivot = cube.pivot();

		GlStateManager.translate(-pivot.x / 16f, -pivot.y / 16f, -pivot.z / 16f);
	}

	public static void translateAwayFromPivotPoint(CoreGeoBone bone) {
		GlStateManager.translate(-bone.getPivotX() / 16f, -bone.getPivotY() / 16f, -bone.getPivotZ() / 16f);
	}

	public static void translateAndRotateMatrixForBone(CoreGeoBone bone) {
		translateToPivotPoint(bone);
		rotateMatrixAroundBone(bone);
	}

	public static void prepMatrixForBone(CoreGeoBone bone) {
		translateMatrixToBone(bone);
		translateToPivotPoint(bone);
		rotateMatrixAroundBone(bone);
		scaleMatrixForBone(bone);
		translateAwayFromPivotPoint(bone);
	}

	public static Matrix4f invertAndMultiplyMatrices(Matrix4f baseMatrix, Matrix4f inputMatrix) {
		inputMatrix = new Matrix4f(inputMatrix);

		inputMatrix.invert();
		inputMatrix.mul(baseMatrix);

		return inputMatrix;
	}

	/**
	 * Translates the provided {@link GlStateManager} to face towards the given {@link Entity}'s rotation.<br>
	 * Usually used for rotating projectiles towards their trajectory<br>
	 */
	public static void faceRotation(Entity animatable, float partialTick) {
		GlStateManager.rotate(lerp(partialTick, animatable.prevRotationYaw, animatable.rotationYaw) - 90, 0, 1, 0);
		GlStateManager.rotate(lerp(partialTick, animatable.prevRotationPitch, animatable.rotationPitch), 0, 0, 1);
	}

	public static float lerp(float pct, float start, float end) {
		return start + pct * (end - start);
	}

	public static double getTime() {
		return (System.nanoTime() - START_TIME) / 1_000_000_000.0;
	}

	/**
	 * Returns the current time (in ticks) that the instance has been running. This is effectively a permanent timer that counts up since the game was launched.
	 */
	public static double getCurrentTick() {
		return getTime() * 20d;
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
	public static void fixInvertedFlatCube(GeoCube cube, Vector3f normal) {
		if (normal.x < 0 && (cube.size().y == 0 || cube.size().z == 0)) {
			normal.x *= -1;
		}

		if (normal.y < 0 && (cube.size().x == 0 || cube.size().z == 0)) {
			normal.y *= -1;
		}

		if (normal.z < 0 && (cube.size().x == 0 || cube.size().y == 0)) {
			normal.z *= -1;
		}
	}

	public static Matrix4f getCurrentMatrix() {
		MatrixUtils.matrix = null;
		MatrixUtils.captureMatrix();
		return MatrixUtils.matrix;
	}

	public static void mul(Vec3d normal, float x, float y, float z) {
		normal = new Vec3d(normal.x * x, normal.y * y, normal.z * z);
	}

	/**
	 * Converts a {@link EnumFacing} to a rotational float for rotation purposes
	 */
	public static float getDirectionAngle(EnumFacing direction) {
		if (direction.equals(EnumFacing.NORTH))
			return 270f;
		else if (direction.equals(EnumFacing.SOUTH))
			return 90f;
		else if (direction.equals(EnumFacing.EAST))
			return 180f;
		else
			return 0f;
	}
}
