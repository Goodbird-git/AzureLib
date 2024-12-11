package mod.azure.azurelib.core2.animation;

import org.jetbrains.annotations.Nullable;

public interface AzAnimatorAccessor<T> {
    @Nullable AzAnimator<T> getAnimator();
    void setAnimator(AzAnimator<T> animator);
}
