/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.render.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Abstract texture wrapper for AzureLib textures.<br>
 * Mostly just handles boilerplate
 */
public abstract class AzAbstractTexture extends AbstractTexture {
	/**
	 * Generates the texture instance for the given path with the given appendix if it hasn't already been generated
	 */
	protected static void generateTexture(ResourceLocation texturePath, Consumer<TextureManager> textureManagerConsumer) {
		if (!RenderSystem.isOnRenderThreadOrInit())
			throw new IllegalThreadStateException("Texture loading called outside of the render thread! This should DEFINITELY not be happening.");

		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

		if (!(textureManager.getTexture(texturePath) instanceof AzAbstractTexture))
			textureManagerConsumer.accept(textureManager);
	}

	@Override
	public final void loadTexture(IResourceManager resourceManager) throws IOException {
		IRenderCall renderCall = loadTexture(resourceManager, Minecraft.getMinecraft());

		if (renderCall == null)
			return;

		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(renderCall);
		} else {
			renderCall.execute();
		}
	}

	@Nullable
	protected abstract IRenderCall loadTexture(IResourceManager resourceManager, Minecraft mc) throws IOException;

	/**
	 * No-frills helper method for uploading {@link BufferedImage images} into memory for use
	 */
	public static void uploadSimple(int texture, BufferedImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(texture, 0, image.getWidth(), image.getHeight());
		image.uploadTextureSub(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), blur, clamp, false, true);
	}

	public static ResourceLocation appendToPath(ResourceLocation location, String suffix) {
		String path = location.getResourcePath();
		int i = path.lastIndexOf('.');

		return new ResourceLocation(location.getResourceDomain(), path.substring(0, i) + suffix + path.substring(i));
	}
}
