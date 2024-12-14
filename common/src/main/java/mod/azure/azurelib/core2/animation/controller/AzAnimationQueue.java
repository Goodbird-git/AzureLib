package mod.azure.azurelib.core2.animation.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;

public class AzAnimationQueue<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzAnimationQueue.class);

    private final Queue<AzQueuedAnimation> animationQueue;

    public AzAnimationQueue() {
        this.animationQueue = new LinkedList<>();
    }

    public void add(@NotNull AzQueuedAnimation queuedAnimation) {
        animationQueue.add(queuedAnimation);
    }

    public void addAll(@NotNull Collection<AzQueuedAnimation> queuedAnimations) {
        animationQueue.addAll(queuedAnimations);
    }

    public @Nullable AzQueuedAnimation peek() {
        return animationQueue.peek();
    }

    public @Nullable AzQueuedAnimation next() {
        return animationQueue.poll();
    }

    public void clear() {
        animationQueue.clear();
    }

    public boolean isEmpty() {
        return animationQueue.isEmpty();
    }
}
