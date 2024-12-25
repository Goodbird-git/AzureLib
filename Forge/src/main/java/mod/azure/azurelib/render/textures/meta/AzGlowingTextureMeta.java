package mod.azure.azurelib.render.textures.meta;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.render.layer.AzAutoGlowingLayer;
import mod.azure.azurelib.util.JSONUtils;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Metadata class that stores the data for AzureLib's {@link AzAutoGlowingLayer emissive texture feature} for a given texture
 */
public class AzGlowingTextureMeta implements IMetadataSection {

	private List<Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>>> glowingSections = new ArrayList<>();

	public List<Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>>> getGlowingSections() {
		return this.glowingSections;
	}

	public void addSection(Tuple<Integer, Integer> pos1, Tuple<Integer, Integer> pos2) {
		Tuple<Tuple<Integer, Integer>, Tuple<Integer, Integer>> entry = new Tuple<>(pos1, pos2);
		this.glowingSections.add(entry);
	}

	public boolean isEmpty() {
		return this.glowingSections.isEmpty();
	}

	private final List<Pixel> pixels;

	public AzGlowingTextureMeta(List<Pixel> pixels) {
		this.pixels = pixels;
	}

	/**
	 * Generate the GlowLayer pixels list from an existing image resource, instead of using the .png.mcmeta file
	 */
	public static AzGlowingTextureMeta fromExistingImage(BufferedImage glowLayer) {
		List<Pixel> pixels = new ObjectArrayList<>();

		for (int x = 0; x < glowLayer.getWidth(); x++) {
			for (int y = 0; y < glowLayer.getHeight(); y++) {
				int color = glowLayer.getRGB(x, y);

				if (color != 0) {
					int alpha = (color >> 24) & 0xFF;
					int red = (color >> 16) & 0xFF;
					int green = (color >> 8) & 0xFF;
					int blue = color & 0xFF;

					// Modify this expression based on what "BufferedImage.get(color)" is expected to return
					int processedColor = processColor(alpha, red, green, blue);

					pixels.add(new Pixel(x, y, processedColor));
				}
			}
		}

		if (pixels.isEmpty())
			throw new IllegalStateException("Invalid glow layer texture provided, must have at least one pixel!");

		return new AzGlowingTextureMeta(pixels);
	}

	/**
	 * Create a new mask image based on the pre-determined pixel data
	 */
	public void createImageMask(BufferedImage originalImage, BufferedImage newImage) {
		for (Pixel pixel : this.pixels) {
			int color = originalImage.getRGB(pixel.x, pixel.y);

			if (pixel.alpha > 0) {
				int alpha = (color >> 24) & 0xFF;  // Extract alpha
				int red = (color >> 16) & 0xFF;    // Extract red
				int green = (color >> 8) & 0xFF;   // Extract green
				int blue = color & 0xFF;           // Extract blue

				color = (pixel.alpha << 24) | (blue << 16) | (green << 8) | red;
			}

			newImage.setRGB(pixel.x, pixel.y, color);
			originalImage.setRGB(pixel.x, pixel.y, 0);
		}
	}

	private static int processColor(int alpha, int red, int green, int blue) {
		return (alpha << 24) | (blue << 16) | (green << 8) | red;
	}

}
