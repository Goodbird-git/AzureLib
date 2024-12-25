package mod.azure.azurelib.render.textures.meta;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.render.layer.AzAutoGlowingLayer;
import mod.azure.azurelib.util.JSONUtils;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSectionSerializer;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Metadata class that stores the data for AzureLib's {@link AzAutoGlowingLayer emissive texture feature} for a given texture
 */
public class AzGlowingTextureMeta implements IMetadataSection {
	public static final IMetadataSectionSerializer<AzGlowingTextureMeta> DESERIALIZER = new IMetadataSectionSerializer<AzGlowingTextureMeta>() {
		@Override
		public AzGlowingTextureMeta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			List<Pixel> pixels = fromSections(JSONUtils.getJsonArray(json, "sections"));

			if (pixels.isEmpty())
				throw new JsonParseException("Empty glowlayer sections file. Must have at least one glow section!");

			return new AzGlowingTextureMeta(pixels);
		}

		@Override
		public String getSectionName() {
			return "glowsections";
		}

		/**
		 * Generate a {@link Pixel} collection from the "sections" array of the mcmeta file
		 */
		private List<Pixel> fromSections(@Nullable JsonArray sectionsArray) {
			if (sectionsArray == null)
				return new ObjectArrayList<>();

			List<Pixel> pixels = new ObjectArrayList<>();

			for (JsonElement element : sectionsArray) {
				if (!(element instanceof JsonObject))
					throw new JsonParseException("Invalid glowsections json format, expected a JsonObject, found: " + element.getClass());

				int x1 = JSONUtils.getInt((JsonObject) element, "x1", JSONUtils.getInt((JsonObject) element, "x", 0));
				int y1 = JSONUtils.getInt((JsonObject) element, "y1", JSONUtils.getInt((JsonObject) element, "y", 0));
				int x2 = JSONUtils.getInt((JsonObject) element, "x2", JSONUtils.getInt((JsonObject) element, "w", 0) + x1);
				int y2 = JSONUtils.getInt((JsonObject) element, "y2", JSONUtils.getInt((JsonObject) element, "h", 0) + y1);
				int alpha = JSONUtils.getInt((JsonObject) element, "alpha", JSONUtils.getInt((JsonObject) element, "a", 0));

				if (x1 + y1 + x2 + y2 == 0)
					throw new IllegalArgumentException("Invalid glowsections section object, section must be at least one pixel in size");

				for (int x = x1; x <= x2; x++) {
					for (int y = y1; y <= y2; y++) {
						pixels.add(new Pixel(x, y, alpha));
					}
				}
			}

			return pixels;
		}
	};

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
