/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;

/**
 * Helper class for segregating client-side code
 */
public final class ClientUtils {
	/**
	 * Get the player on the client
	 */
	public static EntityPlayerSP getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	/**
	 * Gets the current level on the client
	 */
	public static World getLevel() {
		return Minecraft.getMinecraft().world;
	}
}
