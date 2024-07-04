/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.mixin;

import mod.azure.azurelib.cache.texture.AnimatableTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
	@Shadow @Final private Map<ResourceLocation, Texture> mapTextureObjects;

	@Shadow public abstract void loadTexture(ResourceLocation resourceLocation, Texture abstractTexture);
	
	@Inject(method = "getTexture(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/texture/Texture;", at = @At("HEAD"))
	private void wrapAnimatableTexture(ResourceLocation path, CallbackInfoReturnable<Texture> callback) {
		Texture existing = this.mapTextureObjects.get(path);

		if (existing == null && !path.getNamespace().equals("minecraft")) {
			existing = new AnimatableTexture(path);

			loadTexture(path, existing);
		}
	}
}