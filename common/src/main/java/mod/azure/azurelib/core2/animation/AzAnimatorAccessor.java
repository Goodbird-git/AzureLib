package mod.azure.azurelib.core2.animation;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface AzAnimatorAccessor<T> {

    @Nullable
    AzAnimator<T> getAnimatorOrNull();

    void setAnimator(AzAnimator<T> animator);

    default Optional<AzAnimator<T>> getAnimator() {
        return Optional.ofNullable(getAnimatorOrNull());
    }

    @SuppressWarnings("unchecked")
    static <T> AzAnimatorAccessor<T> cast(T target) {
        return (AzAnimatorAccessor<T>) target;
    }

    static <T> AzAnimator<T> getOrNull(T target) {
        return cast(target).getAnimatorOrNull();
    }

    static <T> Optional<AzAnimator<T>> get(T target) {
        return Optional.ofNullable(getOrNull(target));
    }
}
