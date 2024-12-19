package mod.azure.azurelib.core2.util;

import java.lang.ref.WeakReference;

/**
 * Gets a cached {@link WeakReference} from an object. Types implementing this interface should store a {@code final}
 * field of a WeakReference type containing a reference to {@code this}.
 *
 * @param <T> The type of the instance to hold a {@link WeakReference} to.
 */
public interface WeakSelfReference<T> {

    WeakReference<T> getOrCreateRef();

    static <T> WeakReference<T> getOrCreateRef(T target) {
        if (target instanceof WeakSelfReference<?> weakSelfReference) {
            @SuppressWarnings("unchecked")
            var ref = (WeakReference<T>) weakSelfReference.getOrCreateRef();
            return ref;
        }

        return new WeakReference<>(target);
    }
}
