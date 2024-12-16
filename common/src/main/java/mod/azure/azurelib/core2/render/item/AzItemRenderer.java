package mod.azure.azurelib.core2.render.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer;
import mod.azure.azurelib.core2.animation.impl.AzItemAnimator;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.cache.AzBakedModelCache;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.impl.AzItemRendererPipeline;

public abstract class AzItemRenderer<T extends Item> extends BlockEntityWithoutLevelRenderer {

    private final AzItemRendererPipeline<T> rendererPipeline;

    private final List<AzRenderLayer<T>> renderLayers;

    protected float scaleWidth = 1;

    protected float scaleHeight = 1;

    protected boolean useEntityGuiLighting = false;

    @Nullable
    private AzItemAnimator<T> reusedAzItemAnimator;

    protected AzItemRenderer() {
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    protected AzItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
        this.rendererPipeline = new AzItemRendererPipeline<>(this);
        this.renderLayers = new ObjectArrayList<>();
    }

    protected abstract @NotNull ResourceLocation getModelLocation(T item);

    public abstract @NotNull ResourceLocation getTextureLocation(T item);

    @Override
    public void renderByItem(
        ItemStack stack,
        @NotNull ItemDisplayContext transformType,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        @SuppressWarnings("unchecked")
        var animatable = (T) stack.getItem();
        var currentItemStack = stack;
        // TODO: What was this used for?
        var renderPerspective = transformType;

        var cachedEntityAnimator = provideAnimator(animatable);
        var azBakedModel = provideBakedModel(animatable);

        if (cachedEntityAnimator != null && azBakedModel != null) {
            cachedEntityAnimator.setActiveModel(azBakedModel);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzItemAnimator = cachedEntityAnimator;

        if (transformType == ItemDisplayContext.GUI) {
            renderInGui(
                animatable,
                azBakedModel,
                currentItemStack,
                transformType,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
            );
        } else {
            var renderType = rendererPipeline.getContext()
                .getDefaultRenderType(
                    animatable,
                    getTextureLocation(animatable),
                    bufferSource,
                    Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
                );
            var buffer = ItemRenderer.getFoilBufferDirect(
                bufferSource,
                renderType,
                false,
                // TODO: Why the null check here?
                currentItemStack != null && currentItemStack.hasFoil()
            );

            rendererPipeline.render(
                poseStack,
                azBakedModel,
                animatable,
                bufferSource,
                renderType,
                buffer,
                0,
                Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(),
                packedLight
            );
        }
    }

    /**
     * Wrapper method to handle rendering the item in a GUI context (defined by
     * {@link net.minecraft.world.item.ItemDisplayContext#GUI} normally).<br>
     * Just includes some additional required transformations and settings.
     */
    protected void renderInGui(
        T animatable,
        AzBakedModel azBakedModel,
        ItemStack currentItemStack,
        ItemDisplayContext transformType,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        if (this.useEntityGuiLighting) {
            Lighting.setupForEntityInInventory();
        } else {
            Lighting.setupForFlatItems();
        }
        MultiBufferSource.BufferSource defaultBufferSource =
            bufferSource instanceof MultiBufferSource.BufferSource bufferSource2
                ? bufferSource2
                : Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
        RenderType renderType = rendererPipeline.getContext()
            .getDefaultRenderType(
                animatable,
                getTextureLocation(animatable),
                defaultBufferSource,
                Minecraft.getInstance().getTimer().getGameTimeDeltaTicks()
            );
        VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(
            bufferSource,
            renderType,
            true,
            currentItemStack != null && currentItemStack.hasFoil()
        );

        poseStack.pushPose();
        rendererPipeline.render(
            poseStack,
            azBakedModel,
            animatable,
            defaultBufferSource,
            renderType,
            buffer,
            0,
            Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(),
            packedLight
        );
        defaultBufferSource.endBatch();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        poseStack.popPose();
    }

    protected @Nullable AzItemAnimator<T> createAnimator() {
        return null;
    }

    protected @Nullable AzBakedModel provideBakedModel(@NotNull T item) {
        var modelResourceLocation = getModelLocation(item);
        return AzBakedModelCache.getInstance().getNullable(modelResourceLocation);
    }

    protected @Nullable AzItemAnimator<T> provideAnimator(T item) {
        // // TODO: Instead of caching the entire animator itself, we're going to want to cache the relevant data for
        // the
        // // item.
        // var accessor = AzAnimatorAccessor.cast(item);
        // // TODO: This won't work for items. Need to use an itemStack + id, instead.
        // var cachedItemAnimator = (AzItemAnimator<T>) accessor.getAnimatorOrNull();
        //
        // if (cachedItemAnimator == null) {
        // // If the cached animator is null, create a new one. We use a separate reference here just for some
        // cachedItemAnimator = createAnimator();
        //
        // if (cachedItemAnimator != null) {
        // // If the new animator we created is not null, then register its controllers.
        // cachedItemAnimator.registerControllers(cachedItemAnimator.getAnimationControllerContainer());
        // // Also cache the animator so that the next time we fetch the animator, it's ready for us.
        // accessor.setAnimator(cachedItemAnimator);
        // }
        // }
        //
        // return cachedItemAnimator;
        // FIXME:
        return null;
    }

    /**
     * Mark this renderer so that it uses an alternate lighting scheme when rendering the item in GUI
     * <p>
     * This can help with improperly lit 3d models
     */
    public AzItemRenderer<T> useAlternateGuiLighting() {
        this.useEntityGuiLighting = true;
        return this;
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public AzItemRenderer<T> withScale(float scale) {
        return withScale(scale, scale);
    }

    /**
     * Sets a scale override for this renderer, telling AzureLib to pre-scale the model
     */
    public AzItemRenderer<T> withScale(float scaleWidth, float scaleHeight) {
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
        return this;
    }

    /**
     * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
     */
    public List<AzRenderLayer<T>> getRenderLayers() {
        return renderLayers;
    }

    /**
     * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public AzItemRenderer<T> addRenderLayer(AzRenderLayer<T> renderLayer) {
        this.renderLayers.add(renderLayer);
        return this;
    }

    public @Nullable AzItemAnimator<T> getAnimator() {
        return reusedAzItemAnimator;
    }

    public float getScaleHeight() {
        return scaleHeight;
    }

    public float getScaleWidth() {
        return scaleWidth;
    }
}
