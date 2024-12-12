package mod.azure.azurelib.core2.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

import mod.azure.azurelib.core.state.BoneSnapshot;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;

public class AzBoneSnapshotCache {

    private final Map<String, BoneSnapshot> boneSnapshots;

    public AzBoneSnapshotCache() {
        this.boneSnapshots = new Object2ObjectOpenHashMap<>();
    }

    /**
     * Cache the relevant {@link BoneSnapshot BoneSnapshots} for the current {@link AzQueuedAnimation} for animation
     * lerping
     *
     * @param animation The {@code QueuedAnimation} to filter {@code BoneSnapshots} for
     * @param snapshots The master snapshot collection to pull filter from
     */
    public void put(AzQueuedAnimation animation, Collection<BoneSnapshot> snapshots) {
        if (animation.animation().boneAnimations() == null) {
            return;
        }

        for (var snapshot : snapshots) {
            for (var boneAnimation : animation.animation().boneAnimations()) {
                if (boneAnimation.boneName().equals(snapshot.getBone().getName())) {
                    boneSnapshots.put(boneAnimation.boneName(), BoneSnapshot.copy(snapshot));
                    break;
                }
            }
        }
    }

    public @Nullable BoneSnapshot getOrNull(String name) {
        return boneSnapshots.get(name);
    }
}
