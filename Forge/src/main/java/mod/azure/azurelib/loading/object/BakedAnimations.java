/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.loading.object;

import mod.azure.azurelib.core.animation.Animation;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * Container object that holds a deserialized map of {@link Animation Animations}.<br>
 * Kept as a unique object so that it can be registered as a {@link com.google.gson.JsonDeserializer deserializer} for {@link com.google.gson.Gson Gson}
 */
@Deprecated
public class BakedAnimations {
	
	protected final Map<String, Animation> animations;
	protected final Map<String, ResourceLocation> includes;
	
	public BakedAnimations(Map<String, Animation> animations, Map<String, ResourceLocation> includes) {
		this.animations = animations;
		this.includes = includes;
	}
	
	public Map<String, Animation> animations() {
		return this.animations;
	}
	
	public Map<String, ResourceLocation> includes() {
		return this.includes;
	}
	
}
