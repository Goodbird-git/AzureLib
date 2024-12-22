package mod.azure.azurelib.core.utils;

/**
 * Interpolation methods
 *
 * This class is responsible for doing different kind of interpolations. Cubic
 * interpolation code was from website below, but BauerCam also uses this code.
 *
 * @author mchorse
 * @link http://paulbourke.net/miscellaneous/interpolation/
 * @link https://github.com/daipenger/BauerCam
 */
public class Interpolations {
	/**
	 * Linear interpolation
	 */
	public static float lerp(float a, float b, float position) {
		return a + (b - a) * position;
	}

	/* --- Double versions of the functions --- */

	/**
	 * Linear interpolation
	 */
	public static double lerp(double a, double b, double position) {
		return a + (b - a) * position;
	}

	/**
	 * Special interpolation method for interpolating yaw. The problem with yaw, is
	 * that it may go in the "wrong" direction when having, for example, -170 (as a)
	 * and 170 (as b) degress or other way around (170 and -170).
	 *
	 * This interpolation method fixes this problem.
	 */
	public static double lerpYaw(double a, double b, double position) {
		a = MathHelper.wrapDegrees(a);
		b = MathHelper.wrapDegrees(b);

		return lerp(a, normalizeYaw(a, b), position);
	}

	/**
	 * Normalize yaw rotation (argument {@code b}) based on the previous yaw
	 * rotation.
	 */
	public static double normalizeYaw(double a, double b) {
		double diff = a - b;

		if (diff > 180 || diff < -180) {
			diff = Math.copySign(360 - Math.abs(diff), diff);

			return a + diff;
		}

		return b;
	}
}
