package mod.azure.azurelib.core.utils;

public class MathHelper {
	/**
	 * the angle is reduced to an angle between -180 and +180 by mod, and a 360
	 * check
	 */
	public static float wrapDegrees(float value) {
		value = value % 360.0F;

		if (value >= 180.0F) {
			value -= 360.0F;
		}

		if (value < -180.0F) {
			value += 360.0F;
		}

		return value;
	}

	/**
	 * the angle is reduced to an angle between -180 and +180 by mod, and a 360
	 * check
	 */
	public static double wrapDegrees(double value) {
		value = value % 360.0D;

		if (value >= 180.0D) {
			value -= 360.0D;
		}

		if (value < -180.0D) {
			value += 360.0D;
		}

		return value;
	}
}
