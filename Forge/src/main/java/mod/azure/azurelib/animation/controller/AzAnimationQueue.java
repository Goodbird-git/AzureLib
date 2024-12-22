package mod.azure.azurelib.animation.controller;

import mod.azure.azurelib.animation.primitive.AzQueuedAnimation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a queue of animations to be processed in a sequential manner. This class manages a collection of
 * {@link AzQueuedAnimation} objects, allowing animations to be queued, retrieved, and cleared efficiently. It ensures
 * that animations are processed in the order they are added. <br/>
 * <br/>
 * The queue supports operations to inspect the next animation without removal, retrieve and remove the next animation,
 * add individual or multiple animations, and clear the entire queue. Additionally, it provides a method to determine if
 * the queue is empty.
 */
public class AzAnimationQueue {

    private final Queue<AzQueuedAnimation> animationQueue;

    public AzAnimationQueue() {
        this.animationQueue = new LinkedList<>();
    }

    public void add(AzQueuedAnimation queuedAnimation) {
        animationQueue.add(queuedAnimation);
    }

    public void addAll(Collection<AzQueuedAnimation> queuedAnimations) {
        animationQueue.addAll(queuedAnimations);
    }

    public AzQueuedAnimation peek() {
        return animationQueue.peek();
    }

    public AzQueuedAnimation next() {
        return animationQueue.poll();
    }

    public void clear() {
        animationQueue.clear();
    }

    public boolean isEmpty() {
        return animationQueue.isEmpty();
    }
}
