/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.cache.texture;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.IRenderCall;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Abstract texture wrapper for AzureLib textures.<br>
 * Mostly just handles boilerplate
 */
public abstract class GeoAbstractTexture extends Texture {
	/**
	 * Generates the texture instance for the given path with the given appendix if it hasn't already been generated
	 */
	protected static void generateTexture(ResourceLocation texturePath, Consumer<TextureManager> textureManagerConsumer) {
		if (!RenderSystem.isOnRenderThreadOrInit())
			throw new IllegalThreadStateException("Texture loading called outside of the render thread! This should DEFINITELY not be happening.");

		TextureManager textureManager = Minecraft.getInstance().getTextureManager();

		if (!(textureManager.getTexture(texturePath) instanceof GeoAbstractTexture))
			textureManagerConsumer.accept(textureManager);
	}

	@Override
	public final void loadTexture(IResourceManager resourceManager) throws IOException {
		IRenderCall renderCall = loadTexture(resourceManager, Minecraft.getInstance());

		if (renderCall == null)
			return;

		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(renderCall);
		} else {
			renderCall.execute();
		}
	}

	/**
	 * Debugging function to write out the generated glowmap image to disk
	 */
	protected void printDebugImageToDisk(ResourceLocation id, NativeImage newImage) {
		try {
			File file = new File(FMLPaths.GAMEDIR.get().toFile(), "GeoTexture Debug Printouts");

			if (!file.exists()) {
				file.mkdirs();
			} else if (!file.isDirectory()) {
				file.delete();
				file.mkdirs();
			}

			file = new File(file, id.getPath().replace('/', '.'));

			if (!file.exists())
				file.createNewFile();

			newImage.write(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Called at {@link NativeImage#load} time to load this texture for the first time into the render cache. Generate and apply the necessary functions here, then return the RenderCall to submit to the render pipeline.
	 * 
	 * @return The RenderCall to submit to the render pipeline, or null if no further action required
	 */
	@Nullable
	protected abstract IRenderCall loadTexture(IResourceManager resourceManager, Minecraft mc) throws IOException;

	/**
	 * No-frills helper method for uploading {@link NativeImage images} into memory for use
	 */
	public static void uploadSimple(int texture, NativeImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(texture, 0, image.getWidth(), image.getHeight());
		image.uploadTextureSub(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
	}

	public static ResourceLocation appendToPath(ResourceLocation location, String suffix) {
		String path = location.getPath();
		int i = path.lastIndexOf('.');

		return new ResourceLocation(location.getNamespace(), path.substring(0, i) + suffix + path.substring(i));
	}
}
