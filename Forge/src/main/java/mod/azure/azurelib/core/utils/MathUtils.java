package mod.azure.azurelib.core.utils;

public class MathUtils {

	public static double clamp(double x, double min, double max) {
		return x < min ? min : (x > max ? max : x);
	}
}
