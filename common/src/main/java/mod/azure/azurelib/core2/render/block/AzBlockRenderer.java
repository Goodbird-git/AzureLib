package mod.azure.azurelib.core2.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.impl.AzBlockAnimator;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.cache.AzBakedModelCache;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.impl.AzBlockEntityRendererPipeline;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AzBlockRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    private final AzBlockEntityRendererPipeline<T> rendererPipeline;

    private final List<AzRenderLayer<T>> renderLayers;

    private float scaleWidth = 1;

    private float scaleHeight = 1;

    @Nullable
    private AzBlockAnimator<T> reusedAzBlockAnimator;

    protected AzBlockRenderer() {
        super();
        this.rendererPipeline = new AzBlockEntityRendererPipeline<>(this);
        this.renderLayers = new ObjectArrayList<>();
    }

    protected abstract @NotNull ResourceLocation getModelLocation(T entity);

    public abstract @NotNull ResourceLocation getTextureLocation(T entity);

    @Override
    public void render(@NotNull T entity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {

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

    protected @Nullable AzBlockAnimator<T> createAnimator() {
        return null;
    }

    protected @Nullable AzBakedModel provideBakedModel(@NotNull T entity) {
        var modelResourceLocation = getModelLocation(entity);
        return AzBakedModelCache.getInstance().getNullable(modelResourceLocation);
    }

    protected @Nullable AzBlockAnimator<T> provideAnimator(T entity) {
        // TODO: Instead of caching the entire animator itself, we're going to want to cache the relevant data for the
        // entity.
        var accessor = AzAnimatorAccessor.cast(entity);
        var cachedBlockEntityAnimator = (AzBlockAnimator<T>) accessor.getAnimatorOrNull();

        if (cachedBlockEntityAnimator == null) {
            // If the cached animator is null, create a new one. We use a separate reference here just for some
            cachedBlockEntityAnimator = createAnimator();

            if (cachedBlockEntityAnimator != null) {
                // If the new animator we created is not null, then register its controllers.
                cachedBlockEntityAnimator.registerControllers(cachedBlockEntityAnimator.getAnimationControllerContainer());
                // Also cache the animator so that the next time we fetch the animator, it's ready for us.
                accessor.setAnimator(cachedBlockEntityAnimator);
            }
        }

        return cachedBlockEntityAnimator;
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public AzBlockRenderer<T> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public AzBlockRenderer<T> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;

        return this;
    }

    /**
     * Returns the list of registered {@link AzRenderLayer GeoRenderLayers} for this renderer
     */
    public List<AzRenderLayer<T>> getRenderLayers() {
        return this.renderLayers;
    }

    /**
     * Adds a {@link AzRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public AzBlockRenderer<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
        this.renderLayers.add(renderLayer);

        return this;
    }

    public AzBlockAnimator<T> getAnimator() {
        return reusedAzBlockAnimator;
    }

    public float getScaleHeight() {
        return scaleHeight;
    }

    public float getScaleWidth() {
        return scaleWidth;
    }
}
