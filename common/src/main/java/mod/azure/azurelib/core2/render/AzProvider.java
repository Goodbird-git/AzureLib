package mod.azure.azurelib.core2.render;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.cache.AzBakedModelCache;

public class AzProvider<T> {

    private final Supplier<AzAnimator<T>> animatorSupplier;

    private final Function<T, ResourceLocation> modelLocationProvider;

    public AzProvider(
        Supplier<AzAnimator<T>> animatorSupplier,
        Function<T, ResourceLocation> modelLocationProvider
    ) {
        this.animatorSupplier = animatorSupplier;
        this.modelLocationProvider = modelLocationProvider;
    }

    public @Nullable AzBakedModel provideBakedModel(@NotNull T animatable) {
        var modelResourceLocation = modelLocationProvider.apply(animatable);
        return AzBakedModelCache.getInstance().getNullable(modelResourceLocation);
    }

    public @Nullable AzAnimator<T> provideAnimator(T animatable) {
        // TODO: Instead of caching the entire animator itself, we're going to want to cache the relevant data for the
        // entity.
        var accessor = AzAnimatorAccessor.cast(animatable);
        var cachedAnimator = accessor.getAnimatorOrNull();

        if (cachedAnimator == null) {
            // If the cached animator is null, create a new one. We use a separate reference here just for some
            cachedAnimator = animatorSupplier.get();

            if (cachedAnimator != null) {
                // If the new animator we created is not null, then register its controllers.
                cachedAnimator.registerControllers(
                    cachedAnimator.getAnimationControllerContainer()
                );
                // Also cache the animator so that the next time we fetch the animator, it's ready for us.
                accessor.setAnimator(cachedAnimator);
            }
        }

        return cachedAnimator;
    }
}
