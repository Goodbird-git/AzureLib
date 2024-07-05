/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.mixin;

import mod.azure.azurelib.cache.texture.AnimatableTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
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
	@Shadow @Final private Map<ResourceLocation, ITextureObject> mapTextureObjects;

	@Shadow public abstract boolean loadTexture(ResourceLocation resourceLocation, ITextureObject abstractTexture);
	
	@Inject(method = "getTexture", at = @At("HEAD"))
	private void wrapAnimatableTexture(ResourceLocation path, CallbackInfoReturnable<ITextureObject> callback) {
		ITextureObject existing = this.mapTextureObjects.get(path);

		if (existing == null && !path.getResourceDomain().equals("minecraft")) {
			existing = new AnimatableTexture(path);

			loadTexture(path, existing);
		}
	}
}