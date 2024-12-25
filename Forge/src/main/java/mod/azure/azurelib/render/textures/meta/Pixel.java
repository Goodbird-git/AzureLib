package mod.azure.azurelib.render.textures.meta;

public class Pixel {
	public int x;
	public int y;
	public int alpha;

	/**
	 * A pixel marker for a glowlayer mask
	 *
	 * @param x     The X coordinate of the pixel
	 * @param y     The Y coordinate of the pixel
	 * @param alpha The alpha value of the mask
	 */
	public Pixel(int x, int y, int alpha) {
		this.x = x;
		this.y = y;
		this.alpha = alpha;
	}
}