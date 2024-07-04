/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.cache;

import mod.azure.azurelib.core.animatable.instance.SingletonAnimatableInstanceCache;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

/**
 * Storage class that keeps track of the last animatable id used, and provides new ones on request.<br>
 * Generally only used for {@link Item Items}, but any {@link SingletonAnimatableInstanceCache singleton} will likely use this.
 */
public final class AnimatableIdCache extends WorldSavedData {
	private static final String DATA_KEY = "AzureLib_id_cache";
	private long lastId;

	private AnimatableIdCache() {
		super(DATA_KEY);
	}

	/**
	 * Get the next free id from the id cache
	 * 
	 * @param level An arbitrary ServerWorld. It doesn't matter which one
	 * @return The next free ID, which is immediately reserved for use after calling this method
	 */
	public static long getFreeId(WorldServer level) {
		return getCache(level).getNextId();
	}

	private long getNextId() {
		this.isDirty();

		return ++this.lastId;
	}

	private static AnimatableIdCache getCache(WorldServer level) {
		DimensionSavedDataManager storage = level.getMinecraftServer().getWorld(0).getSavedData();

        return storage.getOrCreate(AnimatableIdCache::new, DATA_KEY);
	}

	/**
	 * Legacy wrapper for existing worlds pre-4.0.<br>
	 * Remove this at some point in the future
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		AnimatableIdCache legacyCache = new AnimatableIdCache();
		for (String key : tag.getKeySet()) {
			if (tag.hasKey(key, 99))
				legacyCache.lastId = Math.max(legacyCache.lastId, tag.getInteger(key));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setLong("last_id", this.lastId);

		return tag;
	}
}
