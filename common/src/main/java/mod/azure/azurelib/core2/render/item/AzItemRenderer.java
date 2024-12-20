package mod.azure.azurelib.core2.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.core2.animation.impl.AzItemAnimator;
import mod.azure.azurelib.core2.render.AzProvider;

public abstract class AzItemRenderer extends BlockEntityWithoutLevelRenderer {

    private final AzItemRendererConfig config;

    private final AzProvider<ItemStack> provider;

    private final AzItemRendererPipeline rendererPipeline;

    @Nullable
    private AzItemAnimator reusedAzItemAnimator;

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
        this.provider = new AzProvider<>(config::createAnimator, config::modelLocation);
        this.config = config;
    }

    @Override
    public void renderByItem(
        ItemStack stack,
        @NotNull ItemDisplayContext transformType,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        // TODO: What was this used for?
        var renderPerspective = transformType;

        var cachedEntityAnimator = (AzItemAnimator) provider.provideAnimator(stack);
        var model = provider.provideBakedModel(stack);

        if (cachedEntityAnimator != null && model != null) {
            cachedEntityAnimator.setActiveModel(model);
        }

        // Point the renderer's current animator reference to the cached entity animator before rendering.
        reusedAzItemAnimator = cachedEntityAnimator;

        if (transformType == ItemDisplayContext.GUI) {
            AzItemGuiRenderUtil.renderInGui(
                config,
                rendererPipeline,
                stack,
                model,
                stack,
                transformType,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
            );
        } else {
            var partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
            var textureLocation = config.textureLocation(stack);
            var renderType = rendererPipeline.context()
                .getDefaultRenderType(stack, textureLocation, bufferSource, partialTick);
            var buffer = ItemRenderer.getFoilBufferDirect(
                bufferSource,
                renderType,
                false,
                // TODO: Why the null check here?
                stack != null && stack.hasFoil()
            );

            rendererPipeline.render(
                poseStack,
                model,
                stack,
                bufferSource,
                renderType,
                buffer,
                0,
                partialTick,
                packedLight
            );
        }
    }

    public @Nullable AzItemAnimator getAnimator() {
        return reusedAzItemAnimator;
    }

    public AzItemRendererConfig config() {
        return config;
    }
}
