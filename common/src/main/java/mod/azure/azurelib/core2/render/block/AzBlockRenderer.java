package mod.azure.azurelib.core2.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.impl.AzBlockAnimator;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.cache.AzBakedModelCache;
import mod.azure.azurelib.core2.render.AzBlockEntityRendererConfig;
import mod.azure.azurelib.core2.render.pipeline.block.AzBlockEntityRendererPipeline;

public abstract class AzBlockRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    private final AzBlockEntityRendererConfig<T> config;

    private final AzBlockEntityRendererPipeline<T> rendererPipeline;

    @Nullable
    private AzBlockAnimator<T> reusedAzBlockAnimator;

    protected AzBlockRenderer(AzBlockEntityRendererConfig<T> config) {
        super();
        this.config = config;
        this.rendererPipeline = new AzBlockEntityRendererPipeline<>(config, this);
    }

    @Override
    public void render(
        @NotNull T entity,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource multiBufferSource,
        int packedLight,
        int packedOverlay
    ) {
        var cachedEntityAnimator = provideAnimator(entity);
        var azBakedModel = provideBakedModel(entity);

        if (cachedEntityAnimator != null && azBakedModel != null) {
            cachedEntityAnimator.setActiveModel(azBakedModel);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzBlockAnimator = cachedEntityAnimator;

        // Execute the render pipeline.
        rendererPipeline.render(
            poseStack,
            azBakedModel,
            entity,
            multiBufferSource,
            null,
            null,
            0,
            partialTick,
            packedLight
        );
    }

    protected @Nullable AzBakedModel provideBakedModel(@NotNull T entity) {
        var modelResourceLocation = config.modelLocation(entity);
        return AzBakedModelCache.getInstance().getNullable(modelResourceLocation);
    }

    protected @Nullable AzBlockAnimator<T> provideAnimator(T entity) {
        // TODO: Instead of caching the entire animator itself, we're going to want to cache the relevant data for the
        // entity.
        var accessor = AzAnimatorAccessor.cast(entity);
        var cachedBlockEntityAnimator = (AzBlockAnimator<T>) accessor.getAnimatorOrNull();

        if (cachedBlockEntityAnimator == null) {
            // If the cached animator is null, create a new one. We use a separate reference here just for some
            cachedBlockEntityAnimator = (AzBlockAnimator<T>) config.createAnimator();

            if (cachedBlockEntityAnimator != null) {
                // If the new animator we created is not null, then register its controllers.
                cachedBlockEntityAnimator.registerControllers(
                    cachedBlockEntityAnimator.getAnimationControllerContainer()
                );
                // Also cache the animator so that the next time we fetch the animator, it's ready for us.
                accessor.setAnimator(cachedBlockEntityAnimator);
            }
        }

        return cachedBlockEntityAnimator;
    }

    public AzBlockAnimator<T> getAnimator() {
        return reusedAzBlockAnimator;
    }
}
