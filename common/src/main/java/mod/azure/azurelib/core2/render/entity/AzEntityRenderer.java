package mod.azure.azurelib.core2.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.cache.AzBakedModelCache;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.impl.AzEntityRendererPipeline;

public abstract class AzEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    private final AzEntityRendererPipeline<T> rendererPipeline;

    private final List<AzRenderLayer<T>> renderLayers;

    private float scaleWidth = 1;

    private float scaleHeight = 1;

    @Nullable
    private AzEntityAnimator<T> reusedAzEntityAnimator;

    protected AzEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.rendererPipeline = new AzEntityRendererPipeline<>(this);
        this.renderLayers = new ObjectArrayList<>();
    }

    protected abstract @NotNull ResourceLocation getModelLocation(T entity);

    public void superRender(
        @NotNull T entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void render(
        @NotNull T entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        var cachedEntityAnimator = provideAnimator(entity);
        var azBakedModel = provideBakedModel(entity);

        if (cachedEntityAnimator != null && azBakedModel != null) {
            cachedEntityAnimator.setActiveModel(azBakedModel);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzEntityAnimator = cachedEntityAnimator;

        // Execute the render pipeline.
        rendererPipeline.render(
            poseStack,
            azBakedModel,
            entity,
            bufferSource,
            null,
            null,
            entityYaw,
            partialTick,
            packedLight
        );
    }

    protected @Nullable AzEntityAnimator<T> createAnimator() {
        return null;
    }

    protected @Nullable AzBakedModel provideBakedModel(@NotNull T entity) {
        var modelResourceLocation = getModelLocation(entity);
        return AzBakedModelCache.getInstance().getNullable(modelResourceLocation);
    }

    protected @Nullable AzEntityAnimator<T> provideAnimator(T entity) {
        // TODO: Instead of caching the entire animator itself, we're going to want to cache the relevant data for the
        // entity.
        var accessor = AzAnimatorAccessor.cast(entity);
        var cachedEntityAnimator = (AzEntityAnimator<T>) accessor.getAnimatorOrNull();

        if (cachedEntityAnimator == null) {
            // If the cached animator is null, create a new one. We use a separate reference here just for some
            cachedEntityAnimator = createAnimator();

            if (cachedEntityAnimator != null) {
                // If the new animator we created is not null, then register its controllers.
                cachedEntityAnimator.registerControllers(cachedEntityAnimator.getAnimationControllerContainer());
                // Also cache the animator so that the next time we fetch the animator, it's ready for us.
                accessor.setAnimator(cachedEntityAnimator);
            }
        }

        return cachedEntityAnimator;
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public AzEntityRenderer<T> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public AzEntityRenderer<T> withScale(float scaleWidth, float scaleHeight) {
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
    public AzEntityRenderer<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
        this.renderLayers.add(renderLayer);

        return this;
    }

    /**
     * Whether the entity's nametag should be rendered or not.<br>
     * Pretty much exclusively used in {@link EntityRenderer#renderNameTag}
     */
    @Override
    public boolean shouldShowName(@NotNull T entity) {
        return AzEntityNameRenderUtil.shouldShowName(entityRenderDispatcher, entity);
    }

    // Proxy method override for super.getBlockLightLevel external access.
    @Override
    public int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos pos) {
        return super.getBlockLightLevel(entity, pos);
    }

    public AzEntityAnimator<T> getAnimator() {
        return reusedAzEntityAnimator;
    }

    public float getScaleHeight() {
        return scaleHeight;
    }

    public float getScaleWidth() {
        return scaleWidth;
    }
}
