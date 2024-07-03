/**
 * This class is a fork of the matching class found in the Geckolib repository.
 * Original source: https://github.com/bernie-g/geckolib
 * Copyright © 2024 Bernie-G.
 * Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core.animatable.instance;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animation.AnimatableManager;

/**
 * AnimatableInstanceCache implementation for singleton/flyweight objects such as Items. Utilises a keyed map to
 * differentiate different instances of the object.
 */
public class SingletonAnimatableInstanceCache extends AnimatableInstanceCache {

    protected final Long2ObjectMap<AnimatableManager<?>> managers = new Long2ObjectOpenHashMap<>();

    public SingletonAnimatableInstanceCache(GeoAnimatable animatable) {
        super(animatable);
    }

    /**
     * Gets an {@link AnimatableManager} instance from this cache, cached under the id provided, or a new one if one
     * doesn't already exist.<br>
     * This subclass assumes that all animatable instances will be sharing this cache instance, and so differentiates
     * data by ids.
     */
    @Override
    public AnimatableManager<?> getManagerForId(long uniqueId) {
        return this.managers.computeIfAbsent(uniqueId, key -> new AnimatableManager<>(this.animatable));
    }
}
