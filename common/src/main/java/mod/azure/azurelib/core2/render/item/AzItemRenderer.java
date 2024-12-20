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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import mod.azure.azurelib.core2.animation.AzAnimatorAccessor;
import mod.azure.azurelib.core2.animation.impl.AzItemAnimator;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.cache.AzBakedModelCache;
import mod.azure.azurelib.core2.render.AzItemRendererConfig;
import mod.azure.azurelib.core2.render.layer.AzRenderLayer;
import mod.azure.azurelib.core2.render.pipeline.item.AzItemRendererPipeline;

public abstract class AzItemRenderer extends BlockEntityWithoutLevelRenderer {

    private final AzItemRendererPipeline rendererPipeline;

    private final List<AzRenderLayer<ItemStack>> renderLayers;

    private final AzItemRendererConfig config;

    @Nullable
    private AzItemAnimator reusedAzItemAnimator;

    protected AzItemRenderer() {
        this(AzItemRendererConfig.defaultConfig());
    }

    protected AzItemRenderer(AzItemRendererConfig config) {
        this(
            config,
            Minecraft.getInstance().getBlockEntityRenderDispatcher(),
            Minecraft.getInstance().getEntityModels()
        );
    }

    protected AzItemRenderer(
        AzItemRendererConfig config,
        BlockEntityRenderDispatcher dispatcher,
        EntityModelSet modelSet
    ) {
        super(dispatcher, modelSet);
        this.rendererPipeline = new AzItemRendererPipeline(config, this);
        this.renderLayers = new ObjectArrayList<>();
        this.config = config;
    }

    protected abstract @NotNull ResourceLocation getModelLocation(ItemStack item);

    public abstract @NotNull ResourceLocation getTextureLocation(ItemStack item);

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
        var animatable = stack;
        // TODO: What was this used for?
        var renderPerspective = transformType;

        var cachedEntityAnimator = provideAnimator(stack, animatable);
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
                stack,
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
                stack != null && stack.hasFoil()
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
        ItemStack animatable,
        AzBakedModel azBakedModel,
        ItemStack currentItemStack,
        ItemDisplayContext transformType,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        if (config.useEntityGuiLighting()) {
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

    protected @Nullable AzItemAnimator createAnimator() {
        return null;
    }

    protected @Nullable AzBakedModel provideBakedModel(@NotNull ItemStack item) {
        var modelResourceLocation = getModelLocation(item);
        return AzBakedModelCache.getInstance().getNullable(modelResourceLocation);
    }

    protected @Nullable AzItemAnimator provideAnimator(ItemStack itemStack, ItemStack item) {
        // TODO: Instead of caching the entire animator itself, we're going to want to cache the relevant data for
        // the item.
        var accessor = AzAnimatorAccessor.cast(itemStack);
        var cachedItemAnimator = (AzItemAnimator) accessor.getAnimatorOrNull();

        if (cachedItemAnimator == null) {
            // If the cached animator is null, create a new one. We use a separate reference here just for some
            cachedItemAnimator = createAnimator();

            if (cachedItemAnimator != null) {
                // If the new animator we created is not null, then register its controllers.
                cachedItemAnimator.registerControllers(cachedItemAnimator.getAnimationControllerContainer());
                // Also cache the animator so that the next time we fetch the animator, it's ready for us.
                accessor.setAnimator(cachedItemAnimator);
            }
        }

        return cachedItemAnimator;
    }

    /**
     * Returns the list of registered {@link AzRenderLayer GeoRenderLayers} for this renderer
     */
    public List<AzRenderLayer<ItemStack>> getRenderLayers() {
        return renderLayers;
    }

    /**
     * Adds a {@link AzRenderLayer} to this renderer, to be called after the main model is rendered each frame
     */
    public AzItemRenderer addRenderLayer(AzRenderLayer<ItemStack> renderLayer) {
        this.renderLayers.add(renderLayer);
        return this;
    }

    public @Nullable AzItemAnimator getAnimator() {
        return reusedAzItemAnimator;
    }

    public AzItemRendererConfig config() {
        return config;
    }
}
