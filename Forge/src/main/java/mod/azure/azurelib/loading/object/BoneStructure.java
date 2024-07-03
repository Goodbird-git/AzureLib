/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.loading.object;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.loading.json.raw.Bone;

import java.util.Map;

/**
 * Container class for holding a {@link Bone} structure. Used at startup in deserialization
 */
public record BoneStructure(Bone self, Map<String, BoneStructure> children) {
	public BoneStructure(Bone self) {
		this(self, new Object2ObjectOpenHashMap<>());
	}
}
