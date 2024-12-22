package mod.azure.azurelib.core2.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core2.animation.AzAnimationContext;
import mod.azure.azurelib.core2.animation.AzBoneAnimationUpdateUtil;
import mod.azure.azurelib.core2.animation.cache.AzBoneCache;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzBoneAnimationQueue;

/**
 * The AzBoneAnimationQueueCache class is responsible for managing and updating animation queues for bones. It acts as a
 * cache that maps bone names to their respective animation queues, enabling efficient updates and access.
 *
 * @param <T> the type of the animatable object used in the animation context
 */
public class AzBoneAnimationQueueCache<T> {

    private final Map<String, AzBoneAnimationQueue> boneAnimationQueues;

    private final AzBoneCache boneCache;

    public AzBoneAnimationQueueCache(AzBoneCache boneCache) {
        this.boneAnimationQueues = new Object2ObjectOpenHashMap<>();
        this.boneCache = boneCache;
    }

    public void update(AzAnimationContext<T> context, Function<T, EasingType> overridingEasingTypeFunction) {
        var animatable = context.animatable();
        var boneSnapshots = boneCache.getBoneSnapshotsByName();
        var easingType = overridingEasingTypeFunction.apply(animatable);

        for (var boneAnimation : boneAnimationQueues.values()) {
            var bone = boneAnimation.bone();
            var snapshot = boneSnapshots.get(bone.getName());
            var initialSnapshot = bone.getInitialAzSnapshot();

            AzBoneAnimationUpdateUtil.updateRotations(boneAnimation, bone, easingType, initialSnapshot, snapshot);
            AzBoneAnimationUpdateUtil.updatePositions(boneAnimation, bone, easingType, snapshot);
            AzBoneAnimationUpdateUtil.updateScale(boneAnimation, bone, easingType, snapshot);
        }
    }

    public Collection<AzBoneAnimationQueue> values() {
        return boneAnimationQueues.values();
    }

    public @Nullable AzBoneAnimationQueue getOrNull(String boneName) {
        var bone = boneCache.getBakedModel().getBoneOrNull(boneName);

        if (bone == null) {
            return null;
        }

        return boneAnimationQueues.computeIfAbsent(boneName, $ -> new AzBoneAnimationQueue(bone));
    }

    public void clear() {
        boneAnimationQueues.clear();
    }
}
