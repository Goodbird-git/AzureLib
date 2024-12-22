/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.loading;

import com.google.gson.JsonObject;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.primitive.AzBakedAnimation;
import mod.azure.azurelib.animation.primitive.AzBakedAnimations;
import mod.azure.azurelib.loading.json.raw.Model;
import mod.azure.azurelib.util.JSONUtils;
import mod.azure.azurelib.util.JsonUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Extracts raw information from given files, and other similar functions
 */
public final class FileLoader {

	/**
	 * Load up and deserialize an animation json file to its respective {@link AzBakedAnimation} components
	 *
	 * @param location The resource path of the animations file
	 * @param manager  The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
	 */
	public static AzBakedAnimations loadAzAnimationsFile(ResourceLocation location, IResourceManager manager) {
		return JsonUtil.GEO_GSON.fromJson(loadFile(location, manager), AzBakedAnimations.class);
	}

	/**
	 * Load up and deserialize a geo model json file to its respective format
	 * 
	 * @param location The resource path of the model file
	 * @param manager  The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
	 */
	public static Model loadModelFile(ResourceLocation location, IResourceManager manager) {
		return JsonUtil.GEO_GSON.fromJson(loadFile(location, manager), Model.class);
	}

	/**
	 * Load a given json file into memory
	 * 
	 * @param location The resource path of the json file
	 * @param manager  The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
	 */
	public static JsonObject loadFile(ResourceLocation location, IResourceManager manager) {
		return JSONUtils.fromJson(JsonUtil.GEO_GSON, getFileContents(location, manager), JsonObject.class);
	}

	/**
	 * Read a text-based file into memory in the form of a single string
	 * 
	 * @param location The resource path of the file
	 * @param manager  The Minecraft {@code ResourceManager} responsible for maintaining in-memory resource access
	 */
	public static String getFileContents(ResourceLocation location, IResourceManager manager) {
		try (InputStream inputStream = manager.getResource(location).getInputStream()) {
			return IOUtils.toString(inputStream, Charset.defaultCharset());
		} catch (Exception e) {
			AzureLib.LOGGER.error("Couldn't load " + location, e);

			throw new RuntimeException(new FileNotFoundException(location.toString()));
		}
	}
}
