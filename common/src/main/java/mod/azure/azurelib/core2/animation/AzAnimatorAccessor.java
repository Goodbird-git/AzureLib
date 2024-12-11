package mod.azure.azurelib.core2.animation;

public interface AzAnimatorAccessor<T> {
    AzAnimator<T> getAnimator();
    void setAnimator(AzAnimator<T> animator);
}
