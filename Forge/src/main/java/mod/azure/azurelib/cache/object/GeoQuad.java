/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.cache.object;

import mod.azure.azurelib.loading.json.raw.FaceUV;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

import javax.vecmath.Vector3f;

/**
 * Quad data holder
 */
public class GeoQuad {

	private GeoVertex[] vertices;
	private Vec3i normal;
	private EnumFacing direction;

	public GeoQuad(GeoVertex[] vertices, Vec3i normal, EnumFacing direction) {
		this.vertices = vertices;
		this.normal = normal;
		this.direction = direction;
	}

	public GeoVertex[] getVertices() {
		return vertices;
	}

	public Vec3i getNormal() {
		return normal;
	}

	public EnumFacing getDirection() {
		return direction;
	}

	public static GeoQuad build(
			GeoVertex[] vertices,
			double[] uvCoords,
			double[] uvSize,
			FaceUV.Rotation uvRotation,
			float texWidth,
			float texHeight,
			boolean mirror,
			EnumFacing direction
	) {
		return build(
				vertices,
				(float) uvCoords[0],
				(float) uvCoords[1],
				(float) uvSize[0],
				(float) uvSize[1],
				uvRotation,
				texWidth,
				texHeight,
				mirror,
				direction,
				direction.getDirectionVec()
		);
	}

	public static GeoQuad build(
			GeoVertex[] vertices,
			float u,
			float v,
			float uSize,
			float vSize,
			FaceUV.Rotation uvRotation,
			float texWidth,
			float texHeight,
			boolean mirror,
			EnumFacing direction,
			Vec3i normal
	) {
		float uWidth = (u + uSize) / texWidth;
		float vHeight = (v + vSize) / texHeight;
		u /= texWidth;
		v /= texHeight;

		if (!mirror) {
			float tempWidth = uWidth;
			uWidth = u;
			u = tempWidth;
		} else {
			int x = normal.getX();
			x *= -1;
		}

		float[] uvs = uvRotation.rotateUvs(u, v, uWidth, vHeight);
		vertices[0] = vertices[0].withUVs(uvs[0], uvs[1]);
		vertices[1] = vertices[1].withUVs(uvs[2], uvs[3]);
		vertices[2] = vertices[2].withUVs(uvs[4], uvs[5]);
		vertices[3] = vertices[3].withUVs(uvs[6], uvs[7]);

		return new GeoQuad(vertices, normal, direction);
	}
}
